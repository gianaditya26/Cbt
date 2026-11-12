package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkAccentCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.LightAccentBlue
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceVariant
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.LightTextSecondary
import com.example.ui.theme.ThemeMode

@Composable
fun ThemePersonalizationScreen(
  currentTheme: ThemeMode,
  onThemeSelected: (ThemeMode) -> Unit,
  onApplyTheme: () -> Unit
) {
  var selectedTheme by remember {
    mutableStateOf(if (currentTheme == ThemeMode.LIGHT) ThemeMode.LIGHT else ThemeMode.DARK)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 24.dp, vertical = 32.dp)
      .testTag("screen_theme_personalization"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = "PERSONALISASI EXAMBROWSER",
      style = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
      ),
      color = MaterialTheme.colorScheme.onBackground,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "\"Pilih tampilan aplikasi sesuai preferensi Anda.\"",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(28.dp))

    // Theme Choice A: DARK MODE
    ThemeChoiceCard(
      title = "DARK MODE",
      description = "Background hitam / navy, card abu-abu gelap, teks putih, dan aksen biru neon. Modern dan elegan.",
      isSelected = selectedTheme == ThemeMode.DARK,
      accentColor = DarkAccentCyan,
      isDarkPreview = true,
      onClick = {
        selectedTheme = ThemeMode.DARK
        onThemeSelected(ThemeMode.DARK)
      },
      tag = "card_theme_dark"
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Theme Choice B: WHITE MODE
    ThemeChoiceCard(
      title = "WHITE MODE",
      description = "Background putih, card abu-abu sangat muda, teks hitam, dan aksen biru elegan. Bersih dan minimalis.",
      isSelected = selectedTheme == ThemeMode.LIGHT,
      accentColor = LightAccentBlue,
      isDarkPreview = false,
      onClick = {
        selectedTheme = ThemeMode.LIGHT
        onThemeSelected(ThemeMode.LIGHT)
      },
      tag = "card_theme_light"
    )

    Spacer(modifier = Modifier.height(36.dp))

    Button(
      onClick = {
        onThemeSelected(selectedTheme)
        onApplyTheme()
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .testTag("button_apply_theme"),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
      ),
      shape = RoundedCornerShape(16.dp),
      elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
    ) {
      Text(
        text = "GUNAKAN TEMA INI",
        style = MaterialTheme.typography.labelLarge.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "Tema dapat diubah sewaktu-waktu melalui menu Pengaturan (Settings).",
      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
      textAlign = TextAlign.Center
    )
  }
}

@Composable
private fun ThemeChoiceCard(
  title: String,
  description: String,
  isSelected: Boolean,
  accentColor: Color,
  isDarkPreview: Boolean,
  onClick: () -> Unit,
  tag: String
) {
  val cardBg = if (isDarkPreview) DarkSurface else LightSurface
  val borderCol = if (isSelected) accentColor else (if (isDarkPreview) DarkBorder else LightBorder)
  val titleCol = if (isDarkPreview) DarkTextPrimary else LightTextPrimary
  val descCol = if (isDarkPreview) DarkTextSecondary else LightTextSecondary

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .clickable(onClick = onClick)
      .testTag(tag),
    shape = RoundedCornerShape(20.dp),
    color = cardBg,
    border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderCol),
    shadowElevation = if (isSelected) 8.dp else 2.dp
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = accentColor.copy(alpha = 0.15f),
            modifier = Modifier.size(42.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = if (isDarkPreview) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = titleCol
          )
        }

        Icon(
          imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
          contentDescription = null,
          tint = if (isSelected) accentColor else descCol.copy(alpha = 0.5f),
          modifier = Modifier.size(26.dp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        color = descCol
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Live mini UI preview container
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkPreview) DarkBackground else LightBackground,
        border = BorderStroke(1.dp, if (isDarkPreview) DarkBorder else LightBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Box(
                modifier = Modifier
                  .width(72.dp)
                  .height(8.dp)
                  .clip(RoundedCornerShape(4.dp))
                  .background(if (isDarkPreview) DarkTextPrimary else LightTextPrimary)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Box(
                modifier = Modifier
                  .width(44.dp)
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp))
                  .background(descCol.copy(alpha = 0.6f))
              )
            }
          }

          Box(
            modifier = Modifier
              .width(60.dp)
              .height(24.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(accentColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "PREVIEW",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              ),
              color = accentColor
            )
          }
        }
      }
    }
  }
}
