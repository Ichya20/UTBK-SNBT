package com.aknaf.utbk_snbt.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object DynamicDataSeeder {
    fun seedTextData() {
        val db = FirebaseFirestore.getInstance()
        pushBulkQuestions(db)
    }

    private fun pushBulkQuestions(db: FirebaseFirestore) {
        val daftarSoal = listOf(
            // Kuncinya HARUS pakai format "optionX" biar kebaca bener di Android
            mapOf("subject" to "PU", "question" to "Jika hari hujan, maka jalan basah. Ternyata jalan tidak basah. Kesimpulannya?", "optionA" to "Hari hujan", "optionB" to "Hari tidak hujan", "optionC" to "Jalan rusak", "optionD" to "Mendung", "optionE" to "Tidak ada kesimpulan", "answer" to "optionB", "explanation" to "Menggunakan hukum Modus Tollens."),

            mapOf("subject" to "PK", "question" to "Berapakah nilai dari 2^3 + 3^2?", "optionA" to "12", "optionB" to "15", "optionC" to "17", "optionD" to "25", "optionE" to "18", "answer" to "optionC", "explanation" to "8 + 9 = 17."),

            mapOf("subject" to "PPU", "question" to "Sinonim dari kata 'EVALUASI' adalah...", "optionA" to "Penilaian", "optionB" to "Perencanaan", "optionC" to "Pengabaian", "optionD" to "Pelaksanaan", "optionE" to "Pengawasan", "answer" to "optionA", "explanation" to "Evaluasi bermakna penilaian atau pengujian."),

            mapOf("subject" to "PBM", "question" to "Manakah penulisan kata baku yang tepat?", "optionA" to "Apotik", "optionB" to "Analisa", "optionC" to "Izin", "optionD" to "Nasehat", "optionE" to "Praktek", "answer" to "optionC", "explanation" to "Bentuk bakunya adalah Izin (pakai z)."),

            mapOf("subject" to "PM", "question" to "Harga baju 100rb diskon 20%. Berapa harga setelah diskon?", "optionA" to "80rb", "optionB" to "90rb", "optionC" to "70rb", "optionD" to "85rb", "optionE" to "75rb", "answer" to "optionA", "explanation" to "Diskon 20% dari 100rb adalah 20rb. Maka 100-20 = 80rb."),

            mapOf("subject" to "LIT_BI", "question" to "Ide pokok paragraf tersebut adalah...", "optionA" to "Pentingnya olahraga", "optionB" to "Manfaat tidur", "optionC" to "Cara diet sehat", "optionD" to "Dampak merokok", "optionE" to "Ciri-ciri stres", "answer" to "optionA", "explanation" to "Paragraf tersebut secara konsisten membahas manfaat fisik dari olahraga."),

            mapOf("subject" to "LIT_EN", "question" to "What is the synonym of the word 'HAPPY'?", "optionA" to "Sad", "optionB" to "Angry", "optionC" to "Joyful", "optionD" to "Sleepy", "optionE" to "Bored", "answer" to "optionC", "explanation" to "Joyful means very happy.")
        )

        daftarSoal.forEach { soal ->
            val docId = "sample_${soal["subject"]}"
            db.collection("dynamic_questions").document(docId).set(soal)
                .addOnSuccessListener { Log.d("SEEDER", "Berhasil update data sample untuk: ${soal["subject"]}") }
                .addOnFailureListener { e -> Log.e("SEEDER", "Gagal: ${e.message}") }
        }
    }
}