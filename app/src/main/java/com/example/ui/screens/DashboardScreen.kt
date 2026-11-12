package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.security.KioskStatus
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkAccentCyan
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

@Composable
fun DashboardScreen(
  kioskStatus: KioskStatus,
  onNavigateToScanQr: () -> Unit,
  onNavigateToManualUrl: () -> Unit,
  onNavigateToSettings: () -> Unit,
  onQuickStartDemo: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("screen_dashboard")
  ) {
    // Header
    Surface(
      color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
      shadowElevation = 4.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(40.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Image(
                painter = painterResource(id = R.drawable.exambrowser_icon),
                contentDescription = "EXAMBROWSER",
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(10.dp))
              )
            }
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "EXAMBROWSER",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Secure Exam Portal",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        IconButton(
          onClick = onNavigateToSettings,
          modifier = Modifier
            .size(44.dp)
            .testTag("button_dashboard_settings")
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Pengaturan",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }

    // Body Content
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 22.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Security & Kiosk Status Pill
      val (kioskLabel, kioskColor) = when (kioskStatus) {
        KioskStatus.ACTIVE_LOCKED -> "Kiosk Mode Aktif (Device Owner)" to StatusSuccess
        KioskStatus.ACTIVE_PINNED -> "Kiosk Mode Aktif (App Pinning)" to StatusSuccess
        KioskStatus.INACTIVE -> "Kiosk Siap (Lock Task Tersedia)" to StatusSuccess
        KioskStatus.NOT_CONFIGURED -> "Mode Standar (Kiosk Terbuka)" to StatusWarning
      }

      Surface(
        shape = RoundedCornerShape(50.dp),
        color = kioskColor.copy(alpha = 0.14f),
        border = androidx.compose.foundation.BorderStroke(1.dp, kioskColor.copy(alpha = 0.4f)),
        modifier = Modifier.clickable(onClick = onNavigateToSettings)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = kioskColor,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = kioskLabel,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              fontSize = 11.sp
            ),
            color = kioskColor
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Main Title
      Text(
        text = "SELAMAT DATANG DI EXAMBROWSER",
        style = MaterialTheme.typography.headlineLarge.copy(
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = 0.5.sp
        ),
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "\"Silakan pilih metode untuk memulai ujian online.\"",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Two Main Buttons:
      // 1. SCAN QR CODE
      BigActionButton(
        title = "SCAN QR CODE",
        subtitle = "Arahkan kamera ke QR Code lembar ujian",
        icon = Icons.Default.QrCodeScanner,
        gradient = listOf(
          MaterialTheme.colorScheme.primary,
          MaterialTheme.colorScheme.secondary
        ),
        testTag = "button_scan_qr",
        onClick = onNavigateToScanQr
      )

      Spacer(modifier = Modifier.height(16.dp))

      // 2. INPUT MANUAL
      BigActionButton(
        title = "INPUT MANUAL",
        subtitle = "Ketik atau tempel URL link website ujian",
        icon = Icons.Default.Edit,
        gradient = listOf(
          MaterialTheme.colorScheme.surfaceVariant,
          MaterialTheme.colorScheme.surface
        ),
        isSecondary = true,
        testTag = "button_input_manual",
        onClick = onNavigateToManualUrl
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Quick Demo / Presets for immediate testing without typing
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18.dp
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Akses Cepat Ujian Demo (CBT Test Portal)",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Pilih salah satu portal ujian resmi di bawah untuk pengujian langsung:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            QuickChip(
              label = "Google Forms",
              onClick = { onQuickStartDemo("https://docs.google.com/forms") },
              modifier = Modifier.weight(1f)
            )
            QuickChip(
              label = "Quizizz",
              onClick = { onQuickStartDemo("https://quizizz.com/join") },
              modifier = Modifier.weight(1f)
            )
            QuickChip(
              label = "CBT Demo",
              onClick = { onQuickStartDemo("https://cbt-demo.gianaditya.dev") },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }

    // Footer
    Surface(
      color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "© 2026 Gian Aditya. All Rights Reserved.",
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
      }
    }
  }
}

@Composable
private fun BigActionButton(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  gradient: List<Color>,
  isSecondary: Boolean = false,
  testTag: String,
  onClick: () -> Unit
) {
  val shape = RoundedCornerShape(22.dp)

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .height(100.dp)
      .shadow(if (isSecondary) 3.dp else 8.dp, shape)
      .clip(shape)
      .clickable(onClick = onClick)
      .testTag(testTag),
    shape = shape,
    color = if (isSecondary) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary,
    border = if (isSecondary) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)) else null
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          if (isSecondary) {
            Brush.horizontalGradient(
              listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
              )
            )
          } else {
            Brush.horizontalGradient(gradient)
          }
        )
        .padding(horizontal = 20.dp),
      contentAlignment = Alignment.CenterStart
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Surface(
            shape = CircleShape,
            color = if (isSecondary) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.22f),
            modifier = Modifier.size(54.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSecondary) MaterialTheme.colorScheme.primary else Color.White,
                modifier = Modifier.size(30.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(16.dp))

          Column {
            Text(
              text = title,
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
              ),
              color = if (isSecondary) MaterialTheme.colorScheme.onSurface else Color.White
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
              text = subtitle,
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
              color = if (isSecondary) MaterialTheme.colorScheme.onSurfaceVariant else Color.White.copy(alpha = 0.85f)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun QuickChip(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
    modifier = modifier.clickable(onClick = onClick)
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      textAlign = TextAlign.Center
    )
  }
}
