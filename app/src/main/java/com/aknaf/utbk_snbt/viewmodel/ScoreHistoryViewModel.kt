package com.aknaf.utbk_snbt.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.aknaf.utbk_snbt.model.ScoreModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ScoreHistoryViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var scoreHistory =
        mutableStateOf<List<ScoreModel>>(emptyList())

    var isLoading = mutableStateOf(true)
    var errorMessage = mutableStateOf<String?>(null)

    init {
        fetchHistory()
    }

    fun fetchHistory() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            scoreHistory.value = emptyList()
            isLoading.value = false
            errorMessage.value = "User belum login."
            return
        }

        isLoading.value = true
        errorMessage.value = null

        db.collection("user_scores")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(
                    ScoreModel::class.java
                )

                scoreHistory.value =
                    list.sortedByDescending { it.timestamp }

                isLoading.value = false

                Log.d(
                    "HISTORY_SUCCESS",
                    "Riwayat ditemukan: ${list.size}"
                )
            }
            .addOnFailureListener { error ->
                scoreHistory.value = emptyList()
                isLoading.value = false
                errorMessage.value =
                    "Gagal mengambil riwayat: ${error.message}"

                Log.e(
                    "HISTORY_ERROR",
                    "Gagal mengambil riwayat",
                    error
                )
            }
    }
}
