package com.aknaf.utbk_snbt.screen

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aknaf.utbk_snbt.viewmodel.QuizViewModel

@Composable
fun DynamicQuizScreen(
    vm: QuizViewModel = viewModel()
) {
    if (vm.loading) {
        CircularProgressIndicator()
        return
    }

    Text(
        text = vm.questions.firstOrNull()?.question ?: "No Data"
    )
}