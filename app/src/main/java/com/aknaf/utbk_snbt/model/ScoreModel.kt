package com.aknaf.utbk_snbt.model

data class ScoreModel(
    val userId: String = "",
    val subject: String = "",
    val score: Int = 0,
    val timestamp: Long = 0L
)