package com.aknaf.utbk_snbt.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.aknaf.utbk_snbt.model.ScoreModel
import com.aknaf.utbk_snbt.viewmodel.ScoreHistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScoreHistoryScreen : Screen {
    @Composable
    override fun Content() {
        val vm: ScoreHistoryViewModel = viewModel()
        val history = vm.scoreHistory.value
        val isLoading = vm.isLoading.value

        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF8F9FA)) {
            Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {
                // Header
                Text(
                    text = "Riwayat Nilai",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF2B2B6E),
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Pantau perkembangan belajarmu di sini",
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Menampilkan Konten berdasarkan State
                when {
                    isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF2B2B6E))
                        }
                    }
                    history.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Belum ada riwayat kuis, Wok. Yuk simulasi dulu!", color = Color.Gray)
                        }
                    }
                    else -> {
                        // Daftar Skor
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(history) { scoreData ->
                                HistoryCard(scoreData)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Komponen Kartu untuk masing-masing skor
@Composable
fun HistoryCard(data: ScoreModel) {
    // Ubah angka timestamp dari Firebase jadi format tanggal & jam yang gampang dibaca
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateStr = sdf.format(Date(data.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Materi: ${data.subject}", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = dateStr, fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                text = "${data.score}",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2B2B6E)
            )
        }
    }
}