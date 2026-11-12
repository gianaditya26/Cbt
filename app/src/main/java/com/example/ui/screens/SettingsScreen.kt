package com.example.ui.screens

import android.app.Activity
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.ExamLogEntry
import com.example.security.KioskManager
import com.example.security.KioskStatus
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkAccentCyan
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.ThemeMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
  currentTheme: ThemeMode,
  kioskStatus: KioskStatus,
  enforceHttps: Boolean,
  allowAllHttps: Boolean,
  allowedDomains: Set<String>,
  examHistory: List<ExamLogEntry>,
  onThemeChanged: (ThemeMode) -> Unit,
  onEnforceHttpsChanged: (Boolean) -> Unit,
  onAllowAllHttpsChanged: (Boolean) -> Unit,
  onAddDomain: (String) -> Unit,
  onRemoveDomain: (String) -> Unit,
  onNavigateBack: () -> Unit
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  var showAdminDialog by remember { mutableStateOf(false) }
  var showIosGuideDialog by remember { mutableStateOf(false) }
  var newDomainText by remember { mutableStateOf("") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("screen_settings")
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
          modifier = Modifier.testTag("button_settings_back")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Kembali",
            tint = MaterialTheme.colorScheme.onSurface
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "PENGATURAN (SETTINGS)",
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
        .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
      // SECTION A: APPEARANCE
      SectionTitle(icon = Icons.Default.Palette, title = "A. Appearance / Tampilan")
      Spacer(modifier = Modifier.height(10.dp))
      GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Pilih tema aplikasi yang tersimpan otomatis:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(14.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            ThemeToggleCard(
              title = "Dark Mode",
              icon = Icons.Default.DarkMode,
              isSelected = currentTheme == ThemeMode.DARK,
              onClick = { onThemeChanged(ThemeMode.DARK) },
              modifier = Modifier.weight(1f),
              tag = "settings_theme_dark"
            )
            ThemeToggleCard(
              title = "White Mode",
              icon = Icons.Default.LightMode,
              isSelected = currentTheme == ThemeMode.LIGHT,
              onClick = { onThemeChanged(ThemeMode.LIGHT) },
              modifier = Modifier.weight(1f),
              tag = "settings_theme_light"
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // SECTION B: EXAM SECURITY & KIOSK
      SectionTitle(icon = Icons.Default.Shield, title = "B. Exam Security & Kiosk Mode")
      Spacer(modifier = Modifier.height(10.dp))
      GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Status Kiosk Mode",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              val desc = when (kioskStatus) {
                KioskStatus.ACTIVE_LOCKED -> "Aktif (Terkunci oleh Device Owner resmi)"
                KioskStatus.ACTIVE_PINNED -> "Aktif (App Pinning sistem Android)"
                KioskStatus.INACTIVE -> "Siap (Tersedia untuk dimulai saat ujian)"
                KioskStatus.NOT_CONFIGURED -> "Perangkat belum diset Device Owner via ADB"
              }
              Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (kioskStatus == KioskStatus.ACTIVE_LOCKED || kioskStatus == KioskStatus.ACTIVE_PINNED) StatusSuccess.copy(alpha = 0.15f) else StatusWarning.copy(alpha = 0.15f)
            ) {
              Text(
                text = if (kioskStatus == KioskStatus.ACTIVE_LOCKED || kioskStatus == KioskStatus.ACTIVE_PINNED) "AKTIF" else "STANDAR",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (kioskStatus == KioskStatus.ACTIVE_LOCKED || kioskStatus == KioskStatus.ACTIVE_PINNED) StatusSuccess else StatusWarning,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Enforce HTTPS Switch
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Wajibkan Protokol HTTPS",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Blokir website ujian HTTP tanpa enkripsi",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Switch(
              checked = enforceHttps,
              onCheckedChange = onEnforceHttpsChanged,
              modifier = Modifier.testTag("switch_enforce_https")
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Allow All HTTPS Switch
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Izinkan Semua Domain HTTPS",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Jika nonaktif, hanya domain di Whitelist yang diizinkan",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Switch(
              checked = allowAllHttps,
              onCheckedChange = onAllowAllHttpsChanged,
              modifier = Modifier.testTag("switch_allow_all_https")
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Whitelist Management
          Text(
            text = "Daftar Domain Whitelist Sekolah (${allowedDomains.size}):",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(6.dp))

          allowedDomains.forEach { domain ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "• $domain",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
              )
              IconButton(
                onClick = { onRemoveDomain(domain) },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Delete,
                  contentDescription = "Hapus",
                  tint = StatusDanger,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = newDomainText,
              onValueChange = { newDomainText = it },
              placeholder = { Text("tambah-domain.sch.id", fontSize = 12.sp) },
              modifier = Modifier.weight(1f),
              singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                if (newDomainText.isNotBlank()) {
                  onAddDomain(newDomainText)
                  newDomainText = ""
                }
              },
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Buttons for Admin Guide and iOS Setup
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { showAdminDialog = true },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.primary),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Panduan Admin ADB", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = { showIosGuideDialog = true },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.primary),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.PhoneIphone, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Panduan iOS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // SECTION C: PERMISSIONS
      SectionTitle(icon = Icons.Default.Lock, title = "C. Status Perizinan (Permissions)")
      Spacer(modifier = Modifier.height(10.dp))
      GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
          StatusRow(icon = Icons.Default.CameraAlt, title = "Izin Kamera", status = "Aktif (QR Scanner)")
          Spacer(modifier = Modifier.height(8.dp))
          StatusRow(icon = Icons.Default.Wifi, title = "Izin Akses Internet", status = "Aktif (HTTPS CBT)")
          Spacer(modifier = Modifier.height(8.dp))
          StatusRow(icon = Icons.Default.Shield, title = "Lock Task Policy", status = "Terkonfigurasi")
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // SECTION D: EXAM HISTORY
      if (examHistory.isNotEmpty()) {
        SectionTitle(icon = Icons.Default.History, title = "D. Riwayat Sesi Ujian Selesai")
        Spacer(modifier = Modifier.height(10.dp))
        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18.dp) {
          Column(modifier = Modifier.padding(16.dp)) {
            val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            examHistory.take(5).forEach { entry ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = entry.url,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                  )
                  Text(
                    text = formatter.format(Date(entry.timestamp)),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = StatusSuccess.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = "Selesai",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = StatusSuccess,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(24.dp))
      }

      // SECTION E: ABOUT APPLICATION (IDENTITAS WAJIB)
      SectionTitle(icon = Icons.Default.Info, title = "E. About Application / Tentang Aplikasi")
      Spacer(modifier = Modifier.height(10.dp))
      GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18.dp) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "EXAMBROWSER",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Versi 1.0.0 (Official School Edition)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "Developed by Gian Aditya",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "© 2026 Gian Aditya. All Rights Reserved.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(30.dp))
    }

    // Modal: Admin ADB Guide
    if (showAdminDialog) {
      Dialog(onDismissRequest = { showAdminDialog = false }) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = MaterialTheme.colorScheme.surface,
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            Text(
              text = "PANDUAN ADMINISTRATOR SEKOLAH",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Untuk mengaktifkan Kiosk LockTaskMode penuh tanpa tombol recent/home di Android tablet/HP sekolah, jalankan perintah ADB berikut:",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            val adbCmd = KioskManager.getAdbCommand(context)
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = adbCmd,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(10.dp)
              )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = {
                clipboardManager.setText(AnnotatedString(adbCmd))
                showAdminDialog = false
              },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("SALIN PERINTAH ADB")
            }
          }
        }
      }
    }

    // Modal: iOS Deployment & Guided Access Guide
    if (showIosGuideDialog) {
      Dialog(onDismissRequest = { showIosGuideDialog = false }) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = MaterialTheme.colorScheme.surface,
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            Text(
              text = "PANDUAN EXAMBROWSER UNTUK iOS",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Pada platform Apple iPhone dan iPad, sistem keamanan ujian resmi didukung melalui:\n\n1. Akses Terpandu (Guided Access):\nAktifkan di Pengaturan iOS > Aksesibilitas > Akses Terpandu. Tekan tombol samping 3x saat ujian untuk mengunci perangkat.\n\n2. Apple Configurator / MDM Single App Mode:\nUntuk perangkat sekolah yang dikelola secara terpusat, terapkan profil Single App Mode (Autonomous Single App Mode - ASAM) yang mengunci aplikasi EXAMBROWSER selama sesi ujian berlangsung.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = { showIosGuideDialog = false },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("TUTUP")
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SectionTitle(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onBackground
    )
  }
}

@Composable
private fun ThemeToggleCard(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  tag: String
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
    border = androidx.compose.foundation.BorderStroke(
      if (isSelected) 2.dp else 1.dp,
      if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    ),
    modifier = modifier
      .clickable(onClick = onClick)
      .testTag(tag)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(26.dp)
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
private fun StatusRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  status: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = null,
        tint = StatusSuccess,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = status,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
        color = StatusSuccess
      )
    }
  }
}
