package com.aknaf.utbk_snbt.utils

import com.google.firebase.firestore.FirebaseFirestore

object FirestoreSeeder {

    fun seedIfNeeded() {

        val db = FirebaseFirestore.getInstance()

        db.collection("quizzes")
            .limit(1)
            .get()
            .addOnSuccessListener {

                if (it.isEmpty) {
                    seedData(db)
                }

            }
    }

    private fun seedData(db: FirebaseFirestore) {

        db.collection("subjects")
            .document("tps")
            .set(mapOf("name" to "TPS"))

        db.collection("quizzes")
            .document("quiz_001")
            .set(
                mapOf(
                    "title" to "Latihan Soal Part 1",
                    "subject" to "TPS",
                    "totalQuestion" to 31
                )
            )

        for (i in 1..31) {

            val id = "q" + i.toString().padStart(3, '0')

            db.collection("questions")
                .document(id)
                .set(
                    mapOf(
                        "quizId" to "quiz_001",
                        "subject" to "TPS",
                        "imagePath" to "part1/$i.jpg",
                        "order" to i,
                        "type" to "image"
                    )
                )
        }
    }
}