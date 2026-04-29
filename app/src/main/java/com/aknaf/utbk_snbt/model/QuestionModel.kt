package com.aknaf.utbk_snbt.model

data class QuestionModel(
    val id: String = "",
    val question: String = "",
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val optionE: String = "",
    val answer: String = "",
    val explanation: String = ""
)