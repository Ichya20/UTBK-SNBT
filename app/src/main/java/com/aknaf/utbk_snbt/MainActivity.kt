package com.aknaf.utbk_snbt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import com.aknaf.utbk_snbt.ui.theme.UTBKSNBTTheme
import com.aknaf.utbk_snbt.utils.FirestoreSeeder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirestoreSeeder.seedIfNeeded()
        setContent {
            UTBKSNBTTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigator(Screen1())
                }
            }
        }
    }
}

@Composable
fun Utbk() {


//            Column(
//                modifier = Modifier
//                    .offset(y = 10.dp)
//                    .fillMaxWidth()
//                    .align(Alignment.CenterHorizontally)
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .padding(2.dp)
//                            .size(150.dp)
//                            .clip(RoundedCornerShape(20.dp))
//                            .background(Color.Green)
//                    ) {
//                        Image(
//                            painter = painterResource(id = R.drawable.book_pile_clipart_transparent_png_hd__pile_of_books_3d_icon_education_and_student_concept__education__b_removebg_preview),
//                            contentDescription = null,
//                            Modifier
//                                .fillMaxSize()
//                                .background(Color.Black.copy(alpha = 0.3f)),
//                            contentScale = ContentScale.Crop,
//
//                            )
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .background(
//                                    brush = Brush.verticalGradient(
//                                        colors = listOf(
//                                            Color.Transparent,
//                                            Color.Black.copy(alpha = 0.2f)
//                                        ),
//                                        startY = 0f,
//                                        endY = 1f
//                                    )
//                                )
//                        )
//                        Text(
//                            text = "Materi",
//                            modifier = Modifier
//                                .align(Alignment.BottomCenter)
//                                .padding(10.dp),
//                            color = Color.White
//                        )
//                    }
//                    Box(
//                        modifier = Modifier
//                            .padding(2.dp)
//                            .size(150.dp)
//                            .clip(RoundedCornerShape(20.dp))
//                            .background(Color.Green)
//                    ) {
//                        Image(
//                            painter = painterResource(id = R.drawable.buku_catatan_dan_ikon_pensil_3d__catatan__notes__memeriksa_png_dan_vektor_dengan_background_transparan__removebg_preview__1_),
//                            contentDescription = null,
//                            Modifier.fillMaxSize(),
//                            contentScale = ContentScale.Crop
//                        )
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .background(
//                                    brush = Brush.verticalGradient(
//                                        colors = listOf(
//                                            Color.Transparent,
//                                            Color.Black.copy(alpha = 0.2f)
//                                        ),
//                                        startY = 0f,
//                                        endY = 1f
//                                    )
//                                )
//                        )
//                        Text(
//                            text = "Latihan",
//                            modifier = Modifier
//                                .align(Alignment.BottomCenter)
//                                .padding(10.dp),
//                            color = Color.White
//                        )
//                    }
//                }
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .padding(2.dp)
//                            .size(150.dp)
//                            .clip(RoundedCornerShape(20.dp))
//                            .background(Color.Green)
//                    ) {
//                        Image(
//                            painter = painterResource(id = R.drawable.premium_vector___copywriting_writing_icon_creative_writing_and_storytelling_3d_vector_illustration_removebg_preview),
//                            contentDescription = null,
//                            Modifier
//                                .fillMaxSize()
//                                .background(Color.Black.copy(alpha = 0.3f)),
//                            contentScale = ContentScale.Crop,
//
//                            )
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .background(
//                                    brush = Brush.verticalGradient(
//                                        colors = listOf(
//                                            Color.Transparent,
//                                            Color.Black.copy(alpha = 0.2f)
//                                        ),
//                                        startY = 0f,
//                                        endY = 1f
//                                    )
//                                )
//                        )
//                        Text(
//                            text = "Tips",
//                            modifier = Modifier
//                                .align(Alignment.BottomCenter)
//                                .padding(10.dp),
//                            color = Color.White
//                        )
//                    }
//                    Box(
//                        modifier = Modifier
//                            .padding(2.dp)
//                            .size(150.dp)
//                            .clip(RoundedCornerShape(20.dp))
//                            .background(Color.Green)
//                    ) {
//                        Image(
//                            painter = painterResource(id = R.drawable.premium_psd___calendar_date_with_alarm_clock_front_view_3d_rendering_illustration_icon_with_transparent_removebg_preview),
//                            contentDescription = null,
//                            Modifier.fillMaxSize(),
//                            contentScale = ContentScale.Crop
//                        )
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .background(
//                                    brush = Brush.verticalGradient(
//                                        colors = listOf(
//                                            Color.Transparent,
//                                            Color.Black.copy(alpha = 0.2f)
//                                        ),
//                                        startY = 0f,
//                                        endY = 1f
//                                    )
//                                )
//                        )
//                        Text(
//                            text = "Jadwal",
//                            modifier = Modifier
//                                .align(Alignment.BottomCenter)
//                                .padding(10.dp),
//                            color = Color.White
//                        )
//                    }
//                }
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .padding(2.dp)
//                            .size(150.dp)
//                            .clip(RoundedCornerShape(20.dp))
//                            .background(Color.Green)
//                    ) {
//                        Image(
//                            painter = painterResource(id = R.drawable.ic_launcher_background),
//                            contentDescription = null,
//                            Modifier
//                                .fillMaxSize()
//                                .background(Color.Black.copy(alpha = 0.3f)),
//                            contentScale = ContentScale.Crop,
//
//                            )
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .background(
//                                    brush = Brush.verticalGradient(
//                                        colors = listOf(
//                                            Color.Transparent,
//                                            Color.Black.copy(alpha = 0.2f)
//                                        ),
//                                        startY = 0f,
//                                        endY = 1f
//                                    )
//                                )
//                        )
//                        Text(
//                            text = "Motivasi",
//                            modifier = Modifier
//                                .align(Alignment.BottomCenter)
//                                .padding(10.dp),
//                            color = Color.White
//                        )
//                    }
//                    Box(
//                        modifier = Modifier
//                            .padding(2.dp)
//                            .size(150.dp)
//                            .clip(RoundedCornerShape(20.dp))
//                            .background(Color.Green)
//                    ) {
//                        Image(
//                            painter = painterResource(id = R.drawable.help__3d_icon_download_in_png__obj_or_blend_format_removebg_preview),
//                            contentDescription = null,
//                            Modifier.fillMaxSize(),
//                            contentScale = ContentScale.Crop
//                        )
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .background(
//                                    brush = Brush.verticalGradient(
//                                        colors = listOf(
//                                            Color.Transparent,
//                                            Color.Black.copy(alpha = 0.2f)
//                                        ),
//                                        startY = 0f,
//                                        endY = 1f
//                                    )
//                                )
//                        )
//                        Text(
//                            text = "Tentang",
//                            modifier = Modifier
//                                .align(Alignment.BottomCenter)
//                                .padding(10.dp),
//                            color = Color.White
//                        )
//                    }
//                }
//            }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UTBKSNBTTheme {
        Utbk()
    }
}