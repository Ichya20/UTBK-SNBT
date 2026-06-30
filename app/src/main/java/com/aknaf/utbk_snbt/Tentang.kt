package com.aknaf.utbk_snbt

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.aknaf.utbk_snbt.screen.HelpDeskScreen

class Tentang : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.bgtentang2),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize(),
                colorFilter = ColorFilter.colorMatrix(
                    colorMatrix = ColorMatrix().apply { Color.Black.alpha }
                )
            )

            Image(
                painter = painterResource(
                    id = R.drawable._ea2ddf67_d6e2_4091_b342_8e3ca07650ae
                ),
                contentDescription = stringResource(id = R.string.app_name),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .offset(x = 105.dp, y = 135.dp)
                    .size(150.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = 100.dp)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Tentang Aplikasi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Aplikasi UTBK-SNBT ini dirancang untuk membantu siswa " +
                        "mempersiapkan diri menghadapi ujian seleksi masuk perguruan tinggi.",
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Fitur utama:",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "- Pembahasan soal\n" +
                        "- Simulasi ujian\n" +
                        "- Tips & strategi belajar\n" +
                        "- Jadwal\n" +
                        "- Motivasi",
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        navigator?.push(HelpDeskScreen())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2B2B6E)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Pusat Bantuan / Help Desk",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Buka halaman ini untuk melihat panduan penggunaan " +
                        "atau melaporkan kendala aplikasi.",
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
        }
    }
}
