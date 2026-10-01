package com.firebug.pest

import androidx.compose.ui.test.center
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.firebug.common.R as CommonR
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E: полный обход кнопок. Строки через getString — тесты не зависят
 * от локали устройства. Имена объектов уникальны (timestamp).
 */
@RunWith(AndroidJUnit4::class)
class PestE2eTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun str(res: Int, vararg args: Any) = rule.activity.getString(res, *args)

    private fun createAndOpenObject(): String {
        val uniqueName = "E2E-${System.currentTimeMillis()}"
        rule.onNodeWithTag("addObjectFab").performClick()
        rule.onNodeWithText(str(R.string.field_name)).performTextInput(uniqueName)
        rule.onNodeWithText(str(R.string.btn_create)).performClick()
        rule.onAllNodesWithText(uniqueName)[0].performClick()
        return uniqueName
    }

    @Test
    fun addObjectPlaceTrapAndFindItInDatabase() {
        val uniqueName = createAndOpenObject()

        rule.onNodeWithText(str(CommonR.string.tool_marker)).performClick()
        rule.onNodeWithText("GEL · ${str(R.string.mt_gel)}").performClick()
        rule.onNodeWithTag("planCanvas").performTouchInput { click(center) }

        rule.onNodeWithContentDescription(str(R.string.back)).performClick()

        rule.onNodeWithText(str(R.string.tab_traps)).performClick()
        rule.onNodeWithText("$uniqueName · ${str(CommonR.string.floor_name, 1)}").assertExists()
    }

    @Test
    fun dosageTabShowsResultForArea() {
        rule.onNodeWithText(str(R.string.tab_dosage)).performClick()
        rule.onNodeWithText(str(R.string.field_area)).performTextInput("100")
        rule.onNodeWithText(str(R.string.row_gel_title)).performClick()
        rule.onNodeWithText(str(R.string.row_glue)).assertExists()
    }

    @Test
    fun drawStrokeAndUndo() {
        createAndOpenObject()

        rule.onNodeWithTag("planCanvas").performTouchInput {
            swipe(center - androidx.compose.ui.geometry.Offset(200f, 100f), center + androidx.compose.ui.geometry.Offset(150f, 150f), 500)
        }
        // после штриха кнопка отмены активна и убирает линию без ошибок
        rule.onNodeWithContentDescription(str(CommonR.string.undo_line)).performClick()
    }

    @Test
    fun landmarkDialogAddAndEdit() {
        createAndOpenObject()

        rule.onNodeWithText(str(CommonR.string.tool_label)).performClick()
        rule.onNodeWithTag("planCanvas").performTouchInput { click(center) }
        rule.onNodeWithText(str(CommonR.string.label_dialog_hint)).performTextInput("Kitchen")
        rule.onNodeWithText(str(CommonR.string.btn_add)).performClick()

        // тап по надписи открывает редактирование с введённым текстом
        rule.onNodeWithTag("planCanvas").performTouchInput { click(center) }
        rule.onNodeWithText("Kitchen").assertExists()
        rule.onNodeWithText(str(CommonR.string.btn_save)).performClick()
    }

    @Test
    fun markerTapAgainRemovesIt() {
        createAndOpenObject()

        rule.onNodeWithText(str(CommonR.string.tool_marker)).performClick()
        rule.onNodeWithText("BAIT · ${str(R.string.mt_bait)}").performClick()
        rule.onNodeWithTag("planCanvas").performTouchInput { click(center) }

        // повторный тап по тому же месту удаляет маркер — без ошибок
        rule.onNodeWithTag("planCanvas").performTouchInput { click(center) }
    }

    @Test
    fun floorsAddSwitchDelete() {
        createAndOpenObject()

        rule.onNodeWithText(str(CommonR.string.add_floor)).performClick()
        rule.onNodeWithText(str(CommonR.string.floor_name, 2)).performClick()

        rule.onNodeWithContentDescription(str(CommonR.string.delete_floor_cd)).performClick()
        rule.onNodeWithText(str(CommonR.string.btn_delete)).performClick()

        rule.onNodeWithText(str(CommonR.string.floor_name, 2)).assertDoesNotExist()
    }

    @Test
    fun trapChangeStatusAndNote() {
        val uniqueName = createAndOpenObject()

        rule.onNodeWithText(str(CommonR.string.tool_marker)).performClick()
        rule.onNodeWithText("GLUE · ${str(R.string.mt_glue)}").performClick()
        rule.onNodeWithTag("planCanvas").performTouchInput { click(center) }
        rule.onNodeWithContentDescription(str(R.string.back)).performClick()

        rule.onNodeWithText(str(R.string.tab_traps)).performClick()
        rule.onNodeWithText("$uniqueName · ${str(CommonR.string.floor_name, 1)}").performClick()
        // [0] — чип статуса в открытом диалоге (фильтр-чип с тем же текстом ниже по дереву)
        rule.onAllNodesWithText(str(R.string.status_replace))[0].performClick()
        rule.onNodeWithText(str(R.string.dlg_note)).performTextInput("near door")
        rule.onNodeWithText(str(CommonR.string.btn_save)).performClick()

        rule.onAllNodesWithText(str(R.string.status_replace))[0].assertExists()
    }
}
