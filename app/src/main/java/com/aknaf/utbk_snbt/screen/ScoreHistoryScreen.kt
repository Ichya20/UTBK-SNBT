package com.aknaf.utbk_snbt.screen

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.aknaf.utbk_snbt.model.ScoreEvaluation
import com.aknaf.utbk_snbt.model.ScoreLevel
import com.aknaf.utbk_snbt.model.ScoreModel
import com.aknaf.utbk_snbt.model.ScorePolicy
import com.aknaf.utbk_snbt.model.SubjectCatalog
import com.aknaf.utbk_snbt.viewmodel.ScoreHistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScoreHistoryScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val vm: ScoreHistoryViewModel = viewModel()

        val history = vm.scoreHistory.value
        val isLoading = vm.isLoading.value
        val errorMessage = vm.errorMessage.value

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF8F9FA)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2B2B6E))
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navigator?.pop() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }

                    Column {
                        Text(
                            text = "Riwayat & Progress",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )

                        Text(
                            text = "Pantau perkembangan latihanmu",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp
                        )
                    }
                }

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF2B2B6E)
                            )
                        }
                    }

                    errorMessage != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment =
                                Alignment.CenterHorizontally,
                            verticalArrangement =
                                Arrangement.Center
                        ) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFD32F2F),
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { vm.fetchHistory() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2B2B6E)
                                )
                            ) {
                                Text(
                                    text = "Coba Lagi",
                                    color = Color.White
                                )
                            }
                        }
                    }

                    history.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada riwayat simulasi. " +
                                    "Kerjakan satu subtes untuk melihat progress.",
                                color = Color.Gray,
                                fontSize = 15.sp,
                                lineHeight = 21.sp
                            )
                        }
                    }

                    else -> {
                        val latestResults =
                            latestResultPerSubject(history)

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding =
                                androidx.compose.foundation.layout
                                    .PaddingValues(20.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                OverallProgressCard(latestResults)
                            }

                            item {
                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Semua Riwayat Pengerjaan",
                                    style = MaterialTheme
                                        .typography
                                        .titleLarge,
                                    color = Color(0xFF2B2B6E),
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            items(
                                items = history,
                                key = {
                                    "${it.timestamp}-${it.subject}-${it.score}"
                                }
                            ) { scoreData ->
                                HistoryCard(scoreData)
                            }

                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverallProgressCard(
    latestResults: List<ScoreModel>
) {
    val totalScore = latestResults.sumOf { it.score }
    val totalMaxScore =
        latestResults.sumOf { it.effectiveMaxScore() }

    val overallPercentage =
        ScorePolicy.calculatePercentage(
            score = totalScore,
            maxScore = totalMaxScore
        )

    val evaluation =
        ScorePolicy.evaluate(overallPercentage)

    val statusColor = scoreStatusColor(evaluation.level)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Total Hasil Terbaru",
                color = Color(0xFF2B2B6E),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 19.sp
            )

            Text(
                text = "Menggunakan hasil terbaru dari setiap subtes",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "$totalScore",
                        color = statusColor,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "dari $totalMaxScore poin",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = String.format(
                            Locale.getDefault(),
                            "%.0f%%",
                            overallPercentage
                        ),
                        color = statusColor,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "${latestResults.size}/" +
                            "${SubjectCatalog.subjects.size} subtes",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = (
                    overallPercentage / 100.0
                ).toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = statusColor,
                trackColor = Color(0xFFE4E4E4)
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatusBox(
                evaluation = evaluation,
                color = statusColor
            )

            if (
                latestResults.size <
                SubjectCatalog.subjects.size
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Lengkapi seluruh subtes agar total progress " +
                        "mewakili latihanmu secara lebih menyeluruh.",
                    color = Color(0xFF666666),
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Catatan: status ini merupakan indikator target " +
                    "latihan, bukan prediksi resmi kelulusan UTBK-SNBT.",
                color = Color(0xFF777777),
                fontSize = 10.sp,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun HistoryCard(data: ScoreModel) {
    val dateFormat = SimpleDateFormat(
        "dd MMM yyyy, HH:mm",
        Locale.getDefault()
    )

    val dateText = dateFormat.format(
        Date(data.timestamp)
    )

    val maxScore = data.effectiveMaxScore()
    val percentage = data.effectivePercentage()
    val evaluation = ScorePolicy.evaluate(percentage)
    val statusColor = scoreStatusColor(evaluation.level)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = SubjectCatalog.displayName(
                            data.subject
                        ),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 17.sp
                    )

                    Text(
                        text = "${data.subject} • $dateText",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${data.score}/$maxScore",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusColor
                    )

                    Text(
                        text = String.format(
                            Locale.getDefault(),
                            "%.0f%%",
                            percentage
                        ),
                        fontSize = 12.sp,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = (
                    percentage / 100.0
                ).toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp),
                color = statusColor,
                trackColor = Color(0xFFE7E7E7)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (data.hasDetailedBreakdown()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {
                    ResultMetric(
                        label = "Benar",
                        value = "${data.correctAnswers}",
                        color = Color(0xFF2E7D32)
                    )

                    ResultMetric(
                        label = "Salah",
                        value = "${data.wrongAnswers}",
                        color = Color(0xFFD32F2F)
                    )

                    ResultMetric(
                        label = "Total Soal",
                        value = "${data.totalQuestions}",
                        color = Color(0xFF2B2B6E)
                    )
                }
            } else {
                Text(
                    text = "Riwayat lama: detail benar dan salah " +
                        "belum tersedia.",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            StatusBox(
                evaluation = evaluation,
                color = statusColor
            )
        }
    }
}

@Composable
private fun ResultMetric(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = color,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )

        Text(
            text = label,
            color = Color.Gray,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun StatusBox(
    evaluation: ScoreEvaluation,
    color: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = color.copy(alpha = 0.10f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = evaluation.title,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = evaluation.description,
            color = Color(0xFF444444),
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
    }
}

private fun latestResultPerSubject(
    history: List<ScoreModel>
): List<ScoreModel> {
    return history
        .groupBy { it.subject.uppercase(Locale.getDefault()) }
        .mapNotNull { (_, attempts) ->
            attempts.maxByOrNull { it.timestamp }
        }
        .sortedBy { result ->
            SubjectCatalog.subjects
                .indexOfFirst {
                    it.code.equals(
                        result.subject,
                        ignoreCase = true
                    )
                }
                .let { index ->
                    if (index >= 0) index else Int.MAX_VALUE
                }
        }
}

private fun scoreStatusColor(
    level: ScoreLevel
): Color {
    return when (level) {
        ScoreLevel.BELOW_TARGET ->
            Color(0xFFD32F2F)

        ScoreLevel.NEAR_TARGET ->
            Color(0xFFF9A825)

        ScoreLevel.TARGET_REACHED ->
            Color(0xFF2E7D32)
    }
}
