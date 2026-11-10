package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.KhelvViewModel
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.random.Random

@Composable
fun AviatorGameScreen(
  viewModel: KhelvViewModel,
  onBack: () -> Unit
) {
  BackHandler(onBack = onBack)

  val user by viewModel.user.collectAsState()
  var betAmount by remember { mutableDoubleStateOf(50.0) }
  var isBetPlaced by remember { mutableStateOf(false) }
  var isCashedOut by remember { mutableStateOf(false) }
  var cashoutAmount by remember { mutableDoubleStateOf(0.0) }
  var multiplier by remember { mutableFloatStateOf(1.00f) }
  var isFlying by remember { mutableStateOf(false) }
  var isCrashed by remember { mutableStateOf(false) }
  var crashPoint by remember { mutableFloatStateOf(2.50f) }

  var pastMultipliers by remember {
    mutableStateOf(listOf(1.45f, 2.80f, 1.15f, 5.22f, 1.84f, 12.30f, 2.10f, 1.05f))
  }

  // Game flight loop
  LaunchedEffect(isFlying) {
    if (isFlying) {
      isCrashed = false
      multiplier = 1.00f
      // Generate random crash multiplier between 1.10 and 15.00
      crashPoint = if (Random.nextFloat() < 0.25f) {
        1.10f + Random.nextFloat() * 0.40f
      } else if (Random.nextFloat() < 0.60f) {
        1.50f + Random.nextFloat() * 2.50f
      } else {
        4.00f + Random.nextFloat() * 8.00f
      }

      while (multiplier < crashPoint) {
        delay(60)
        val step = if (multiplier < 2.0f) 0.02f else if (multiplier < 5.0f) 0.05f else 0.12f
        multiplier += step
      }

      // Crashed!
      isFlying = false
      isCrashed = true
      pastMultipliers = (listOf(multiplier) + pastMultipliers).take(10)

      if (isBetPlaced && !isCashedOut) {
        viewModel.showToast("Plane flew away at ${String.format(Locale.US, "%.2f", multiplier)}x!")
      }
      isBetPlaced = false
      isCashedOut = false
    }
  }

  Scaffold(
    containerColor = Color(0xFF0F111E),
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
          modifier = Modifier.testTag("aviator_back")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "✈️ AVIATOR",
            color = KhelvRed,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
          )
        }

        // Current balance pill
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color(0xFF1E2238)
        ) {
          Text(
            text = "₹${String.format(Locale.US, "%.2f", user.balance)}",
            color = KhelvGold,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Past multipliers pill row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        pastMultipliers.forEach { mult ->
          val color = if (mult >= 5.0f) Color(0xFFE040FB) else if (mult >= 2.0f) Color(0xFF3B82F6) else Color(0xFF9E9E9E)
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF181B2C)
          ) {
            Text(
              text = "${String.format(Locale.US, "%.2f", mult)}x",
              color = color,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      // Flight Canvas Arena
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(260.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF090A13))
          .testTag("aviator_canvas_box"),
        contentAlignment = Alignment.Center
      ) {
        // Flight curved trajectory graph
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height

          // Grid lines
          drawLine(Color(0xFF1E2238), Offset(0f, h * 0.25f), Offset(w, h * 0.25f), strokeWidth = 1f)
          drawLine(Color(0xFF1E2238), Offset(0f, h * 0.5f), Offset(w, h * 0.5f), strokeWidth = 1f)
          drawLine(Color(0xFF1E2238), Offset(0f, h * 0.75f), Offset(w, h * 0.75f), strokeWidth = 1f)

          if (isFlying) {
            val progress = ((multiplier - 1.0f) / 6.0f).coerceIn(0.1f, 0.9f)
            val path = Path().apply {
              moveTo(20f, h - 20f)
              quadraticTo(
                w * 0.3f,
                h - 20f,
                20f + (w - 60f) * progress,
                h - 20f - (h - 60f) * progress
              )
            }
            drawPath(
              path = path,
              color = Color(0xFFEF4444),
              style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
          }
        }

        // Center multiplier text / crash state
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          if (isFlying) {
            Text(
              text = "✈️",
              fontSize = 38.sp
            )
            Text(
              text = "${String.format(Locale.US, "%.2f", multiplier)}x",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 44.sp
            )
          } else if (isCrashed) {
            Text(
              text = "FLEW AWAY!",
              color = Color(0xFFEF4444),
              fontWeight = FontWeight.Black,
              fontSize = 24.sp
            )
            Text(
              text = "${String.format(Locale.US, "%.2f", multiplier)}x",
              color = Color(0xFFEF4444),
              fontWeight = FontWeight.Bold,
              fontSize = 36.sp
            )
          } else {
            Text(
              text = "READY FOR TAKEOFF",
              color = KhelvTextSecondary,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Place your bet and press START",
              color = KhelvTextMuted,
              fontSize = 13.sp
            )
          }

          if (isCashedOut) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFF10B981)
            ) {
              Text(
                text = "WON ₹${String.format(Locale.US, "%.2f", cashoutAmount)}!",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
              )
            }
          }
        }
      }

      // Bet controls Card
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF16192C),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = "BET AMOUNT",
            color = KhelvTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )

          // Preset amount buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(20.0, 50.0, 100.0, 500.0).forEach { amt ->
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (betAmount == amt) KhelvBluePrimary else Color(0xFF222642),
                modifier = Modifier
                  .weight(1f)
                  .clickable(enabled = !isFlying) { betAmount = amt }
              ) {
                Text(
                  text = "₹${amt.toInt()}",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  modifier = Modifier
                    .padding(vertical = 8.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                )
              }
            }
          }

          // Main Action Button (BET / START / CASH OUT)
          if (!isFlying) {
            Button(
              onClick = {
                if (user.balance < betAmount) {
                  viewModel.showToast("Insufficient balance! Please deposit to play.")
                } else {
                  val ok = viewModel.repository.placeBet(betAmount, "Aviator")
                  if (ok) {
                    isBetPlaced = true
                    isCashedOut = false
                    isFlying = true
                  }
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("aviator_action_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
              shape = RoundedCornerShape(16.dp)
            ) {
              Text(
                text = "BET ₹${betAmount.toInt()} & START",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = Color.White
              )
            }
          } else {
            // Flying: can Cash Out!
            if (isBetPlaced && !isCashedOut) {
              val currentWin = betAmount * multiplier
              Button(
                onClick = {
                  isCashedOut = true
                  cashoutAmount = currentWin
                  viewModel.repository.creditWin(cashoutAmount, "Aviator")
                  viewModel.showToast("Cashed out ₹${String.format(Locale.US, "%.2f", cashoutAmount)} at ${String.format(Locale.US, "%.2f", multiplier)}x!")
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(54.dp)
                  .testTag("aviator_cashout_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB800)),
                shape = RoundedCornerShape(16.dp)
              ) {
                Text(
                  text = "CASH OUT ₹${String.format(Locale.US, "%.2f", currentWin)} (${String.format(Locale.US, "%.2f", multiplier)}x)",
                  fontWeight = FontWeight.Black,
                  fontSize = 16.sp,
                  color = Color.Black
                )
              }
            } else {
              Surface(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(54.dp),
                color = Color(0xFF222642),
                shape = RoundedCornerShape(16.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = if (isCashedOut) "CASHED OUT!" else "ROUND IN PROGRESS...",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
