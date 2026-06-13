package com.aknaf.utbk_snbt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

class Lindo: Screen {
    @Composable
    override fun Content() {
        val materilist = listOf(
            "Ide Pokok Paragraf",
            "Fakta, Opini, & Kesimpulan"
        )
        val navigator = LocalNavigator.current

        Box(modifier = Modifier.background(color = Color(android.graphics.Color.parseColor("#2B2B6E")))) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.padding(15.dp)) {
                    IconButton(onClick = { navigator?.pop() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "Literasi Bahasa Indonesia",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp)).background(color = Color.White)) {
                    ElevatedCard(modifier = Modifier.fillMaxWidth().padding(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                        LazyColumn(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                            itemsIndexed(materilist) { index, item ->
                                Column(modifier = Modifier.clickable { navigator?.push(LindoMateri("$index")) }) {
                                    Text(text = item, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(15.dp))
                                    Divider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}