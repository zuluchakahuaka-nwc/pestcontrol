package com.firebug.common.plan

import org.junit.Assert.assertEquals
import org.junit.Test

class ModelTest {

    @Test
    fun `floors renumbered sequentially after deletion`() {
        val floors = listOf(
            Floor("b", "Этаж 4"),
            Floor("a", "Этаж 1"),
            Floor("c", "Этаж 7")
        )

        val result = renumberFloors(floors)

        assertEquals(listOf("Этаж 1", "Этаж 2", "Этаж 3"), result.map { it.name })
        assertEquals(listOf("b", "a", "c"), result.map { it.id })
    }

    @Test
    fun `renumber keeps floor content`() {
        val floor = Floor("x", "Этаж 9", markers = listOf(PlanMarker("m", "gel", Vec2(1f, 2f))))

        val result = renumberFloors(listOf(floor))

        assertEquals(1, result[0].markers.size)
        assertEquals("m", result[0].markers[0].id)
    }

    @Test
    fun `generated ids are unique`() {
        val ids = (1..100).map { newId() }.toSet()
        assertEquals(100, ids.size)
    }
}
