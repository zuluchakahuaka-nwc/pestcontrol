package com.firebug.pest

import androidx.annotation.StringRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.math.sqrt

enum class Pest(@StringRes val res: Int) {
    COCKROACH(R.string.pest_cockroach),
    RAT(R.string.pest_rat),
    MOUSE(R.string.pest_mouse)
}

enum class Level(@StringRes val res: Int) {
    LOW(R.string.level_low),
    MEDIUM(R.string.level_medium),
    HIGH(R.string.level_high)
}

/** Чистые числа расчёта — без строк, тестируются на JVM. */
data class DosageNumbers(
    val perimeterM: Int = 0,
    val gelPoints: Int = 0,
    val gelGrams: Float = 0f,
    val gelRatePer100: Int = 0,
    val solutionLiters: Float = 0f,
    val solutionMlPerSqm: Int = 0,
    val glueCount: Int = 0,
    val gluePerSqm: Int = 0,
    val points: Int = 0,
    val baitGrams: Float = 0f,
    val baitPerPoint: Int = 0
)

/** Ориентировочный расчёт средств: периметр оценён как периметр квадрата той же площади. */
object DosageCalculator {
    fun calculate(pest: Pest, areaSqm: Int, level: Level): DosageNumbers {
        val a = areaSqm.coerceAtLeast(1).toFloat()
        val perimeter = 4f * sqrt(a)
        return when (pest) {
            Pest.COCKROACH -> {
                val gelRate = when (level) { Level.LOW -> 0.3f; Level.MEDIUM -> 0.6f; Level.HIGH -> 1.0f }
                val ml = when (level) { Level.LOW -> 50; Level.MEDIUM -> 75; Level.HIGH -> 100 }
                DosageNumbers(
                    perimeterM = perimeter.roundToInt(),
                    gelPoints = maxOf(6, ceil(perimeter / 2f).toInt()),
                    gelGrams = a * gelRate,
                    gelRatePer100 = (gelRate * 100).roundToInt(),
                    solutionLiters = a * ml / 1000f,
                    solutionMlPerSqm = ml,
                    glueCount = ceil(a / if (level == Level.LOW) 20f else 10f).toInt(),
                    gluePerSqm = if (level == Level.LOW) 20 else 10
                )
            }
            Pest.RAT -> {
                val pts = maxOf(4, ceil(perimeter / 8f).toInt())
                DosageNumbers(
                    perimeterM = perimeter.roundToInt(),
                    points = pts,
                    baitGrams = pts * 100f,
                    baitPerPoint = 100
                )
            }
            Pest.MOUSE -> {
                val pts = maxOf(4, ceil(perimeter / 4f).toInt())
                DosageNumbers(
                    perimeterM = perimeter.roundToInt(),
                    points = pts,
                    baitGrams = pts * 25f,
                    baitPerPoint = 25
                )
            }
        }
    }
}

private data class RowUi(
    @StringRes val titleRes: Int,
    val value: String,
    @StringRes val hintRes: Int,
    val hintArgs: List<Any> = emptyList()
)

@Composable
fun DosageTab() {
    var pest by remember { mutableStateOf(Pest.COCKROACH) }
    var level by remember { mutableStateOf(Level.MEDIUM) }
    var areaText by remember { mutableStateOf("") }
    val area = areaText.toIntOrNull() ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(stringResource(R.string.tab_dosage), style = MaterialTheme.typography.titleLarge)

        Text(stringResource(R.string.lbl_pest), style = MaterialTheme.typography.titleSmall)
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Pest.entries.forEach { p ->
                FilterChip(selected = pest == p, onClick = { pest = p }, label = { Text(stringResource(p.res)) })
            }
        }

        Text(stringResource(R.string.lbl_level), style = MaterialTheme.typography.titleSmall)
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Level.entries.forEach { l ->
                FilterChip(selected = level == l, onClick = { level = l }, label = { Text(stringResource(l.res)) })
            }
        }

        OutlinedTextField(
            value = areaText,
            onValueChange = { areaText = it.filter { c -> c.isDigit() }.take(6) },
            label = { Text(stringResource(R.string.field_area)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        if (area > 0) {
            val n = DosageCalculator.calculate(pest, area, level)
            val levelTitle = stringResource(level.res)
            val rows: List<RowUi> = when (pest) {
                Pest.COCKROACH -> listOf(
                    RowUi(
                        R.string.row_gel_title,
                        weightString(n.gelGrams),
                        R.string.hint_gel,
                        listOf(n.gelRatePer100, levelTitle)
                    ),
                    RowUi(R.string.row_points_gel, n.gelPoints.toString(), R.string.hint_points_gel, listOf(n.perimeterM)),
                    RowUi(
                        R.string.row_solution,
                        stringResource(R.string.unit_l, "%.1f".format(n.solutionLiters)),
                        R.string.hint_solution,
                        listOf(n.solutionMlPerSqm)
                    ),
                    RowUi(R.string.row_glue, stringResource(R.string.unit_pcs, n.glueCount), R.string.hint_glue, listOf(n.gluePerSqm))
                )
                Pest.RAT -> listOf(
                    RowUi(R.string.row_points, stringResource(R.string.unit_pcs, n.points), R.string.hint_points_rat),
                    RowUi(R.string.row_bait_rat, weightString(n.baitGrams), R.string.hint_bait_rat),
                    RowUi(R.string.row_containers_rat, stringResource(R.string.unit_pcs, n.points), R.string.hint_containers_rat)
                )
                Pest.MOUSE -> listOf(
                    RowUi(R.string.row_points, stringResource(R.string.unit_pcs, n.points), R.string.hint_points_mouse),
                    RowUi(R.string.row_bait_mouse, weightString(n.baitGrams), R.string.hint_bait_mouse),
                    RowUi(R.string.row_containers_mouse, stringResource(R.string.unit_pcs, n.points), R.string.hint_containers_mouse)
                )
            }
            rows.forEach { row ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(stringResource(row.titleRes), style = MaterialTheme.typography.titleSmall)
                        Text(row.value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(
                            stringResource(row.hintRes, *row.hintArgs.toTypedArray()),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            Text(stringResource(R.string.area_prompt), color = Color.Gray)
        }

        Text(
            stringResource(R.string.dosage_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun weightString(grams: Float): String =
    if (grams >= 1000f) stringResource(R.string.unit_kg, "%.1f".format(grams / 1000f))
    else stringResource(R.string.unit_g, grams.roundToInt())
