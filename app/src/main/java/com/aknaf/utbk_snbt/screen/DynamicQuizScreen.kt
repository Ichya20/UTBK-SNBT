package com.aknaf.utbk_snbt.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.aknaf.utbk_snbt.viewmodel.QuizViewModel
import kotlinx.coroutines.delay
import java.util.Locale

// --- [FUNGSI FORMAT WAKTU DI LUAR CLASS BIAR BISA DIPAKAI] ---
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, secs)
}

data class DynamicQuizScreen(val subjectCode: String) : Screen {
    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun Content() {
        val vm: QuizViewModel = viewModel()
        val navigator = LocalNavigator.currentOrThrow

        // Load soal cuma sekali
        LaunchedEffect(subjectCode) {
            vm.startQuiz(subjectCode)
        }

        val questions = vm.filteredQuestions.value
        val currentIdx = vm.currentIndex.value

        // PAKSA BACKGROUND PUTIH (Anti Dark Mode hancur)
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF8F9FA)) {
            when {
                vm.isQuizFinished.value -> {
                    ResultUI(vm.score.value) { navigator.popUntilRoot() }
                }
                questions.isNotEmpty() -> {
                    val soal = questions[currentIdx]

                    Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {

                        // --- [UI UPGRADE: HEADER TIMER & INFO SOAL] ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Warnanya berubah jadi merah kalau waktu sisa < 1 Menit (60 detik)
                            val timerColor = if (vm.timeLeft.value < 60) Color(0xFFD32F2F) else Color(0xFF2B2B6E)

                            Card(
                                colors = CardDefaults.cardColors(containerColor = timerColor.copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, timerColor.copy(alpha = 0.2f))
                            ) {
                                Text(
                                    text = "⏱ ${formatTime(vm.timeLeft.value)}",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    color = timerColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Text(
                                text = "Soal ${currentIdx + 1} dari ${questions.size}",
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Header: Progress Bar Halus
                        val animatedProgress by animateFloatAsState(
                            targetValue = (currentIdx + 1).toFloat() / questions.size,
                            animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
                        )
                        LinearProgressIndicator(
                            progress = animatedProgress,
                            modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                            color = Color(0xFF2B2B6E),
                            trackColor = Color(0xFFE0E0E0)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // --- [ANIMASI TRANSISI ANTAR SOAL] ---
                        AnimatedContent(
                            targetState = soal,
                            transitionSpec = {
                                (slideInHorizontally(animationSpec = tween(500)) { width -> width } + fadeIn()) togetherWith
                                        (slideOutHorizontally(animationSpec = tween(500)) { width -> -width } + fadeOut())
                            },
                            label = "question_transition",
                            modifier = Modifier.weight(1f)
                        ) { currentSoal ->
                            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                // Teks Soal
                                Text(
                                    text = currentSoal.question,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.Black,
                                    lineHeight = 30.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                // List Opsi Jawaban
                                val options = listOf(
                                    "optionA" to currentSoal.optionA, "optionB" to currentSoal.optionB,
                                    "optionC" to currentSoal.optionC, "optionD" to currentSoal.optionD, "optionE" to currentSoal.optionE
                                )

                                options.forEach { (key, text) ->
                                    QuizOptionAnimCard(key, text, vm, currentSoal.answer)
                                }

                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }

                        // --- [LOGIKA FEEDBACK VISUAL SAAT DIJAWAB] ---
                        AnimatedVisibility(
                            visible = vm.isAnswered.value,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = fadeOut()
                        ) {
                            val isUserCorrect = vm.selectedOption.value == soal.answer
                            FeedbackMessage(
                                isCorrect = isUserCorrect,
                                explanation = soal.explanation
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // --- [LOGIKA TOMBOL AKSI BAWAH (Diperbarui)] ---
                        Button(
                            onClick = {
                                if (vm.isAnswered.value) {
                                    // 🚀 Langsung panggil nextQuestion() aja, karena logika simpannya ada di dalam situ!
                                    vm.nextQuestion()
                                } else {
                                    vm.submitAnswer()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2B6E)),
                            shape = RoundedCornerShape(16.dp),
                            enabled = vm.selectedOption.value.isNotEmpty()
                        ) {
                            // Teks tombol berubah jadi "Selesai" kalau di soal terakhir
                            val buttonLabel = when {
                                !vm.isAnswered.value -> "Cek Jawaban"
                                currentIdx == questions.size - 1 -> "Selesai & Lihat Skor"
                                else -> "Lanjut"
                            }

                            Text(
                                text = buttonLabel,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                else -> {
                    // Loading State
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF2B2B6E))
                    }
                }
            }
        }
    }
}

// ============================================
// COMPOSABLE COMPONENT KHUSUS ANIMASI
// ============================================

@Composable
fun QuizOptionAnimCard(key: String, text: String, vm: QuizViewModel, correctAnswer: String) {
    val isSelected = vm.selectedOption.value == key

    val backgroundColor by animateColorAsState(
        targetValue = when {
            vm.isAnswered.value && key == correctAnswer -> Color(0xFFC8E6C9)
            vm.isAnswered.value && isSelected && key != correctAnswer -> Color(0xFFFFCDD2)
            isSelected -> Color(0xFFD1E3FF)
            else -> Color.White
        },
        animationSpec = tween(durationMillis = 300)
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected || (vm.isAnswered.value && key == correctAnswer))
            Color(0xFF2B2B6E) else Color(0xFFE0E0E0)
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .scale(scale)
            .clickable(enabled = !vm.isAnswered.value) { vm.selectedOption.value = key },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if(isSelected) 4.dp else 1.dp),
        border = BorderStroke(if(isSelected) 2.dp else 1.dp, borderColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val letter = key.takeLast(1)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFF2B2B6E) else Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(letter, color = if(isSelected) Color.White else Color.Gray, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(text, modifier = Modifier.weight(1f), color = Color.Black, fontSize = 16.sp)

            if (vm.isAnswered.value) {
                AnimatedVisibility(visible = true, enter = scaleIn() + fadeIn()) {
                    Icon(
                        imageVector = if (key == correctAnswer) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (key == correctAnswer) Color(0xFF388E3C) else Color(0xFFD32F2F),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FeedbackMessage(isCorrect: Boolean, explanation: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isCorrect) Color(0xFFA5D6A7) else Color(0xFFEF9A9A))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape)
                        .background(if (isCorrect) Color(0xFF388E3C) else Color(0xFFD32F2F)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isCorrect) "Jawabanmu Benar, Wok! 🔥" else "Kurang Tepat, Wok! 🥺",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isCorrect) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Pembahasan: $explanation",
                color = Color.Black,
                lineHeight = 22.sp,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ResultUI(score: Int, onHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Kuis Selesai!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(24.dp))

        var animatedScore by remember { mutableStateOf(0) }
        LaunchedEffect(score) {
            delay(300)
            for (i in 0..score) {
                animatedScore = i
                delay(if (score > 0) (1000 / score).toLong() else 10)
            }
        }

        Text(text = "$animatedScore", fontSize = 100.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2B2B6E))
        Text("Skor Kamu", color = Color.Gray, fontSize = 18.sp)

        Spacer(modifier = Modifier.height(60.dp))

        Button(
            onClick = onHome,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2B2B6E),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Kembali ke Beranda",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}