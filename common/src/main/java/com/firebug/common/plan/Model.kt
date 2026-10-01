package com.firebug.common.plan

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Vec2(val x: Float, val y: Float)

@Serializable
data class Stroke(
    val points: List<Vec2>,
    val color: Long = 0xFF37474F,
    val width: Float = 6f
)

@Serializable
data class PlanLabel(val id: String, val text: String, val position: Vec2)

@Serializable
data class PlanMarker(
    val id: String,
    val type: String,
    val position: Vec2,
    val note: String = "",
    val status: String = "Активна",
    val createdAt: Long = 0L
)

@Serializable
data class Floor(
    val id: String,
    val name: String,
    val strokes: List<Stroke> = emptyList(),
    val labels: List<PlanLabel> = emptyList(),
    val markers: List<PlanMarker> = emptyList()
)

@Serializable
data class FacilityObject(
    val id: String,
    val name: String,
    val address: String = "",
    val createdAt: Long = 0L,
    val floors: List<Floor> = emptyList()
)

/** Палитра маркеров, специфичная для каждого приложения: title — ресурс строки. */
data class MarkerType(
    val type: String,
    val titleRes: Int,
    val badge: String,
    val color: Long
)

fun newId(): String = UUID.randomUUID().toString()

/** Перенумеровывает этажи последовательно после удаления: «Этаж 1», «Этаж 2», … */
fun renumberFloors(floors: List<Floor>): List<Floor> =
    floors.mapIndexed { i, f -> f.copy(name = "Этаж ${i + 1}") }
