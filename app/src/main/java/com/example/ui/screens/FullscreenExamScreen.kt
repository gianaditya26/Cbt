package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.components.ExitConfirmDialog
import com.example.ui.components.OfflineBanner
import com.example.ui.theme.DarkAccentCyan
import com.example.ui.theme.StatusDanger
import kotlin.math.roundToInt

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FullscreenExamScreen(
  examUrl: String,
  isOnline: Boolean,
  onExitConfirmed: () -> Unit
) {
  var showExitConfirmDialog by remember { mutableStateOf(false) }
  var webViewInstance by remember { mutableStateOf<WebView?>(null) }
  var pageProgress by remember { mutableIntStateOf(0) }
  var isInitialPageLoaded by remember { mutableStateOf(false) }
  var hasPageError by remember { mutableStateOf(false) }

  // Drag offset for the small floating exit pill so it never blocks an exam question/radio button
  var offsetX by remember { mutableFloatStateOf(0f) }
  var offsetY by remember { mutableFloatStateOf(0f) }

  // Prevent back button from accidentally closing the exam; it prompts confirmation instead
  BackHandler(enabled = true) {
    showExitConfirmDialog = true
  }

  DisposableEffect(Unit) {
    onDispose {
      webViewInstance?.apply {
        stopLoading()
        clearHistory()
        removeAllViews()
        destroy()
      }
      webViewInstance = null
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White)
      .testTag("screen_fullscreen_exam")
  ) {
    // 1. FULLSCREEN EXAM WEBVIEW (Fills 100% of Screen)
    AndroidView(
      modifier = Modifier
        .fillMaxSize()
        .testTag("exam_webview"),
      factory = { ctx ->
        WebView(ctx).apply {
          layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )

          // Enable cookies for student session persistence
          CookieManager.getInstance().setAcceptCookie(true)
          CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

          settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = true
            displayZoomControls = false
            setSupportZoom(true)
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            cacheMode = WebSettings.LOAD_DEFAULT
            allowFileAccess = false
            allowContentAccess = false
            userAgentString = "$userAgentString EXAMBROWSER/1.0.0 (School Edition; Android)"
          }

          webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
              super.onProgressChanged(view, newProgress)
              pageProgress = newProgress
              if (newProgress >= 100) {
                isInitialPageLoaded = true
              }
            }
          }

          webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
              super.onPageStarted(view, url, favicon)
              hasPageError = false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
              super.onPageFinished(view, url)
              isInitialPageLoaded = true
            }

            override fun onReceivedError(
              view: WebView?,
              request: WebResourceRequest?,
              error: WebResourceError?
            ) {
              super.onReceivedError(view, request, error)
              if (request?.isForMainFrame == true) {
                hasPageError = true
              }
            }

            override fun shouldOverrideUrlLoading(
              view: WebView?,
              request: WebResourceRequest?
            ): Boolean {
              val targetUrl = request?.url?.toString() ?: return false
              val scheme = request.url.scheme?.lowercase() ?: ""

              // Anti-cheat: Block opening external messenger/apps (WhatsApp, Telegram, YouTube, Intent)
              if (scheme != "http" && scheme != "https") {
                return true // Block external schemes
              }

              return false // Load inside this WebView
            }
          }

          loadUrl(examUrl)
          webViewInstance = this
        }
      },
      update = { wv ->
        // No-op to preserve active exam form inputs and DOM state
      }
    )

    // Subtle 2dp loading indicator at the very top edge during initial page load
    if (pageProgress < 100) {
      LinearProgressIndicator(
        progress = { pageProgress / 100f },
        modifier = Modifier
          .fillMaxWidth()
          .height(3.dp)
          .align(Alignment.TopCenter),
        color = DarkAccentCyan,
        trackColor = Color.Transparent
      )
    }

    // 2. ONLY ONE EXIT BUTTON ("SATU TOMBOL KELUAR")
    // Minimalist, elegant glassmorphic floating pill in top-right corner.
    // Draggable so students/admins can move it if it obscures any question.
    Box(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
        .padding(top = 16.dp, end = 16.dp)
        .pointerInput(Unit) {
          detectDragGestures { change, dragAmount ->
            change.consume()
            offsetX += dragAmount.x
            offsetY += dragAmount.y
          }
        }
    ) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0F172A).copy(alpha = 0.88f),
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155).copy(alpha = 0.8f)),
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .clickable { showExitConfirmDialog = true }
          .testTag("button_exam_exit")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.ExitToApp,
            contentDescription = "Keluar Ujian",
            tint = StatusDanger,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "KELUAR",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp,
              fontSize = 11.sp
            ),
            color = Color.White
          )
        }
      }
    }

    // Network Disconnected / Offline Overlay Banner
    // Does NOT close the exam or wipe answers; lets student retry connection seamlessly
    AnimatedVisibility(
      visible = !isOnline || hasPageError,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 16.dp)
    ) {
      OfflineBanner(
        onRetry = {
          hasPageError = false
          webViewInstance?.reload()
        }
      )
    }

    // Exit Confirmation Dialog
    if (showExitConfirmDialog) {
      ExitConfirmDialog(
        onDismiss = { showExitConfirmDialog = false },
        onConfirmExit = {
          showExitConfirmDialog = false
          onExitConfirmed()
        }
      )
    }
  }
}
