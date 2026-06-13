package com.aknaf.utbk_snbt

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import cafe.adriel.voyager.core.screen.Screen

class LinggrisMateri(val index: String): Screen {
    @Composable
    override fun Content() {
        AndroidView(factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                // Memanggil folder Linggris
                loadUrl("file:///android_asset/Linggris/$index.html")
            }
        }, modifier = Modifier.fillMaxSize())
    }
}