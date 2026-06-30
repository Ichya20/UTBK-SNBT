package com.aknaf.utbk_snbt.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object AlarmScheduler {

    fun scheduleSavedTasks(context: Context) {
        cancelLegacyFixedTasks(context)

        ScheduleStorage.getAll(context).forEach { schedule ->
            if (schedule.enabled) {
                scheduleTask(context, schedule)
            } else {
                cancelTask(context, schedule)
            }
        }
    }

    fun scheduleTask(
        context: Context,
        schedule: StudySchedule
    ) {
        cancelTask(context, schedule)

        if (!schedule.enabled) return

        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        val intent = Intent(
            context,
            StudyReminderReceiver::class.java
        ).apply {
            putExtra("taskId", schedule.id)
            putExtra("title", schedule.title)
            putExtra(
                "message",
                schedule.note.ifBlank {
                    "Waktunya belajar sesuai jadwalmu."
                }
            )
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode(schedule.id),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
        )

        val now = Calendar.getInstance()

        val nextTrigger = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, schedule.dayOfWeek)
            set(Calendar.HOUR_OF_DAY, schedule.hour)
            set(Calendar.MINUTE, schedule.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (!after(now)) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            nextTrigger.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
    }

    fun cancelTask(
        context: Context,
        schedule: StudySchedule
    ) {
        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode(schedule.id),
            Intent(
                context,
                StudyReminderReceiver::class.java
            ),
            PendingIntent.FLAG_NO_CREATE or
                PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun requestCode(id: Long): Int {
        return (id xor (id ushr 32)).toInt()
    }

    /*
     * Membatalkan alarm bawaan versi lama agar jadwal tetap
     * Matematika/Fisika/Kimia dan lainnya tidak terus muncul
     * setelah aplikasi diperbarui.
     */
    private fun cancelLegacyFixedTasks(context: Context) {
        legacyTasks.forEach { task ->
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                task.hashCode(),
                Intent(
                    context,
                    StudyReminderReceiver::class.java
                ),
                PendingIntent.FLAG_NO_CREATE or
                    PendingIntent.FLAG_IMMUTABLE
            )

            if (pendingIntent != null) {
                val alarmManager = context.getSystemService(
                    Context.ALARM_SERVICE
                ) as AlarmManager

                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }
    }

    private data class LegacyTask(
        val day: Int,
        val hour: Int,
        val min: Int,
        val title: String,
        val msg: String
    )

    private val legacyTasks = listOf(
        LegacyTask(Calendar.MONDAY, 8, 0, "Matematika", "Materi: Aljabar"),
        LegacyTask(Calendar.MONDAY, 10, 30, "Fisika", "Materi: Dinamika"),
        LegacyTask(
            Calendar.MONDAY,
            13,
            30,
            "Latihan Soal Matematika",
            "Ayo asah kemampuan Aljabarmu!"
        ),
        LegacyTask(Calendar.TUESDAY, 8, 0, "Kimia", "Materi: Reaksi Kimia"),
        LegacyTask(Calendar.TUESDAY, 10, 30, "Biologi", "Materi: Ekologi"),
        LegacyTask(
            Calendar.TUESDAY,
            13,
            30,
            "Latihan Soal Kimia",
            "Review materi Reaksi Kimia."
        ),
        LegacyTask(
            Calendar.WEDNESDAY,
            8,
            0,
            "Bahasa Indonesia",
            "Materi: Esai dan Karangan"
        ),
        LegacyTask(
            Calendar.WEDNESDAY,
            10,
            30,
            "Latihan Soal Biologi",
            "Bahas soal materi Ekologi."
        ),
        LegacyTask(
            Calendar.WEDNESDAY,
            13,
            30,
            "Motivasi",
            "Bacaan Inspiratif: Tetap Semangat!"
        ),
        LegacyTask(
            Calendar.THURSDAY,
            8,
            0,
            "Matematika",
            "Materi: Trigonometri"
        ),
        LegacyTask(Calendar.THURSDAY, 10, 30, "Fisika", "Materi: Optika"),
        LegacyTask(
            Calendar.THURSDAY,
            13,
            30,
            "Tips Ujian",
            "Tips Mengerjakan Soal Ujian."
        ),
        LegacyTask(Calendar.FRIDAY, 8, 0, "Kimia", "Materi: Struktur Atom"),
        LegacyTask(Calendar.FRIDAY, 10, 30, "Biologi", "Materi: Genetika"),
        LegacyTask(
            Calendar.FRIDAY,
            13,
            30,
            "Latihan Bahasa Indonesia",
            "Review materi Esai."
        ),
        LegacyTask(
            Calendar.SATURDAY,
            8,
            0,
            "Ulangan Mingguan",
            "Kombinasi Materi Sepekan."
        ),
        LegacyTask(
            Calendar.SATURDAY,
            10,
            30,
            "Diskusi Forum",
            "Bahas Soal atau Materi Sulit."
        ),
        LegacyTask(
            Calendar.SATURDAY,
            13,
            30,
            "Evaluasi",
            "Review Statistik Kemajuan."
        ),
        LegacyTask(
            Calendar.SUNDAY,
            10,
            0,
            "Evaluasi Pekan",
            "Istirahat dan Rencana Pekan Depan."
        )
    )
}
