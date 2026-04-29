package com.aknaf.utbk_snbt.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.aknaf.utbk_snbt.model.ScoreModel
import com.google.firebase.firestore.FirebaseFirestore

class ScoreHistoryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    var scoreHistory = mutableStateOf<List<ScoreModel>>(emptyList())
    var isLoading = mutableStateOf(true)

    init {
        fetchHistory()
    }

    private fun fetchHistory() {
        val dummyUserId = "ichya_ulumiddiin" // Sesuai dengan yang kita simpan tadi

        db.collection("user_scores")
            .whereEqualTo("userId", dummyUserId)
            .get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(ScoreModel::class.java)
                // Urutkan dari yang terbaru (descending) berdasarkan waktu
                scoreHistory.value = list.sortedByDescending { it.timestamp }
                isLoading.value = false
            }
            .addOnFailureListener {
                isLoading.value = false
                android.util.Log.e("HISTORY_ERROR", "Gagal tarik riwayat")
            }
    }
}