package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 20.dp,
  borderWidth: Dp = 1.dp,
  backgroundColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
  borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
  elevation: Dp = 4.dp,
  content: @Composable () -> Unit
) {
  val shape = RoundedCornerShape(cornerRadius)

  Surface(
    modifier = modifier
      .shadow(elevation, shape, clip = false)
      .clip(shape),
    shape = shape,
    color = backgroundColor,
    border = BorderStroke(borderWidth, borderColor)
  ) {
    content()
  }
}

@Composable
fun GradientGlassCard(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 20.dp,
  borderWidth: Dp = 1.dp,
  gradientColors: List<Color>,
  borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
  elevation: Dp = 6.dp,
  content: @Composable () -> Unit
) {
  val shape = RoundedCornerShape(cornerRadius)

  Box(
    modifier = modifier
      .shadow(elevation, shape, clip = false)
      .clip(shape)
      .background(Brush.linearGradient(gradientColors))
  ) {
    Surface(
      modifier = Modifier.matchParentSize(),
      shape = shape,
      color = Color.Transparent,
      border = BorderStroke(borderWidth, borderColor)
    ) {}
    Box(modifier = Modifier.padding(1.dp)) {
      content()
    }
  }
}
