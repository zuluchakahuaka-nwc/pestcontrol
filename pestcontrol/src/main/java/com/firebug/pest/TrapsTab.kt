package com.firebug.pest

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.firebug.common.plan.Floor
import com.firebug.common.plan.FacilityObject
import com.firebug.common.plan.PlanMarker
import com.firebug.common.plan.PlanRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class TrapEntry(
    val obj: FacilityObject,
    val floor: Floor,
    val trap: PlanMarker
) {
    val floorOrdinal: Int get() = obj.floors.indexOfFirst { it.id == floor.id } + 1
}

@Composable
fun TrapsTab(objects: List<FacilityObject>, repo: PlanRepository) {
    val all: List<TrapEntry> = objects.flatMap { o ->
        o.floors.flatMap { f ->
            f.markers.filter { it.type in TRAP_TYPE_SET }.map { TrapEntry(o, f, it) }
        }
    }
    var typeFilter by remember { mutableStateOf<String?>(null) }
    var statusFilter by remember { mutableStateOf<String?>(null) }
    var edit by remember { mutableStateOf<TrapEntry?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.traps_title), style = MaterialTheme.typography.titleLarge)
        Text(
            stringResource(R.string.traps_total, all.size),
            style = MaterialTheme.typography.bodySmall
        )

        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = typeFilter == null,
                onClick = { typeFilter = null },
                label = { Text(stringResource(R.string.filter_all_types)) }
            )
            PEST_MARKER_TYPES.filter { it.type in TRAP_TYPE_SET }.forEach { mt ->
                FilterChip(
                    selected = typeFilter == mt.type,
                    onClick = { typeFilter = if (typeFilter == mt.type) null else mt.type },
                    label = { Text(stringResource(mt.titleRes)) }
                )
            }
        }

        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = statusFilter == null,
                onClick = { statusFilter = null },
                label = { Text(stringResource(R.string.filter_any_status)) }
            )
            TrapStatus.CODES.forEach { code ->
                FilterChip(
                    selected = statusFilter == code,
                    onClick = { statusFilter = if (statusFilter == code) null else code },
                    label = { Text(stringResource(TrapStatus.labelRes(code))) }
                )
            }
        }

        if (all.isEmpty()) {
            Text(stringResource(R.string.traps_empty), color = Color.Gray)
        } else {
            val filtered = all.filter { e ->
                (typeFilter == null || e.trap.type == typeFilter) &&
                    (statusFilter == null || e.trap.status == statusFilter)
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered, key = { it.trap.id }) { e ->
                    val mt = PEST_MARKER_TYPES.firstOrNull { it.type == e.trap.type }
                    Card(modifier = Modifier.fillMaxWidth(), onClick = { edit = e }) {
                        Column(Modifier.padding(12.dp)) {
                            Row {
                                Text(
                                    stringResource(mt?.titleRes ?: R.string.mt_gel),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    stringResource(TrapStatus.labelRes(e.trap.status)),
                                    color = if (TrapStatus.labelRes(e.trap.status) == R.string.status_active) {
                                        Color(0xFF2E7D32)
                                    } else {
                                        Color(0xFF8D6E63)
                                    }
                                )
                            }
                            Text(
                                "${e.obj.name} · ${stringResource(com.firebug.common.R.string.floor_name, e.floorOrdinal)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (e.trap.note.isNotBlank()) {
                                Text("«${e.trap.note}»", style = MaterialTheme.typography.bodySmall)
                            }
                            if (e.trap.createdAt > 0) {
                                Text(
                                    stringResource(
                                        R.string.installed,
                                        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(e.trap.createdAt))
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    edit?.let { e ->
        var note by remember(e.trap.id) { mutableStateOf(e.trap.note) }
        var status by remember(e.trap.id) { mutableStateOf(e.trap.status) }
        AlertDialog(
            onDismissRequest = { edit = null },
            title = {
                Text(stringResource(PEST_MARKER_TYPES.firstOrNull { it.type == e.trap.type }?.titleRes ?: R.string.mt_gel))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TrapStatus.CODES.forEach { code ->
                        FilterChip(
                            selected = status == code,
                            onClick = { status = code },
                            label = { Text(stringResource(TrapStatus.labelRes(code))) }
                        )
                    }
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text(stringResource(R.string.dlg_note)) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val newMarker = e.trap.copy(note = note.trim(), status = status)
                    repo.updateObject(
                        e.obj.copy(
                            floors = e.obj.floors.map { f ->
                                if (f.id == e.floor.id) {
                                    f.copy(markers = f.markers.map { m -> if (m.id == e.trap.id) newMarker else m })
                                } else f
                            }
                        )
                    )
                    edit = null
                }) { Text(stringResource(com.firebug.common.R.string.btn_save)) }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        repo.updateObject(
                            e.obj.copy(
                                floors = e.obj.floors.map { f ->
                                    if (f.id == e.floor.id) {
                                        f.copy(markers = f.markers.filter { m -> m.id != e.trap.id })
                                    } else f
                                }
                            )
                        )
                        edit = null
                    }) { Text(stringResource(com.firebug.common.R.string.btn_delete), color = Color(0xFFC62828)) }
                    TextButton(onClick = { edit = null }) { Text(stringResource(com.firebug.common.R.string.btn_cancel)) }
                }
            }
        )
    }
}
