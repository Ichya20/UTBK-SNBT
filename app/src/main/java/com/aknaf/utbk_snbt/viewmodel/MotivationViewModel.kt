package com.aknaf.utbk_snbt.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class MotivationViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // Default quote kalau internet mati atau Firebase lagi kosong
    var currentQuote = mutableStateOf("Today is a good day to learn something new")

    init {
        fetchRandomQuote()
    }

    fun fetchRandomQuote() {
        db.collection("motivations")
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    // Ambil semua teks dari Firebase
                    val quotesList = result.documents.mapNotNull { it.getString("text") }

                    // Kalau datanya ada, acak dan pilih 1
                    if (quotesList.isNotEmpty()) {
                        currentQuote.value = quotesList.random()
                    }
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("MOTIVATION_ERROR", "Gagal tarik motivasi: ${e.message}")
            }
    }
}