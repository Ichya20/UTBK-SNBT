package com.aknaf.utbk_snbt

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.aknaf.utbk_snbt.utils.AlarmScheduler
import com.aknaf.utbk_snbt.utils.ScheduleStorage
import com.aknaf.utbk_snbt.utils.StudySchedule
import java.util.Calendar
import java.util.Locale

class Jadwal : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val context = LocalContext.current

        val schedules = remember { mutableStateListOf<StudySchedule>() }

        var title by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }
        var selectedDay by remember { mutableStateOf(Calendar.MONDAY) }
        var selectedHour by remember { mutableStateOf(18) }
        var selectedMinute by remember { mutableStateOf(0) }
        var dayMenuExpanded by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            schedules.clear()
            schedules.addAll(ScheduleStorage.getAll(context))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F6FA))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2B2B6E))
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navigator?.pop() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Jadwal Belajar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Buat jadwal sesuai waktu luangmu",
                        color = Color(0xFF2B2B6E),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Kamu bisa menentukan sendiri hari, jam, dan kegiatan belajar. " +
                            "Aplikasi akan mengirimkan pengingat setiap minggu.",
                        color = Color(0xFF555555),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Tambah Jadwal",
                                color = Color(0xFF2B2B6E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Nama kegiatan") },
                                placeholder = { Text("Contoh: Latihan Penalaran Matematika") },
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = note,
                                onValueChange = { note = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Catatan (opsional)") },
                                placeholder = { Text("Contoh: Kerjakan 20 soal") },
                                minLines = 2,
                                maxLines = 3
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedButton(
                                        onClick = { dayMenuExpanded = true },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(dayName(selectedDay))
                                    }

                                    DropdownMenu(
                                        expanded = dayMenuExpanded,
                                        onDismissRequest = { dayMenuExpanded = false }
                                    ) {
                                        dayOptions.forEach { (day, label) ->
                                            DropdownMenuItem(
                                                text = { Text(label) },
                                                onClick = {
                                                    selectedDay = day
                                                    dayMenuExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        TimePickerDialog(
                                            context,
                                            { _, hour, minute ->
                                                selectedHour = hour
                                                selectedMinute = minute
                                            },
                                            selectedHour,
                                            selectedMinute,
                                            true
                                        ).show()
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        String.format(
                                            Locale.getDefault(),
                                            "%02d:%02d",
                                            selectedHour,
                                            selectedMinute
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val cleanTitle = title.trim()
                                    if (cleanTitle.isBlank()) {
                                        Toast.makeText(
                                            context,
                                            "Nama kegiatan belum diisi.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@Button
                                    }

                                    val newSchedule = StudySchedule(
                                        id = System.currentTimeMillis(),
                                        title = cleanTitle,
                                        note = note.trim(),
                                        dayOfWeek = selectedDay,
                                        hour = selectedHour,
                                        minute = selectedMinute,
                                        enabled = true
                                    )

                                    schedules.add(newSchedule)
                                    schedules.sortWith(scheduleComparator)
                                    ScheduleStorage.saveAll(context, schedules.toList())
                                    AlarmScheduler.scheduleTask(context, newSchedule)

                                    title = ""
                                    note = ""

                                    Toast.makeText(
                                        context,
                                        "Jadwal berhasil ditambahkan.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF57BF4B)
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    text = "Simpan Jadwal",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Jadwal Saya",
                        color = Color(0xFF2B2B6E),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                if (schedules.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Belum ada jadwal",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF333333)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tambahkan jadwal pertamamu melalui formulir di atas.",
                                    color = Color(0xFF777777),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                } else {
                    items(
                        items = schedules,
                        key = { it.id }
                    ) { task ->
                        ScheduleCard(
                            task = task,
                            onEnabledChange = { enabled ->
                                val index = schedules.indexOfFirst { it.id == task.id }
                                if (index >= 0) {
                                    val updated = task.copy(enabled = enabled)
                                    schedules[index] = updated
                                    ScheduleStorage.saveAll(context, schedules.toList())

                                    if (enabled) {
                                        AlarmScheduler.scheduleTask(context, updated)
                                    } else {
                                        AlarmScheduler.cancelTask(context, updated)
                                    }
                                }
                            },
                            onDelete = {
                                AlarmScheduler.cancelTask(context, task)
                                schedules.removeAll { it.id == task.id }
                                ScheduleStorage.saveAll(context, schedules.toList())

                                Toast.makeText(
                                    context,
                                    "Jadwal dihapus.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ScheduleCard(
    task: StudySchedule,
    onEnabledChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.enabled) Color.White else Color(0xFFECECEC)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = Color(0xFF222222),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "${dayName(task.dayOfWeek)}, ${
                        String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            task.hour,
                            task.minute
                        )
                    }",
                    color = Color(0xFF2B2B6E),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                if (task.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = task.note,
                        color = Color(0xFF666666),
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = if (task.enabled) "Pengingat aktif" else "Pengingat dinonaktifkan",
                    color = if (task.enabled) Color(0xFF2E7D32) else Color(0xFF777777),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(
                    checked = task.enabled,
                    onCheckedChange = onEnabledChange
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus jadwal",
                        tint = Color(0xFFD32F2F)
                    )
                }
            }
        }
    }
}

private val dayOptions = listOf(
    Calendar.MONDAY to "Senin",
    Calendar.TUESDAY to "Selasa",
    Calendar.WEDNESDAY to "Rabu",
    Calendar.THURSDAY to "Kamis",
    Calendar.FRIDAY to "Jumat",
    Calendar.SATURDAY to "Sabtu",
    Calendar.SUNDAY to "Minggu"
)

private fun dayName(day: Int): String {
    return dayOptions.firstOrNull { it.first == day }?.second ?: "Senin"
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

private val scheduleComparator =
    compareBy<StudySchedule>(
        { dayOrder(it.dayOfWeek) },
        { it.hour },
        { it.minute },
        { it.title.lowercase(Locale.getDefault()) }
    )
