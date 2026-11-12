package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.UrlSecurityValidator
import com.example.security.UrlValidationResult
import com.example.ui.components.GlassCard
import com.example.ui.components.UrlConfirmDialog
import com.example.ui.theme.StatusDanger

@Composable
fun ManualUrlScreen(
  initialUrl: String = "",
  enforceHttps: Boolean,
  allowedDomains: Set<String>,
  allowAllHttps: Boolean,
  onNavigateBack: () -> Unit,
  onExamConfirmed: (String) -> Unit
) {
  val clipboardManager = LocalClipboardManager.current
  val keyboardController = LocalSoftwareKeyboardController.current

  var urlText by remember { mutableStateOf(initialUrl) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var pendingConfirmUrl by remember { mutableStateOf<String?>(null) }
  var pendingConfirmHost by remember { mutableStateOf("") }

  fun executeValidationAndProceed() {
    errorMessage = null
    keyboardController?.hide()

    val result = UrlSecurityValidator.validate(
      input = urlText,
      enforceHttps = enforceHttps,
      allowedDomains = allowedDomains,
      allowAllHttps = allowAllHttps
    )

    when (result) {
      is UrlValidationResult.Valid -> {
        pendingConfirmUrl = result.normalizedUrl
        pendingConfirmHost = result.host
      }
      is UrlValidationResult.Warning -> {
        pendingConfirmUrl = result.normalizedUrl
        pendingConfirmHost = result.host
      }
      is UrlValidationResult.Invalid -> {
        errorMessage = result.reason
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("screen_manual_url")
  ) {
    // Header
    Surface(
      color = MaterialTheme.colorScheme.surface,
      shadowElevation = 3.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier.testTag("button_manual_back")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Kembali",
            tint = MaterialTheme.colorScheme.onSurface
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "INPUT LINK UJIAN",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          ),
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }

    // Body
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
        modifier = Modifier.size(64.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Link,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      Text(
        text = "INPUT LINK UJIAN",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "\"Masukkan alamat website ujian yang diberikan oleh guru atau pengawas.\"",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Input field card
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          OutlinedTextField(
            value = urlText,
            onValueChange = {
              urlText = it
              if (errorMessage != null) errorMessage = null
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_exam_url"),
            placeholder = {
              Text("https://contoh-ujian.com")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Uri,
              imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(
              onGo = { executeValidationAndProceed() }
            ),
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
            },
            trailingIcon = {
              if (urlText.isNotEmpty()) {
                IconButton(
                  onClick = { urlText = "" },
                  modifier = Modifier.testTag("button_clear_url")
                ) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Hapus teks",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Clipboard Paste Button
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clickable {
                  val clipText = clipboardManager.getText()?.text
                  if (!clipText.isNullOrBlank()) {
                    urlText = clipText.trim()
                    errorMessage = null
                  }
                }
                .testTag("button_paste_clipboard")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.ContentPaste,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp),
                  tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Tempel dari Clipboard",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }

          // Error banner if any
          errorMessage?.let { msg ->
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = StatusDanger.copy(alpha = 0.12f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.ErrorOutline,
                  contentDescription = null,
                  tint = StatusDanger,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = msg,
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                  color = StatusDanger
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Preset / Quick Suggestions
      Text(
        text = "Contoh Portal Ujian:",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(8.dp))

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PresetUrlRow(
          title = "Google Forms CBT",
          url = "https://docs.google.com/forms",
          onClick = { urlText = "https://docs.google.com/forms" }
        )
        PresetUrlRow(
          title = "Quizizz Exam Portal",
          url = "https://quizizz.com/join",
          onClick = { urlText = "https://quizizz.com/join" }
        )
        PresetUrlRow(
          title = "Simulasi Server CBT Lokal",
          url = "https://cbt.sekolah.id",
          onClick = { urlText = "https://cbt.sekolah.id" }
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Main Button: MULAI UJIAN
      Button(
        onClick = { executeValidationAndProceed() },
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .testTag("button_start_exam"),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = null,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "MULAI UJIAN",
          style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        )
      }
    }

    // Confirmation Modal before entering Fullscreen Exam
    pendingConfirmUrl?.let { url ->
      UrlConfirmDialog(
        url = url,
        host = pendingConfirmHost,
        onDismiss = { pendingConfirmUrl = null },
        onConfirmStart = {
          val target = url
          pendingConfirmUrl = null
          onExamConfirmed(target)
        }
      )
    }
  }
}

@Composable
private fun PresetUrlRow(
  title: String,
  url: String,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = url,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.primary
        )
      }
      Text(
        text = "PILIH",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )
    }
  }
}
