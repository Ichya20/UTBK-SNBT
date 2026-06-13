package com.aknaf.utbk_snbt.viewmodel

import android.os.CountDownTimer
import android.util.Log
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

    var timeLeft = mutableStateOf(600)
    private var timer: CountDownTimer? = null

    fun startQuiz(subjectCode: String) {
        filteredQuestions.value = emptyList()
        score.value = 0
        currentIndex.value = 0
        isQuizFinished.value = false
        selectedOption.value = ""
        isAnswered.value = false
        timeLeft.value = 600
        timer?.cancel()

        db.collection("dynamic_questions")
            .whereEqualTo("subject", subjectCode)
            .get()
            .addOnSuccessListener { result ->
                // ─── DEBUG: Lihat raw data dari Firestore ───
                result.documents.forEach { doc ->
                    Log.d("QUIZ_DEBUG", "=== Document ID: ${doc.id} ===")
                    Log.d("QUIZ_DEBUG", "  subject  : ${doc.getString("subject")}")
                    Log.d("QUIZ_DEBUG", "  question : ${doc.getString("question")}")
                    Log.d("QUIZ_DEBUG", "  optionA  : ${doc.getString("optionA")}")
                    Log.d("QUIZ_DEBUG", "  optionB  : ${doc.getString("optionB")}")
                    Log.d("QUIZ_DEBUG", "  optionC  : ${doc.getString("optionC")}")
                    Log.d("QUIZ_DEBUG", "  optionD  : ${doc.getString("optionD")}")
                    Log.d("QUIZ_DEBUG", "  optionE  : ${doc.getString("optionE")}")
                    Log.d("QUIZ_DEBUG", "  answer   : ${doc.getString("answer")}")
                }

                val list = result.toObjects(QuestionModel::class.java)

                // ─── DEBUG: Lihat hasil mapping ke QuestionModel ───
                list.forEach { q ->
                    Log.d("QUIZ_DEBUG", "=== QuestionModel ===")
                    Log.d("QUIZ_DEBUG", "  subject  : ${q.subject}")
                    Log.d("QUIZ_DEBUG", "  question : ${q.question}")
                    Log.d("QUIZ_DEBUG", "  answer   : [${q.answer}]") // kurung buat deteksi spasi tersembunyi
                    Log.d("QUIZ_DEBUG", "  optionA  : [${q.optionA}]")
                    Log.d("QUIZ_DEBUG", "  optionB  : [${q.optionB}]")
                }

                filteredQuestions.value = list.shuffled()

                if (list.isNotEmpty()) {
                    startTimer()
                }
            }
            .addOnFailureListener { e ->
                Log.e("QUIZ_ERROR", "Gagal ambil soal: ${e.message}")
            }
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(timeLeft.value * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft.value = (millisUntilFinished / 1000).toInt()
            }
            override fun onFinish() {
                isQuizFinished.value = true
                saveScoreToFirebase()
            }
        }.start()
    }

    fun submitAnswer() {
        val currentSoal = filteredQuestions.value[currentIndex.value]
        val userAnswer = selectedOption.value.trim()
        val correctAnswer = currentSoal.answer.trim() // .trim() buat buang spasi tersembunyi

        // ─── DEBUG: Bandingkan secara eksplisit ───
        Log.d("QUIZ_ANSWER", "User pilih   : [$userAnswer]")
        Log.d("QUIZ_ANSWER", "Jawaban benar: [$correctAnswer]")
        Log.d("QUIZ_ANSWER", "Cocok?       : ${userAnswer == correctAnswer}")

        if (userAnswer == correctAnswer) {
            score.value += 10
            Log.d("QUIZ_ANSWER", "✅ BENAR! Skor sekarang: ${score.value}")
        } else {
            Log.d("QUIZ_ANSWER", "❌ SALAH")
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
            saveScoreToFirebase()
        }
    }

    private fun saveScoreToFirebase() {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
            ?: "anonymous_user"

        val currentSubject = filteredQuestions.value.firstOrNull()?.subject ?: "UNKNOWN"

        val scoreData = com.aknaf.utbk_snbt.model.ScoreModel(
            userId = userId,
            subject = currentSubject,
            score = score.value,
            timestamp = System.currentTimeMillis()
        )

        db.collection("user_scores").add(scoreData)
            .addOnSuccessListener {
                Log.d("FIREBASE_SCORE", "Skor berhasil disimpan: ${score.value} untuk $currentSubject")
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE_SCORE", "Gagal simpan skor: ${e.message}")
            }
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}