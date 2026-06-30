package com.aknaf.utbk_snbt.model

data class QuestionModel(
    val id: String = "",
    val subject: String = "",

    val question: String = "",
    val questionImageUrl: String = "",
    val questionImageName: String = "",

    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val optionE: String = "",

    val optionAImageUrl: String = "",
    val optionBImageUrl: String = "",
    val optionCImageUrl: String = "",
    val optionDImageUrl: String = "",
    val optionEImageUrl: String = "",

    val optionAImageName: String = "",
    val optionBImageName: String = "",
    val optionCImageName: String = "",
    val optionDImageName: String = "",
    val optionEImageName: String = "",

    val answer: String = "",
    val explanation: String = "",
    val explanationImageUrl: String = "",
    val explanationImageName: String = ""
)
