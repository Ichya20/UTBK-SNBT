package com.aknaf.utbk_snbt.utils

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

data class StudySchedule(
    val id: Long,
    val title: String,
    val note: String,
    val dayOfWeek: Int,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean
)

object ScheduleStorage {

    private const val PREFS_NAME = "study_schedule_preferences"
    private const val KEY_SCHEDULES = "saved_schedules"

    fun getAll(context: Context): List<StudySchedule> {
        val preferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val rawJson = preferences.getString(KEY_SCHEDULES, "[]") ?: "[]"

        return try {
            val jsonArray = JSONArray(rawJson)
            val result = mutableListOf<StudySchedule>()

            for (index in 0 until jsonArray.length()) {
                val item = jsonArray.optJSONObject(index) ?: continue

                result.add(
                    StudySchedule(
                        id = item.optLong("id", System.currentTimeMillis() + index),
                        title = item.optString("title", "Jadwal Belajar"),
                        note = item.optString("note", ""),
                        dayOfWeek = item.optInt(
                            "dayOfWeek",
                            Calendar.MONDAY
                        ),
                        hour = item.optInt("hour", 18),
                        minute = item.optInt("minute", 0),
                        enabled = item.optBoolean("enabled", true)
                    )
                )
            }

            result.sortedWith(
                compareBy(
                    { dayOrder(it.dayOfWeek) },
                    { it.hour },
                    { it.minute },
                    { it.title.lowercase() }
                )
            )
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveAll(
        context: Context,
        schedules: List<StudySchedule>
    ) {
        val jsonArray = JSONArray()

        schedules.forEach { schedule ->
            jsonArray.put(
                JSONObject().apply {
                    put("id", schedule.id)
                    put("title", schedule.title)
                    put("note", schedule.note)
                    put("dayOfWeek", schedule.dayOfWeek)
                    put("hour", schedule.hour)
                    put("minute", schedule.minute)
                    put("enabled", schedule.enabled)
                }
            )
        }

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .putString(KEY_SCHEDULES, jsonArray.toString())
            .apply()
    }

    private fun dayOrder(day: Int): Int {
        return when (day) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 8
        }
    }
}
