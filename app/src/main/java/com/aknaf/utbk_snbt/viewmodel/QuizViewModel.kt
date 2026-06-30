package com.aknaf.utbk_snbt.viewmodel

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.aknaf.utbk_snbt.model.QuestionModel
import com.aknaf.utbk_snbt.model.ScoreModel
import com.aknaf.utbk_snbt.model.ScorePolicy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuizViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    var allQuestions = mutableStateOf<List<QuestionModel>>(emptyList())
    var filteredQuestions = mutableStateOf<List<QuestionModel>>(emptyList())

    var currentSubjectCode = mutableStateOf("")
    var currentIndex = mutableStateOf(0)

    var score = mutableStateOf(0)
    var correctAnswers = mutableStateOf(0)
    var wrongAnswers = mutableStateOf(0)
    var totalQuestions = mutableStateOf(0)
    var maxScore = mutableStateOf(0)
    var percentage = mutableStateOf(0.0)

    var isQuizFinished = mutableStateOf(false)
    var selectedOption = mutableStateOf("")
    var isAnswered = mutableStateOf(false)
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    var timeLeft = mutableStateOf(600)

    private var timer: CountDownTimer? = null
    private var scoreHasBeenSaved = false

    fun startQuiz(subjectCode: String) {
        resetQuizState(subjectCode)

        db.collection("dynamic_questions")
            .whereEqualTo("subject", subjectCode)
            .get()
            .addOnSuccessListener { result ->
                val loadedQuestions = result.toObjects(
                    QuestionModel::class.java
                )

                allQuestions.value = loadedQuestions
                filteredQuestions.value = loadedQuestions.shuffled()

                totalQuestions.value = loadedQuestions.size
                maxScore.value =
                    loadedQuestions.size * ScorePolicy.POINTS_PER_CORRECT

                isLoading.value = false

                if (loadedQuestions.isNotEmpty()) {
                    startTimer()
                } else {
                    errorMessage.value =
                        "Belum ada soal untuk subtes $subjectCode"
                }
            }
            .addOnFailureListener { error ->
                isLoading.value = false
                errorMessage.value =
                    "Gagal mengambil soal: ${error.message}"

                Log.e(
                    "QUIZ_ERROR",
                    "Gagal mengambil soal",
                    error
                )
            }
    }

    fun submitAnswer() {
        if (
            isQuizFinished.value ||
            isAnswered.value ||
            selectedOption.value.isBlank()
        ) {
            return
        }

        val questions = filteredQuestions.value
        val index = currentIndex.value

        if (index !in questions.indices) return

        val currentQuestion = questions[index]
        val userAnswer = selectedOption.value.trim()
        val correctAnswer = currentQuestion.answer.trim()

        val isCorrect = userAnswer == correctAnswer

        if (isCorrect) {
            correctAnswers.value += 1
            score.value =
                correctAnswers.value * ScorePolicy.POINTS_PER_CORRECT
        }

        isAnswered.value = true

        Log.d(
            "QUIZ_ANSWER",
            "Subject=${currentSubjectCode.value}, " +
                "user=[$userAnswer], correct=[$correctAnswer], " +
                "isCorrect=$isCorrect, score=${score.value}"
        )
    }

    fun nextQuestion() {
        if (!isAnswered.value || isQuizFinished.value) {
            return
        }

        if (
            currentIndex.value <
            filteredQuestions.value.size - 1
        ) {
            currentIndex.value += 1
            selectedOption.value = ""
            isAnswered.value = false
        } else {
            finishQuiz()
        }
    }

    fun resetAndRestartQuiz() {
        val subjectCode = currentSubjectCode.value

        if (subjectCode.isBlank()) {
            return
        }

        startQuiz(subjectCode)
    }

    private fun resetQuizState(subjectCode: String) {
        timer?.cancel()

        currentSubjectCode.value = subjectCode
        allQuestions.value = emptyList()
        filteredQuestions.value = emptyList()

        currentIndex.value = 0
        score.value = 0
        correctAnswers.value = 0
        wrongAnswers.value = 0
        totalQuestions.value = 0
        maxScore.value = 0
        percentage.value = 0.0

        isQuizFinished.value = false
        selectedOption.value = ""
        isAnswered.value = false
        isLoading.value = true
        errorMessage.value = null

        timeLeft.value = 600
        scoreHasBeenSaved = false
    }

    private fun startTimer() {
        timer?.cancel()

        timer = object : CountDownTimer(
            timeLeft.value * 1000L,
            1000L
        ) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft.value =
                    (millisUntilFinished / 1000L).toInt()
            }

            override fun onFinish() {
                timeLeft.value = 0
                finishQuiz()
            }
        }.start()
    }

    private fun finishQuiz() {
        if (isQuizFinished.value) return

        timer?.cancel()

        val total = totalQuestions.value
        val correct = correctAnswers.value

        score.value =
            correct * ScorePolicy.POINTS_PER_CORRECT

        wrongAnswers.value =
            (total - correct).coerceAtLeast(0)

        maxScore.value =
            total * ScorePolicy.POINTS_PER_CORRECT

        percentage.value = ScorePolicy.calculatePercentage(
            score = score.value,
            maxScore = maxScore.value
        )

        isQuizFinished.value = true
        saveScoreToFirebase()
    }

    private fun saveScoreToFirebase() {
        if (scoreHasBeenSaved) return
        scoreHasBeenSaved = true

        val userId = FirebaseAuth.getInstance()
            .currentUser
            ?.uid
            ?: "anonymous_user"

        val scoreData = ScoreModel(
            userId = userId,
            subject = currentSubjectCode.value,
            score = score.value,
            correctAnswers = correctAnswers.value,
            wrongAnswers = wrongAnswers.value,
            totalQuestions = totalQuestions.value,
            maxScore = maxScore.value,
            percentage = percentage.value,
            timestamp = System.currentTimeMillis()
        )

        db.collection("user_scores")
            .add(scoreData)
            .addOnSuccessListener {
                Log.d(
                    "FIREBASE_SCORE",
                    "Skor tersimpan: ${score.value}/" +
                        "${maxScore.value} untuk " +
                        currentSubjectCode.value
                )
            }
            .addOnFailureListener { error ->
                scoreHasBeenSaved = false

                Log.e(
                    "FIREBASE_SCORE",
                    "Gagal menyimpan skor",
                    error
                )
            }
    }

    override fun onCleared() {
        timer?.cancel()
        super.onCleared()
    }
}
