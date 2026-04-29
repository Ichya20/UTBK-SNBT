package com.aknaf.utbk_snbt.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aknaf.utbk_snbt.model.QuestionModel
import com.aknaf.utbk_snbt.repository.QuizRepository
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {

    private val repo = QuizRepository()

    var questions by mutableStateOf<List<QuestionModel>>(emptyList())
    var loading by mutableStateOf(true)

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            questions = repo.getQuestions()
            loading = false
        }
    }
}