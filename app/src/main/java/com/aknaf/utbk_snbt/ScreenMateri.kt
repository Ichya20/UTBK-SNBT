package com.aknaf.utbk_snbt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

class ScreenMateri: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        Box(modifier = Modifier.background(color = Color(android.graphics.Color.parseColor("#2B2B6E")))){
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.padding(15.dp)
                ) {
                    IconButton(onClick = {navigator?.pop()}) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Materi 7 Sub-Test",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp))
                        .background(color = Color.White)
                        .size(700.dp)
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(15.dp)
                    ){
                        item{
                            ElevatedCard(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp
                                ),
                                modifier = Modifier
                                    .padding(6.dp)
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                Text(
                                    text = "Pengetahuan Kuantitatif",
                                    modifier = Modifier
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Button(
                                    onClick = { navigator?.push(PKuantitatif()) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(text = "Masuk")
                                }
                            }
                            ElevatedCard(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp
                                ),
                                modifier = Modifier
                                    .padding(6.dp)
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                Text(
                                    text = "Penalaran Matematika",
                                    modifier = Modifier
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Button(
                                    onClick = { navigator?.push(PMatematika()) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(text = "Masuk")
                                }
                            }
                            ElevatedCard(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp
                                ),
                                modifier = Modifier
                                    .padding(6.dp)
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                Text(
                                    text = "Penalaran Umum",
                                    modifier = Modifier
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Button(
                                    onClick = { navigator?.push(PUmum()) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(text = "Masuk")
                                }
                            }
                            ElevatedCard(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp
                                ),
                                modifier = Modifier
                                    .padding(6.dp)
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                Text(
                                    text = "Pengetahuan Pemahaman Umum",
                                    modifier = Modifier
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Button(
                                    onClick = { navigator?.push(PKuantitatif()) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(text = "Masuk")
                                }
                            }
                            ElevatedCard(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp
                                ),
                                modifier = Modifier
                                    .padding(6.dp)
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                Text(
                                    text = "KMBM",
                                    modifier = Modifier
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Button(
                                    onClick = { navigator?.push(PKuantitatif()) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(text = "Masuk")
                                }
                            }
                            ElevatedCard(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp
                                ),
                                modifier = Modifier
                                    .padding(6.dp)
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                Text(
                                    text = "Literasi Bahasa Indonesia",
                                    modifier = Modifier
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Button(
                                    onClick = { navigator?.push(PMatematika()) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(text = "Masuk")
                                }
                            }
                            ElevatedCard(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp
                                ),
                                modifier = Modifier
                                    .padding(6.dp)
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                Text(
                                    text = "Literasi Bahasa Inggris",
                                    modifier = Modifier
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Button(
                                    onClick = { navigator?.push(PKuantitatif()) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(text = "Masuk")
                                }
                            }
                        }
                    }
                }

            }
        }

    }
}