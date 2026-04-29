package com.aknaf.utbk_snbt

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen

class Tentang:Screen {
    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.bgtentang2),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize(),
                colorFilter = ColorFilter.colorMatrix(colorMatrix = ColorMatrix().apply { Color.Black.alpha })
            )
            Image(
                painter = painterResource(id = R.drawable._ea2ddf67_d6e2_4091_b342_8e3ca07650ae),
                contentDescription = stringResource(id = R.string.app_name),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .offset(x = 105.dp, y = 135.dp)
                    .size(150.dp)
                    .clip(CircleShape)
            )
            Column(modifier = Modifier.fillMaxSize().offset(y = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text(
                    text = "Tentang Aplikasi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "Aplikasi UTBK-SNBT ini dirancang untuk membantu siswa dalam mempersiapkan diri menghadapi ujian seleksi masuk perguruan tinggi.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = Color.Black
                )

                Text(
                    text = "Fitur utama:",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "- Pembahasan soal\n- Simulasi ujian\n- Tips & strategi belajar\n- Jadwal\n- Motivasi",
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
//                Text(
//                    text = "Hubungi saya:",
//                    textAlign = TextAlign.Center,
//                    fontWeight = FontWeight.Bold
//
            }

        }
    }
}