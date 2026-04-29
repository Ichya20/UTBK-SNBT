package com.aknaf.utbk_snbt.repository

import com.aknaf.utbk_snbt.model.QuestionModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class QuizRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getQuestions(): List<QuestionModel> {
        return db.collection("questions")
            .get()
            .await()
            .documents
            .mapNotNull {
                it.toObject(QuestionModel::class.java)
            }
    }
}