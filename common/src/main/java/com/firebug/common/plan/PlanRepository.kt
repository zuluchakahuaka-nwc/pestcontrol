package com.firebug.common.plan

import android.content.Context
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * Локальное хранилище объектов и их планов.
 * Данные сериализуются в JSON в filesDir приложения (plan_objects.json).
 */
class PlanRepository(context: Context) {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    private val file = File(context.filesDir, "plan_objects.json")
    private val serializer = ListSerializer(FacilityObject.serializer())

    private val _objects = MutableStateFlow(load())
    val objects: StateFlow<List<FacilityObject>> = _objects

    private fun load(): List<FacilityObject> =
        runCatching { json.decodeFromString(serializer, file.readText()) }.getOrDefault(emptyList())

    private fun persist() {
        runCatching { file.writeText(json.encodeToString(serializer, _objects.value)) }
    }

    fun addObject(name: String, address: String) {
        val obj = FacilityObject(
            id = newId(),
            name = name,
            address = address,
            createdAt = System.currentTimeMillis(),
            floors = listOf(Floor(newId(), "Этаж 1"))
        )
        // Новый объект — первым в списке (иначе на длинном списке его не видно)
        _objects.update { listOf(obj) + it }
        persist()
    }

    fun updateObject(obj: FacilityObject) {
        _objects.update { list -> list.map { if (it.id == obj.id) obj else it } }
        persist()
    }

    fun deleteObject(id: String) {
        _objects.update { list -> list.filter { it.id != id } }
        persist()
    }
}
