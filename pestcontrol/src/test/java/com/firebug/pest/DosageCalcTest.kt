package com.firebug.pest

import org.junit.Assert.assertEquals
import org.junit.Test

class DosageCalcTest {

    @Test
    fun `cockroach gel 100 sqm low infestation is 30 g`() {
        val n = DosageCalculator.calculate(Pest.COCKROACH, 100, Level.LOW)
        assertEquals(30f, n.gelGrams, 0.01f)
        assertEquals(30, n.gelRatePer100)
        assertEquals(20, n.gelPoints) // точек: периметр 40 м / 2
        assertEquals(5, n.glueCount)  // клеевые: 100 м² / 20
        assertEquals(20, n.gluePerSqm)
    }

    @Test
    fun `cockroach gel scales with infestation`() {
        assertEquals(60f, DosageCalculator.calculate(Pest.COCKROACH, 100, Level.MEDIUM).gelGrams, 0.01f)
        assertEquals(100f, DosageCalculator.calculate(Pest.COCKROACH, 100, Level.HIGH).gelGrams, 0.01f)
    }

    @Test
    fun `cockroach solution volume in liters`() {
        assertEquals(5f, DosageCalculator.calculate(Pest.COCKROACH, 100, Level.LOW).solutionLiters, 0.01f)    // 50 мл/м²
        assertEquals(10f, DosageCalculator.calculate(Pest.COCKROACH, 100, Level.HIGH).solutionLiters, 0.01f) // 100 мл/м²
    }

    @Test
    fun `rat bait is 100 g per point`() {
        val n = DosageCalculator.calculate(Pest.RAT, 100, Level.MEDIUM)
        assertEquals(5, n.points) // периметр 40 м / 8
        assertEquals(500f, n.baitGrams, 0.01f)
        assertEquals(100, n.baitPerPoint)
    }

    @Test
    fun `mouse bait is 25 g per point`() {
        val n = DosageCalculator.calculate(Pest.MOUSE, 100, Level.MEDIUM)
        assertEquals(10, n.points) // периметр 40 м / 4
        assertEquals(250f, n.baitGrams, 0.01f)
    }

    @Test
    fun `small areas keep minimum points`() {
        assertEquals(4, DosageCalculator.calculate(Pest.RAT, 1, Level.LOW).points)
        assertEquals(6, DosageCalculator.calculate(Pest.COCKROACH, 1, Level.LOW).gelPoints)
    }

    @Test
    fun `perimeter estimated as square of same area`() {
        assertEquals(40, DosageCalculator.calculate(Pest.RAT, 100, Level.LOW).perimeterM)
        assertEquals(200, DosageCalculator.calculate(Pest.RAT, 2500, Level.LOW).perimeterM)
    }
}
