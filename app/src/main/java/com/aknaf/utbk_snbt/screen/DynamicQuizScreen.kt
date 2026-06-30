package com.aknaf.utbk_snbt.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import com.aknaf.utbk_snbt.model.ScoreLevel
import com.aknaf.utbk_snbt.model.ScorePolicy
import com.aknaf.utbk_snbt.model.SubjectCatalog
import java.util.Locale
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.aknaf.utbk_snbt.ads.InterstitialAdManager

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
        val context = LocalContext.current
        val activity = context as? Activity
        var canShowResult by remember(subjectCode) { mutableStateOf(false) }

        // Load soal cuma sekali
        LaunchedEffect(subjectCode) {
            vm.startQuiz(subjectCode)
            InterstitialAdManager.load(context)
        }

        val questions = vm.filteredQuestions.value
        val currentIdx = vm.currentIndex.value

        // PAKSA BACKGROUND PUTIH (Anti Dark Mode hancur)
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF8F9FA)) {
            when {
                vm.isLoading.value -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2B2B6E))
                    }
                }

                vm.errorMessage.value != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = vm.errorMessage.value ?: "Terjadi kesalahan saat memuat soal.",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { navigator.pop() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2B2B6E)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Kembali",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                vm.isQuizFinished.value && !canShowResult -> {
                    LaunchedEffect(Unit) {
                        if (activity != null) {
                            InterstitialAdManager.show(activity) {
                                canShowResult = true
                            }
                        } else {
                            canShowResult = true
                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = Color(0xFF2B2B6E))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Menyiapkan hasil simulasi...",
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                vm.isQuizFinished.value && canShowResult -> {
                    ResultUI(
                        subjectCode = vm.currentSubjectCode.value,
                        score = vm.score.value,
                        correctAnswers = vm.correctAnswers.value,
                        wrongAnswers = vm.wrongAnswers.value,
                        totalQuestions = vm.totalQuestions.value,
                        maxScore = vm.maxScore.value,
                        percentage = vm.percentage.value,
                        onRestart = {
                            canShowResult = false
                            vm.resetAndRestartQuiz()
                            InterstitialAdManager.load(context)
                        },
                        onHome = { navigator.popUntilRoot() }
                    )
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
                            val timerColor =
                                if (vm.timeLeft.value < 60) Color(0xFFD32F2F) else Color(0xFF2B2B6E)

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = timerColor.copy(
                                        alpha = 0.1f
                                    )
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, timerColor.copy(alpha = 0.2f))
                            ) {
                                Text(
                                    text = "⏱ ${formatTime(vm.timeLeft.value)}",
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
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
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = LinearOutSlowInEasing
                            )
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
                                // Teks dan gambar soal
                                if (currentSoal.question.isNotBlank()) {
                                    Text(
                                        text = currentSoal.question,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.Black,
                                        lineHeight = 30.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                if (
                                    currentSoal.questionImageUrl.isNotBlank()
                                    || currentSoal.questionImageName.isNotBlank()
                                ) {
                                    if (currentSoal.question.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                    }

                                    QuizImage(
                                        imageUrl = currentSoal.questionImageUrl,
                                        imageName = currentSoal.questionImageName,
                                        contentDescription = "Gambar soal",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(
                                                min = 140.dp,
                                                max = 320.dp
                                            )
                                            .clip(RoundedCornerShape(16.dp))
                                    )
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                // List Opsi Jawaban.
                                // Pilihan bisa berupa teks, gambar, atau teks + gambar.
                                val options = listOf(
                                    QuizOptionData("optionA", currentSoal.optionA, currentSoal.optionAImageUrl, currentSoal.optionAImageName),
                                    QuizOptionData("optionB", currentSoal.optionB, currentSoal.optionBImageUrl, currentSoal.optionBImageName),
                                    QuizOptionData("optionC", currentSoal.optionC, currentSoal.optionCImageUrl, currentSoal.optionCImageName),
                                    QuizOptionData("optionD", currentSoal.optionD, currentSoal.optionDImageUrl, currentSoal.optionDImageName),
                                    QuizOptionData("optionE", currentSoal.optionE, currentSoal.optionEImageUrl, currentSoal.optionEImageName)
                                )

                                options.forEach { option ->
                                    if (
                                        option.text.isNotBlank()
                                        || option.imageUrl.isNotBlank()
                                        || option.imageName.isNotBlank()
                                    ) {
                                        QuizOptionAnimCard(
                                            key = option.key,
                                            text = option.text,
                                            imageUrl = option.imageUrl,
                                            imageName = option.imageName,
                                            vm = vm,
                                            correctAnswer = currentSoal.answer
                                        )
                                    }
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
                            val isUserCorrect = vm.selectedOption.value.trim() == soal.answer.trim()
                            FeedbackMessage(
                                isCorrect = isUserCorrect,
                                explanation = soal.explanation,
                                explanationImageUrl = soal.explanationImageUrl,
                                explanationImageName = soal.explanationImageName
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
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Soal belum tersedia.",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ============================================
// COMPOSABLE COMPONENT KHUSUS ANIMASI
// ============================================

private data class QuizOptionData(
    val key: String,
    val text: String,
    val imageUrl: String,
    val imageName: String
)

@Composable
fun QuizImage(
    imageUrl: String,
    imageName: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cleanImageName = imageName.trim().substringBeforeLast(".")
    val localImageResId = if (cleanImageName.isNotBlank()) {
        context.resources.getIdentifier(cleanImageName, "drawable", context.packageName)
    } else {
        0
    }

    when {
        localImageResId != 0 -> {
            Image(
                painter = painterResource(id = localImageResId),
                contentDescription = contentDescription,
                modifier = modifier.background(Color.White),
                contentScale = ContentScale.Fit
            )
        }

        imageUrl.isNotBlank() -> {
            RemoteImage(
                url = imageUrl,
                contentDescription = contentDescription,
                modifier = modifier
            )
        }

        else -> {
            Box(
                modifier = modifier.background(
                    color = Color(0xFFFFF3F3),
                    shape = RoundedCornerShape(16.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Gambar belum tersedia",
                    color = Color(0xFFD32F2F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private sealed class RemoteImageState {
    object Loading : RemoteImageState()
    data class Success(val bitmap: Bitmap) : RemoteImageState()
    object Error : RemoteImageState()
}

@Composable
fun RemoteImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val cleanUrl = url.trim()

    if (cleanUrl.isBlank()) {
        return
    }

    val imageState by produceState<RemoteImageState>(
        initialValue = RemoteImageState.Loading,
        key1 = cleanUrl
    ) {
        value = withContext(Dispatchers.IO) {
            try {
                val connection = URL(cleanUrl).openConnection()
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                connection.getInputStream().use { input ->
                    val bitmap = BitmapFactory.decodeStream(input)

                    if (bitmap != null) {
                        RemoteImageState.Success(bitmap)
                    } else {
                        RemoteImageState.Error
                    }
                }
            } catch (error: Exception) {
                RemoteImageState.Error
            }
        }
    }

    when (val state = imageState) {
        is RemoteImageState.Loading -> {
            Box(
                modifier = modifier.background(
                    color = Color(0xFFF1F1F1),
                    shape = RoundedCornerShape(16.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = Color(0xFF2B2B6E),
                    strokeWidth = 3.dp
                )
            }
        }

        is RemoteImageState.Success -> {
            Image(
                bitmap = state.bitmap.asImageBitmap(),
                contentDescription = contentDescription,
                modifier = modifier.background(Color.White),
                contentScale = ContentScale.Fit
            )
        }

        is RemoteImageState.Error -> {
            Box(
                modifier = modifier.background(
                    color = Color(0xFFFFF3F3),
                    shape = RoundedCornerShape(16.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Gambar tidak dapat dimuat",
                    color = Color(0xFFD32F2F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuizOptionAnimCard(
    key: String,
    text: String,
    imageUrl: String,
    imageName: String,
    vm: QuizViewModel,
    correctAnswer: String
) {
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

            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (text.isNotBlank()) {
                    Text(
                        text = text,
                        color = Color.Black,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    )
                }

                if (imageUrl.isNotBlank() || imageName.isNotBlank()) {
                    if (text.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    QuizImage(
                        imageUrl = imageUrl,
                        imageName = imageName,
                        contentDescription = "Gambar pilihan $letter",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(
                                min = 90.dp,
                                max = 190.dp
                            )
                            .clip(RoundedCornerShape(14.dp))
                    )
                }
            }

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
fun FeedbackMessage(
    isCorrect: Boolean,
    explanation: String,
    explanationImageUrl: String,
    explanationImageName: String
) {
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
            if (explanation.isNotBlank()) {
                Text(
                    text = "Pembahasan: $explanation",
                    color = Color.Black,
                    lineHeight = 22.sp,
                    fontSize = 14.sp
                )
            } else {
                Text(
                    text = "Pembahasan belum tersedia.",
                    color = Color.Black,
                    lineHeight = 22.sp,
                    fontSize = 14.sp
                )
            }

            if (explanationImageUrl.isNotBlank() || explanationImageName.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                QuizImage(
                    imageUrl = explanationImageUrl,
                    imageName = explanationImageName,
                    contentDescription = "Gambar pembahasan",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            min = 120.dp,
                            max = 260.dp
                        )
                        .clip(RoundedCornerShape(14.dp))
                )
            }
        }
    }
}

@Composable
fun ResultUI(
    subjectCode: String,
    score: Int,
    correctAnswers: Int,
    wrongAnswers: Int,
    totalQuestions: Int,
    maxScore: Int,
    percentage: Double,
    onRestart: () -> Unit,
    onHome: () -> Unit
) {
    val evaluation = ScorePolicy.evaluate(percentage)

    val statusColor = when (evaluation.level) {
        ScoreLevel.BELOW_TARGET -> Color(0xFFD32F2F)
        ScoreLevel.NEAR_TARGET -> Color(0xFFF9A825)
        ScoreLevel.TARGET_REACHED -> Color(0xFF2E7D32)
    }

    var startAnimation by remember {
        mutableStateOf(false)
    }

    var showResetDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(score) {
        startAnimation = true
    }

    val animatedScore by animateFloatAsState(
        targetValue = if (startAnimation) {
            score.toFloat()
        } else {
            0f
        },
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearOutSlowInEasing
        )
    )

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = {
                showResetDialog = false
            },
            title = {
                Text(
                    text = "Ulangi Simulasi?",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            text = {
                Text(
                    text = "Jawaban dan skor percobaan ini akan direset dari layar pengerjaan. Riwayat skor yang sudah tersimpan tetap aman.",
                    color = Color(0xFF333333),
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        onRestart()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2B2B6E),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Ya, Ulangi",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showResetDialog = false
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Batal")
                }
            },
            containerColor = Color.White
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Simulasi Selesai!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = SubjectCatalog.displayName(subjectCode),
            color = Color.Gray,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            ),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = animatedScore.toInt().toString(),
                    fontSize = 76.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = statusColor
                )

                Text(
                    text = "dari $maxScore poin",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = (
                        percentage / 100.0
                    ).toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = statusColor,
                    trackColor = Color(0xFFE5E5E5)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "%.0f%%",
                        percentage
                    ),
                    color = statusColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ResultMetricCard(
                label = "Benar",
                value = correctAnswers.toString(),
                color = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f)
            )

            ResultMetricCard(
                label = "Salah",
                value = wrongAnswers.toString(),
                color = Color(0xFFD32F2F),
                modifier = Modifier.weight(1f)
            )

            ResultMetricCard(
                label = "Total Soal",
                value = totalQuestions.toString(),
                color = Color(0xFF2B2B6E),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = statusColor.copy(alpha = 0.10f)
            ),
            border = BorderStroke(
                width = 1.dp,
                color = statusColor.copy(alpha = 0.35f)
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = evaluation.title,
                    color = statusColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = evaluation.description,
                    color = Color(0xFF333333),
                    lineHeight = 20.sp,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Status ini merupakan indikator target latihan, " +
                "bukan prediksi resmi kelulusan UTBK-SNBT.",
            color = Color.Gray,
            fontSize = 10.sp,
            lineHeight = 15.sp
        )

        Spacer(modifier = Modifier.height(26.dp))

        Button(
            onClick = {
                showResetDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD32F2F),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Ulangi Simulasi",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
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

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ResultMetricCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = color,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            )

            Text(
                text = label,
                color = Color.Gray,
                fontSize = 11.sp
            )
        }
    }
}
