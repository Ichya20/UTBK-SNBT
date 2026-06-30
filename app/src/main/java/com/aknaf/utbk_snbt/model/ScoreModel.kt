package com.aknaf.utbk_snbt.model

import kotlin.math.ceil

data class ScoreModel(
    val userId: String = "",
    val subject: String = "",
    val score: Int = 0,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val totalQuestions: Int = 0,
    val maxScore: Int = 0,
    val percentage: Double = 0.0,
    val timestamp: Long = 0L
) {
    /*
     * Dokumen skor versi lama hanya memiliki field score.
     * Fungsi effective* menjaga data lama tetap dapat ditampilkan.
     */
    fun effectiveMaxScore(): Int {
        return when {
            maxScore > 0 -> maxScore
            totalQuestions > 0 -> totalQuestions * ScorePolicy.POINTS_PER_CORRECT
            else -> maxOf(
                100,
                ceil(score / ScorePolicy.POINTS_PER_CORRECT.toDouble())
                    .toInt() * ScorePolicy.POINTS_PER_CORRECT
            )
        }
    }

    fun effectivePercentage(): Double {
        val resolvedMax = effectiveMaxScore()
        if (resolvedMax <= 0) return 0.0

        return if (percentage > 0.0) {
            percentage.coerceIn(0.0, 100.0)
        } else {
            (score.toDouble() / resolvedMax.toDouble() * 100.0)
                .coerceIn(0.0, 100.0)
        }
    }

    fun effectiveCorrectAnswers(): Int {
        return when {
            totalQuestions > 0 -> correctAnswers
            score > 0 -> score / ScorePolicy.POINTS_PER_CORRECT
            else -> 0
        }
    }

    fun hasDetailedBreakdown(): Boolean {
        return totalQuestions > 0
    }
}

enum class ScoreLevel {
    BELOW_TARGET,
    NEAR_TARGET,
    TARGET_REACHED
}

data class ScoreEvaluation(
    val level: ScoreLevel,
    val title: String,
    val description: String
)

object ScorePolicy {
    const val POINTS_PER_CORRECT = 10

    const val NEAR_TARGET_MIN_PERCENTAGE = 60.0
    const val TARGET_REACHED_MIN_PERCENTAGE = 80.0

    fun calculatePercentage(
        score: Int,
        maxScore: Int
    ): Double {
        if (maxScore <= 0) return 0.0

        return (score.toDouble() / maxScore.toDouble() * 100.0)
            .coerceIn(0.0, 100.0)
    }

    fun evaluate(percentage: Double): ScoreEvaluation {
        return when {
            percentage >= TARGET_REACHED_MIN_PERCENTAGE -> {
                ScoreEvaluation(
                    level = ScoreLevel.TARGET_REACHED,
                    title = "Target latihan tercapai",
                    description = "Pertahankan hasilmu dan lanjutkan latihan secara konsisten."
                )
            }

            percentage >= NEAR_TARGET_MIN_PERCENTAGE -> {
                ScoreEvaluation(
                    level = ScoreLevel.NEAR_TARGET,
                    title = "Mendekati target latihan",
                    description = "Hasilmu sudah cukup baik. Perkuat materi yang masih salah."
                )
            }

            else -> {
                ScoreEvaluation(
                    level = ScoreLevel.BELOW_TARGET,
                    title = "Belum mencapai target latihan",
                    description = "Pelajari kembali pembahasan dan ulangi simulasi untuk meningkatkan skor."
                )
            }
        }
    }
}

data class SubjectInfo(
    val name: String,
    val code: String
)

object SubjectCatalog {
    val subjects = listOf(
        SubjectInfo("Penalaran Umum", "PU"),
        SubjectInfo("Pengetahuan Kuantitatif", "PK"),
        SubjectInfo("Penalaran Matematika", "PM"),
        SubjectInfo("Pengetahuan & Pemahaman Umum", "PPU"),
        SubjectInfo("Kemampuan Memahami Bacaan & Menulis", "KMBM"),
        SubjectInfo("Literasi Bahasa Indonesia", "LBI"),
        SubjectInfo("Literasi Bahasa Inggris", "LBE")
    )

    fun displayName(code: String): String {
        return subjects.firstOrNull {
            it.code.equals(code, ignoreCase = true)
        }?.name ?: code
    }
}
