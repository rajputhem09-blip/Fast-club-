package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SpinReward
import com.example.ui.theme.*
import com.example.viewmodel.KhelvViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class SpinHistoryItem(
  val time: String,
  val prize: String,
  val value: String
)

@Composable
fun WheelSpinScreen(
  viewModel: KhelvViewModel,
  onBack: () -> Unit
) {
  BackHandler(onBack = onBack)

  val user by viewModel.user.collectAsState()
  val prizes = viewModel.wheelPrizes
  val coroutineScope = rememberCoroutineScope()
  val rotation = remember { Animatable(0f) }
  var isSpinning by remember { mutableStateOf(false) }

  var spinHistory by remember {
    mutableStateOf(
      listOf(
        SpinHistoryItem("12:10:45", "Cash Bonus", "₹50.00"),
        SpinHistoryItem("11:42:19", "Lucky Coins", "₹10.00")
      )
    )
  }

  fun spinWheel() {
    if (isSpinning) return
    if (user.availableSpins <= 0) {
      viewModel.showToast("No spins remaining! Complete daily missions or deposits to earn spins.")
      return
    }

    isSpinning = true
    val targetPrizeIndex = Random.nextInt(prizes.size)
    val sectorAngle = 360f / prizes.size
    // Calculate final rotation
    val extraSpins = 360f * 5f
    val targetAngle = extraSpins + (360f - (targetPrizeIndex * sectorAngle) - (sectorAngle / 2f))

    coroutineScope.launch {
      rotation.animateTo(
        targetValue = rotation.value + targetAngle,
        animationSpec = tween(
          durationMillis = 4000,
          easing = FastOutSlowInEasing
        )
      )

      val prize = prizes[targetPrizeIndex]
      viewModel.onSpinWheel(prize)
      spinHistory = listOf(
        SpinHistoryItem("Just now", if (prize.isGrandPrize) "Gadget" else "Cash Bonus", prize.label)
      ) + spinHistory
      isSpinning = false
    }
  }

  Scaffold(
    containerColor = KhelvNavyDark,
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.testTag("wheel_back")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
          )
        }

        Text(
          text = "Wheel Spin",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )

        Surface(
          shape = RoundedCornerShape(16.dp),
          color = KhelvNavyCard
        ) {
          Text(
            text = "Spins: ${user.availableSpins}",
            color = KhelvGold,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header Card
      item {
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = KhelvNavySurface,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Today Available Spins",
                color = KhelvTextSecondary,
                fontSize = 13.sp
              )
              Text(
                text = "${user.availableSpins} / 10",
                color = KhelvRed,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp
              )
            }

            Button(
              onClick = {
                viewModel.repository.deposit(100.0, "Spin Recharge")
                viewModel.showToast("Recharged! +2 Free Spins added.")
              },
              colors = ButtonDefaults.buttonColors(containerColor = KhelvBluePrimary),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("Get Spins", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // Wheel Canvas Item with Pointer and Center Button
      item {
        Box(
          modifier = Modifier
            .size(310.dp)
            .testTag("wheel_container"),
          contentAlignment = Alignment.Center
        ) {
          // The Rotating Wheel
          Canvas(
            modifier = Modifier
              .fillMaxSize()
              .rotate(rotation.value)
          ) {
            val canvasSize = size.minDimension
            val radius = canvasSize / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val sweep = 360f / prizes.size

            prizes.forEachIndexed { i, prize ->
              drawArc(
                color = prize.color,
                startAngle = i * sweep,
                sweepAngle = sweep,
                useCenter = true,
                size = Size(canvasSize, canvasSize),
                topLeft = Offset(center.x - radius, center.y - radius)
              )
            }

            // Outer gold ring
            drawCircle(
              color = Color(0xFFFFB800),
              radius = radius,
              style = Stroke(width = 8.dp.toPx())
            )
          }

          // Needle Pointer at top
          Box(
            modifier = Modifier
              .align(Alignment.TopCenter)
              .offset(y = (-8).dp)
          ) {
            Text(
              text = "🔻",
              fontSize = 28.sp
            )
          }

          // Center GO Button
          Box(
            modifier = Modifier
              .size(68.dp)
              .shadow(8.dp, CircleShape)
              .clip(CircleShape)
              .background(Brush.radialGradient(listOf(Color(0xFFFF5252), Color(0xFFD50000))))
              .clickable(enabled = !isSpinning) { spinWheel() }
              .testTag("spin_go_btn"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (isSpinning) "..." else "GO",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 22.sp
            )
          }
        }
      }

      // Prize list overview
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = KhelvNavySurface,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "PRIZES ON THE WHEEL",
              color = KhelvGold,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              Text("📱 iPhone 18", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              Text("⌚ Apple Watch", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              Text("💰 ₹1,000", color = KhelvGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Text("💵 ₹100", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }

      // History Table
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = KhelvNavySurface,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "SPIN HISTORY",
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            spinHistory.forEach { hist ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(hist.time, color = KhelvTextMuted, fontSize = 12.sp)
                Text(hist.prize, color = KhelvTextSecondary, fontSize = 12.sp)
                Text(hist.value, color = KhelvGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}
