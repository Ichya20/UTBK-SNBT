package com.aknaf.utbk_snbt.screen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

class HelpDeskScreen : Screen {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.current

        /*
         * Isi data kontak admin sebelum aplikasi dirilis.
         *
         * Contoh nomor WhatsApp:
         * private val adminWhatsAppNumber = "6281234567890"
         *
         * Jangan memakai tanda +, spasi, atau tanda hubung.
         */
        val adminWhatsAppNumber = "6285602305477"
        val adminEmail = "restimardiana611@gmail.com"

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FB))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = "‹ Kembali",
                color = Color(0xFF2B2B6E),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navigator?.pop()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Pusat Bantuan",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2B2B6E)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Help Desk digunakan untuk mencari panduan penggunaan aplikasi " +
                    "dan menghubungi admin ketika kamu mengalami kendala.",
                color = Color(0xFF333333),
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            HelpSectionCard(
                title = "Help Desk bisa digunakan untuk:",
                items = listOf(
                    "Masalah login atau pendaftaran akun",
                    "Simulasi tidak dapat dibuka atau diselesaikan",
                    "Nilai atau riwayat tidak muncul",
                    "Jadwal dan notifikasi tidak berjalan",
                    "Chatbot AI tidak merespons",
                    "Melaporkan error lain pada aplikasi"
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pertanyaan Umum",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2B2B6E)
            )

            Spacer(modifier = Modifier.height(10.dp))

            FaqItem(
                question = "Aplikasi tiba-tiba error. Apa yang harus dilakukan?",
                answer = "Tutup aplikasi, buka kembali, lalu pastikan koneksi internet aktif. " +
                    "Jika masih error, kirim laporan ke admin dan sertakan tangkapan layar."
            )

            FaqItem(
                question = "Nilai simulasi belum muncul.",
                answer = "Pastikan proses pengumpulan jawaban sudah selesai. " +
                    "Coba buka kembali menu riwayat setelah beberapa saat."
            )

            FaqItem(
                question = "Chatbot AI tidak menjawab.",
                answer = "Periksa koneksi internet lalu tekan coba lagi. " +
                    "Jika berulang, laporkan kendala melalui tombol kontak admin."
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Hubungi Admin",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2B2B6E)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (adminWhatsAppNumber.isBlank()) {
                        Toast.makeText(
                            context,
                            "Nomor WhatsApp admin belum dikonfigurasi.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val message = Uri.encode(
                            "Halo Admin UTBK-SNBT, saya ingin melaporkan kendala aplikasi."
                        )
                        val uri = Uri.parse(
                            "https://wa.me/$adminWhatsAppNumber?text=$message"
                        )
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF25D366)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Hubungi melalui WhatsApp",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (adminEmail.isBlank()) {
                        Toast.makeText(
                            context,
                            "Email admin belum dikonfigurasi.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:$adminEmail")
                            putExtra(
                                Intent.EXTRA_SUBJECT,
                                "Laporan Kendala Aplikasi UTBK-SNBT"
                            )
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Halo Admin,\n\nSaya mengalami kendala berikut:\n"
                            )
                        }
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2B2B6E)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Kirim Email",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Saat melapor, sertakan nama, jenis perangkat, bagian yang error, " +
                    "dan tangkapan layar agar kendala lebih cepat diperiksa.",
                color = Color(0xFF666666),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HelpSectionCard(
    title: String,
    items: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2B2B6E),
                fontSize = 16.sp
            )

            items.forEach { item ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "• ",
                        color = Color(0xFF57BF4B),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item,
                        color = Color(0xFF333333),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FaqItem(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable {
                expanded = !expanded
            },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = question,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    fontSize = 14.sp
                )

                Text(
                    text = if (expanded) "−" else "+",
                    color = Color(0xFF2B2B6E),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE5E5E5))
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = answer,
                    color = Color(0xFF555555),
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
fun Box(modifier: Modifier) {
    TODO("Not yet implemented")
}
