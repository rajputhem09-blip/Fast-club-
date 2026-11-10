package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun FloatingActionWidgets(
  onWheelSpinClick: () -> Unit,
  onGiftsClick: () -> Unit,
  onCustomerServiceClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.padding(end = 12.dp, bottom = 80.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
    horizontalAlignment = Alignment.End
  ) {
    // 1. Wheel Spin Bubble
    FloatingBubble(
      iconText = "🎡",
      gradientColors = listOf(Color(0xFFFFB300), Color(0xFFFF3D00)),
      testTag = "floating_wheel",
      onClick = onWheelSpinClick
    )

    // 2. Gifts Box Bubble
    FloatingBubble(
      iconText = "🎁",
      gradientColors = listOf(Color(0xFFFF4081), Color(0xFFE040FB)),
      testTag = "floating_gifts",
      onClick = onGiftsClick
    )

    // 3. Customer Service Robot Bubble
    FloatingBubble(
      iconText = "🤖",
      gradientColors = listOf(Color(0xFF00E5FF), Color(0xFF2979FF)),
      testTag = "floating_support",
      onClick = onCustomerServiceClick
    )
  }
}

@Composable
private fun FloatingBubble(
  iconText: String,
  gradientColors: List<Color>,
  testTag: String,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .size(46.dp)
      .shadow(8.dp, CircleShape)
      .clip(CircleShape)
      .background(Brush.linearGradient(gradientColors))
      .clickable(onClick = onClick)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = iconText,
      fontSize = 22.sp
    )
  }
}
