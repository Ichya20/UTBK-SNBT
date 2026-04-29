package com.aknaf.utbk_snbt.utils

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aknaf.utbk_snbt.R

class StudyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Waktunya Belajar!"
        val message = intent.getStringExtra("message") ?: "Cek materi hari ini, yuk."

        // 1. Cek izin dulu biar error di baris 24 ilang
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        // 2. Builder Notifikasi
        val builder = NotificationCompat.Builder(context, "STUDY_SMART_CHANNEL")
            .setSmallIcon(R.drawable.ic_notification) // <--- PASTIKAN FILE INI ADA (BACA DI BAWAH)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}