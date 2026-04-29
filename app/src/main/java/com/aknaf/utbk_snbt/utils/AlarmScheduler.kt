package com.aknaf.utbk_snbt.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

object AlarmScheduler {

    data class Task(val day: Int, val hour: Int, val min: Int, val title: String, val msg: String)

    val listJadwal = listOf(
        // SENIN
        Task(Calendar.MONDAY, 8, 0, "Matematika", "Materi: Aljabar"),
        Task(Calendar.MONDAY, 10, 30, "Fisika", "Materi: Dinamika"),
        Task(Calendar.MONDAY, 13, 30, "Latihan Soal Matematika", "Ayo asah kemampuan Aljabarmu!"),
        // SELASA
        Task(Calendar.TUESDAY, 8, 0, "Kimia", "Materi: Reaksi Kimia"),
        Task(Calendar.TUESDAY, 10, 30, "Biologi", "Materi: Ekologi"),
        Task(Calendar.TUESDAY, 13, 30, "Latihan Soal Kimia", "Review materi Reaksi Kimia."),
        // RABU
        Task(Calendar.WEDNESDAY, 8, 0, "Bahasa Indonesia", "Materi: Esai dan Karangan"),
        Task(Calendar.WEDNESDAY, 10, 30, "Latihan Soal Biologi", "Bahas soal materi Ekologi."),
        Task(Calendar.WEDNESDAY, 13, 30, "Motivasi", "Bacaan Inspiratif: Tetap Semangat!"),
        // KAMIS
        Task(Calendar.THURSDAY, 8, 0, "Matematika", "Materi: Trigonometri"),
        Task(Calendar.THURSDAY, 10, 30, "Fisika", "Materi: Optika"),
        Task(Calendar.THURSDAY, 13, 30, "Tips Ujian", "Tips Mengerjakan Soal Ujian."),
        // JUMAT
        Task(Calendar.FRIDAY, 8, 0, "Kimia", "Materi: Struktur Atom"),
        Task(Calendar.FRIDAY, 10, 30, "Biologi", "Materi: Genetika"),
        Task(Calendar.FRIDAY, 13, 30, "Latihan Bahasa Indonesia", "Review materi Esai."),
        // SABTU
        Task(Calendar.SATURDAY, 8, 0, "Ulangan Mingguan", "Kombinasi Materi Sepekan."),
        Task(Calendar.SATURDAY, 10, 30, "Diskusi Forum", "Bahas Soal atau Materi Sulit."),
        Task(Calendar.SATURDAY, 13, 30, "Evaluasi", "Review Statistik Kemajuan."),
        // MINGGU
        Task(Calendar.SUNDAY, 10, 0, "Evaluasi Pekan", "Istirahat dan Rencana Pekan Depan.")
    )

    fun scheduleAllTasks(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        listJadwal.forEach { task ->
            val intent = Intent(context, StudyReminderReceiver::class.java).apply {
                putExtra("title", task.title)
                putExtra("message", task.msg)
            }

            // Gunakan hash unik agar alarm tidak saling menimpa
            val pendingIntent = PendingIntent.getBroadcast(
                context, task.hashCode(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val cal = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, task.day)
                set(Calendar.HOUR_OF_DAY, task.hour)
                set(Calendar.MINUTE, task.min)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) add(Calendar.DATE, 7)
            }

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, cal.timeInMillis,
                AlarmManager.INTERVAL_DAY * 7, pendingIntent
            )
        }
    }
}