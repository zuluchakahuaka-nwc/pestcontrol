package com.firebug.common.plan

import androidx.compose.foundation.Canvas
import com.firebug.common.R
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke as DrawStroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class Tool { DRAW, LABEL, MARKER }

/**
 * Редактор плана объекта: рисование пальцем, подписи ориентиров,
 * расстановка маркеров (ловушки / огнетушители и т.п.), поддержка этажей.
 * Имя этажа не хранится в данных — отображается по позиции (локализуется).
 */
@Composable
fun FloorPlanEditor(
    obj: FacilityObject,
    markerTypes: List<MarkerType>,
    onObjectChange: (FacilityObject) -> Unit
) {
    var floorIndex by remember(obj.id) { mutableIntStateOf(0) }
    val idx = floorIndex.coerceIn(0, obj.floors.lastIndex)
    val floor = obj.floors[idx]

    fun replaceFloor(transform: (Floor) -> Floor) {
        onObjectChange(obj.copy(floors = obj.floors.mapIndexed { i, f -> if (i == idx) transform(f) else f }))
    }

    var tool by remember { mutableStateOf(Tool.DRAW) }
    var selectedMarker by remember { mutableStateOf(markerTypes.firstOrNull()?.type.orEmpty()) }
    var currentStroke by remember { mutableStateOf<Stroke?>(null) }
    var newLabelPos by remember { mutableStateOf<Vec2?>(null) }
    var editLabel by remember { mutableStateOf<PlanLabel?>(null) }
    var confirmDeleteFloor by remember { mutableStateOf(false) }
    val measurer = rememberTextMeasurer()

    Column(Modifier.fillMaxSize()) {

        // Выбор этажа + действия
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            ScrollableTabRow(
                selectedTabIndex = idx,
                modifier = Modifier.weight(1f),
                edgePadding = 8.dp
            ) {
                obj.floors.forEachIndexed { i, _ ->
                    Tab(
                        selected = i == idx,
                        onClick = { floorIndex = i },
                        text = { Text(stringResource(R.string.floor_name, i + 1), maxLines = 1) }
                    )
                }
                Tab(
                    selected = false,
                    onClick = {
                        val newSize = obj.floors.size + 1
                        onObjectChange(obj.copy(floors = obj.floors + Floor(newId(), "")))
                        floorIndex = newSize - 1
                    },
                    text = { Text(stringResource(R.string.add_floor), maxLines = 1) }
                )
            }
            IconButton(
                onClick = {
                    currentStroke = null
                    replaceFloor { it.copy(strokes = it.strokes.dropLast(1)) }
                },
                enabled = floor.strokes.isNotEmpty()
            ) {
                Icon(Icons.Filled.Undo, contentDescription = stringResource(R.string.undo_line))
            }
            IconButton(
                onClick = { confirmDeleteFloor = true },
                enabled = obj.floors.size > 1
            ) {
                Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_floor_cd))
            }
        }

        // Инструменты
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = tool == Tool.DRAW,
                onClick = { tool = Tool.DRAW },
                label = { Text(stringResource(R.string.tool_draw)) }
            )
            FilterChip(
                selected = tool == Tool.LABEL,
                onClick = { tool = Tool.LABEL },
                label = { Text(stringResource(R.string.tool_label)) }
            )
            FilterChip(
                selected = tool == Tool.MARKER,
                onClick = { tool = Tool.MARKER },
                label = { Text(stringResource(R.string.tool_marker)) }
            )
        }

        // Палитра маркеров
        if (tool == Tool.MARKER) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                markerTypes.forEach { mt ->
                    FilterChip(
                        selected = selectedMarker == mt.type,
                        onClick = { selectedMarker = mt.type },
                        label = { Text("${mt.badge} · ${stringResource(mt.titleRes)}") }
                    )
                }
            }
        }

        // Холст
        val allStrokes = floor.strokes + listOfNotNull(currentStroke)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFFAFAFA))
                .testTag("planCanvas")
                .pointerInput(tool, floor) {
                    detectDragGestures(
                        onDragStart = { off ->
                            if (tool == Tool.DRAW) currentStroke = Stroke(listOf(Vec2(off.x, off.y)))
                        },
                        onDragEnd = {
                            currentStroke?.let { s ->
                                if (s.points.size > 1) replaceFloor { it.copy(strokes = it.strokes + s) }
                            }
                            currentStroke = null
                        },
                        onDragCancel = { currentStroke = null },
                        onDrag = { change, _ ->
                            if (tool == Tool.DRAW) {
                                currentStroke = currentStroke?.let { s ->
                                    s.copy(points = s.points + Vec2(change.position.x, change.position.y))
                                }
                            }
                        }
                    )
                }
                .pointerInput(tool, selectedMarker, floor) {
                    detectTapGestures { off ->
                        val pos = Vec2(off.x, off.y)
                        when (tool) {
                            Tool.DRAW -> {}
                            Tool.LABEL -> {
                                val r = 48.dp.toPx()
                                val hit = floor.labels.lastOrNull { l ->
                                    val dx = l.position.x - pos.x
                                    val dy = l.position.y - pos.y
                                    dx * dx + dy * dy <= r * r
                                }
                                if (hit != null) editLabel = hit else newLabelPos = pos
                            }
                            Tool.MARKER -> {
                                val r = 44.dp.toPx()
                                val hit = floor.markers.lastOrNull { m ->
                                    val dx = m.position.x - pos.x
                                    val dy = m.position.y - pos.y
                                    dx * dx + dy * dy <= r * r
                                }
                                if (hit != null) {
                                    replaceFloor { f -> f.copy(markers = f.markers.filter { it.id != hit.id }) }
                                } else if (selectedMarker.isNotEmpty()) {
                                    replaceFloor { f ->
                                        f.copy(
                                            markers = f.markers + PlanMarker(
                                                id = newId(),
                                                type = selectedMarker,
                                                position = pos,
                                                createdAt = System.currentTimeMillis()
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
        ) {
            // Сетка-фон
            val grid = 48.dp.toPx()
            var p = grid
            while (p < size.width) {
                drawLine(Color(0xFFEEEEEE), Offset(p, 0f), Offset(p, size.height), 1.5f)
                p += grid
            }
            p = grid
            while (p < size.height) {
                drawLine(Color(0xFFEEEEEE), Offset(0f, p), Offset(size.width, p), 1.5f)
                p += grid
            }

            // Штрихи (нарисованные стены/контуры)
            allStrokes.forEach { s ->
                if (s.points.size > 1) {
                    val path = Path().apply {
                        moveTo(s.points.first().x, s.points.first().y)
                        s.points.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                    drawPath(path, Color(s.color), style = DrawStroke(width = s.width, cap = StrokeCap.Round))
                }
            }

            // Маркеры
            floor.markers.forEach { m ->
                val mt = markerTypes.firstOrNull { it.type == m.type }
                val color = Color(mt?.color ?: 0xFF546E7A)
                val c = Offset(m.position.x, m.position.y)
                drawCircle(color, radius = 20.dp.toPx(), center = c)
                drawCircle(Color.White, radius = 20.dp.toPx(), center = c, style = DrawStroke(width = 2.dp.toPx()))
                val layout = measurer.measure(
                    mt?.badge ?: "?",
                    TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )
                drawText(layout, topLeft = Offset(c.x - layout.size.width / 2f, c.y - layout.size.height / 2f))
            }

            // Подписи ориентиров
            floor.labels.forEach { l ->
                val layout = measurer.measure(
                    l.text,
                    TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0D47A1))
                )
                drawText(layout, topLeft = Offset(l.position.x, l.position.y))
            }
        }

        // Легенда
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            markerTypes.forEach { mt ->
                Text("${mt.badge} — ${stringResource(mt.titleRes)}", fontSize = 12.sp, color = Color(0xFF616161))
            }
        }
    }

    // Диалог новой подписи
    newLabelPos?.let { pos ->
        var txt by remember(pos) { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { newLabelPos = null },
            title = { Text(stringResource(R.string.label_dialog_title)) },
            text = {
                OutlinedTextField(
                    value = txt,
                    onValueChange = { txt = it },
                    label = { Text(stringResource(R.string.label_dialog_hint)) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (txt.isNotBlank()) {
                        replaceFloor { it.copy(labels = it.labels + PlanLabel(newId(), txt.trim(), pos)) }
                    }
                    newLabelPos = null
                }) { Text(stringResource(R.string.btn_add)) }
            },
            dismissButton = {
                TextButton(onClick = { newLabelPos = null }) { Text(stringResource(R.string.btn_cancel)) }
            }
        )
    }

    // Диалог редактирования подписи
    editLabel?.let { lbl ->
        var txt by remember(lbl.id) { mutableStateOf(lbl.text) }
        AlertDialog(
            onDismissRequest = { editLabel = null },
            title = { Text(stringResource(R.string.edit_label_title)) },
            text = {
                OutlinedTextField(value = txt, onValueChange = { txt = it }, singleLine = true)
            },
            confirmButton = {
                TextButton(onClick = {
                    if (txt.isNotBlank()) {
                        replaceFloor { it.copy(labels = it.labels.map { l -> if (l.id == lbl.id) l.copy(text = txt.trim()) else l }) }
                    }
                    editLabel = null
                }) { Text(stringResource(R.string.btn_save)) }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        replaceFloor { it.copy(labels = it.labels.filter { l -> l.id != lbl.id }) }
                        editLabel = null
                    }) { Text(stringResource(R.string.btn_delete), color = Color(0xFFC62828)) }
                    TextButton(onClick = { editLabel = null }) { Text(stringResource(R.string.btn_cancel)) }
                }
            }
        )
    }

    // Подтверждение удаления этажа
    if (confirmDeleteFloor) {
        AlertDialog(
            onDismissRequest = { confirmDeleteFloor = false },
            title = { Text(stringResource(R.string.delete_floor_title)) },
            text = { Text(stringResource(R.string.delete_floor_text, stringResource(R.string.floor_name, idx + 1))) },
            confirmButton = {
                TextButton(onClick = {
                    onObjectChange(obj.copy(floors = obj.floors.filterIndexed { i, _ -> i != idx }))
                    floorIndex = 0
                    confirmDeleteFloor = false
                }) { Text(stringResource(R.string.btn_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteFloor = false }) { Text(stringResource(R.string.btn_cancel)) }
            }
        )
    }
}
