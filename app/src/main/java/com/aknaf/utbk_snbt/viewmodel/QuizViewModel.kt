package com.aknaf.utbk_snbt.viewmodel

import android.os.CountDownTimer // 🚀 WAJIB TAMBAH IMPORT INI
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.aknaf.utbk_snbt.model.QuestionModel
import com.google.firebase.firestore.FirebaseFirestore

class QuizViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    var allQuestions = mutableStateOf<List<QuestionModel>>(emptyList())
    var filteredQuestions = mutableStateOf<List<QuestionModel>>(emptyList())
    var currentIndex = mutableStateOf(0)
    var score = mutableStateOf(0)
    var isQuizFinished = mutableStateOf(false)
    var selectedOption = mutableStateOf("")
    var isAnswered = mutableStateOf(false)

    var timeLeft = mutableStateOf(600) // Waktu default 600 detik (10 Menit)
    private var timer: CountDownTimer? = null

    // Fungsi untuk narik data & filter berdasarkan kategori
    fun startQuiz(subjectCode: String) {
        // Reset state biar kerasa fresh kalau user ngulang kuis
        filteredQuestions.value = emptyList()
        score.value = 0
        currentIndex.value = 0
        isQuizFinished.value = false
        timeLeft.value = 600
        timer?.cancel()

        db.collection("dynamic_questions")
            .whereEqualTo("subject", subjectCode) // Filter di server Firestore
            .get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(QuestionModel::class.java)
                filteredQuestions.value = list.shuffled()

                // 🚀 MULAI TIMER HANYA JIKA SOAL BERHASIL DI-DOWNLOAD
                if (list.isNotEmpty()) {
                    startTimer()
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("QUIZ_ERROR", "Gagal ambil soal: ${e.message}")
            }
    }

    // 🚀 INI BLOK FUNGSI TIMER-NYA
    private fun startTimer() {
        timer?.cancel() // Batalkan timer lama kalau ada
        timer = object : CountDownTimer(timeLeft.value * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft.value = (millisUntilFinished / 1000).toInt()
            }
            override fun onFinish() {
                // Kuis otomatis selesai saat waktu habis
                isQuizFinished.value = true
                saveScoreToFirebase() // 🚀 TAMBAHKAN INI WOK!
            }
        }.start()
    }

    // --- [FUNGSI SIMPAN SKOR KE FIREBASE] ---
    private fun saveScoreToFirebase() {
        // Karena kita belum bikin fitur Login, kita pakai ID sementara dulu
        val dummyUserId = "ichya_ulumiddiin"

        // Ambil kode materi dari soal yang lagi dikerjakan
        val currentSubject = filteredQuestions.value.firstOrNull()?.subject ?: "UNKNOWN"

        val scoreData = com.aknaf.utbk_snbt.model.ScoreModel(
            userId = dummyUserId,
            subject = currentSubject,
            score = score.value,
            timestamp = System.currentTimeMillis()
        )

        db.collection("user_scores").add(scoreData)
            .addOnSuccessListener {
                android.util.Log.d("FIREBASE_SCORE", "Wih, skor berhasil disimpan!")
            }
            .addOnFailureListener { e ->
                android.util.Log.e("FIREBASE_SCORE", "Gagal simpan skor: ${e.message}")
            }
    }

    fun submitAnswer() {
        val currentSoal = filteredQuestions.value[currentIndex.value]
        // Logika skor asli: bandingkan pilihan user dengan kunci jawaban
        if (selectedOption.value == currentSoal.answer) {
            score.value += 10
        }
        isAnswered.value = true
    }

    fun nextQuestion() {
        if (currentIndex.value < filteredQuestions.value.size - 1) {
            currentIndex.value++
            selectedOption.value = ""
            isAnswered.value = false
        } else {
            isQuizFinished.value = true
            saveScoreToFirebase() // 🚀 TAMBAHKAN INI JUGA DI SINI!
        }
    }

    // 🚀 WAJIB DITAMBAHKAN BIAR HP GAK NGE-LAG KARENA MEMORY LEAK
    override fun onCleared() {
        super.onCleared()
        timer?.cancel() // Matikan timer kalau user keluar dari halaman kuis
    }
}