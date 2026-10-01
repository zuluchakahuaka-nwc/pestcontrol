package com.firebug.pest

import androidx.annotation.StringRes
import com.firebug.common.plan.MarkerType

/** Палитра маркеров плана для дезинсекции / дератизации. */
val PEST_MARKER_TYPES = listOf(
    MarkerType("gel", R.string.mt_gel, "GEL", 0xFF2E7D32),
    MarkerType("glue", R.string.mt_glue, "GLUE", 0xFFF9A825),
    MarkerType("bait", R.string.mt_bait, "BAIT", 0xFF6D4C41),
    MarkerType("hole", R.string.mt_hole, "HOLE", 0xFFC62828),
)

/** Типы маркеров, которые считаются «ловушками» в базе ловушек. */
val TRAP_TYPE_SET = setOf("gel", "glue", "bait")

/** Коды статусов ловушек (хранятся в данных; отображаются локализованно). */
object TrapStatus {
    const val ACTIVE = "active"
    const val REPLACE = "replace"
    const val REMOVED = "removed"

    @StringRes
    fun labelRes(code: String): Int = when (code) {
        REPLACE, "На замене" -> R.string.status_replace
        REMOVED, "Снята" -> R.string.status_removed
        else -> R.string.status_active
    }

    val CODES = listOf(ACTIVE, REPLACE, REMOVED)
}
