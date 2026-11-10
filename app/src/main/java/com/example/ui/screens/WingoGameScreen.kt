package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.KhelvViewModel
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.random.Random

data class WingoRoundResult(
  val period: String,
  val number: Int,
  val bigSmall: String,
  val color: Color
)

@Composable
fun WingoGameScreen(
  viewModel: KhelvViewModel,
  onBack: () -> Unit
) {
  BackHandler(onBack = onBack)

  val user by viewModel.user.collectAsState()
  var secondsLeft by remember { mutableIntStateOf(24) }
  var currentPeriod by remember { mutableLongStateOf(202611090124L) }
  var selectedBetType by remember { mutableStateOf<String?>("Green") }
  var selectedNumber by remember { mutableStateOf<Int?>(null) }
  var betAmount by remember { mutableDoubleStateOf(50.0) }

  var pastResults by remember {
    mutableStateOf(
      listOf(
        WingoRoundResult("202611090123", 7, "Big", Color(0xFF10B981)),
        WingoRoundResult("202611090122", 2, "Small", Color(0xFFEF4444)),
        WingoRoundResult("202611090121", 0, "Small", Color(0xFF8B5CF6)),
        WingoRoundResult("202611090120", 9, "Big", Color(0xFF10B981)),
        WingoRoundResult("202611090119", 4, "Small", Color(0xFFEF4444))
      )
    )
  }

  // 30s countdown timer loop
  LaunchedEffect(Unit) {
    while (true) {
      delay(1000)
      if (secondsLeft > 0) {
        secondsLeft -= 1
      } else {
        // Round Finished! Generate result
        val winningNum = Random.nextInt(10)
        val winningColor = when {
          winningNum == 0 -> Color(0xFF8B5CF6) // Violet + Red
          winningNum == 5 -> Color(0xFF8B5CF6) // Violet + Green
          winningNum % 2 == 1 -> Color(0xFF10B981) // Green
          else -> Color(0xFFEF4444) // Red
        }
        val isBig = winningNum >= 5

        // Check if user won
        if (selectedBetType != null) {
          var won = false
          var multiplier = 2.0
          if (selectedBetType == "Green" && (winningNum % 2 == 1 || winningNum == 5)) won = true
          if (selectedBetType == "Red" && (winningNum % 2 == 0 || winningNum == 0)) won = true
          if (selectedBetType == "Violet" && (winningNum == 0 || winningNum == 5)) {
            won = true
            multiplier = 4.5
          }
          if (selectedBetType == "Big" && isBig) won = true
          if (selectedBetType == "Small" && !isBig) won = true
          if (selectedNumber == winningNum) {
            won = true
            multiplier = 9.0
          }

          if (won) {
            val winAmount = betAmount * multiplier
            viewModel.repository.creditWin(winAmount, "WinGo 30s")
            viewModel.showToast("Congratulations! Won ₹${String.format(Locale.US, "%.2f", winAmount)} on Period $currentPeriod!")
          } else {
            viewModel.showToast("Period $currentPeriod finished with Number $winningNum.")
          }
        }

        // Add to history
        pastResults = listOf(
          WingoRoundResult(currentPeriod.toString(), winningNum, if (isBig) "Big" else "Small", winningColor)
        ) + pastResults.take(10)

        currentPeriod += 1
        secondsLeft = 30
      }
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
          modifier = Modifier.testTag("wingo_back")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
          )
        }

        Text(
          text = "🎱 WinGo 30sec",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )

        Surface(
          shape = RoundedCornerShape(16.dp),
          color = KhelvNavyCard
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
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Countdown & Period Header Card
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
                text = "Period: $currentPeriod",
                color = KhelvTextSecondary,
                fontSize = 13.sp
              )
              Text(
                text = "Count Down",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }

            // Timer display
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEF4444)
              ) {
                Text(
                  text = "00 : ${if (secondsLeft < 10) "0$secondsLeft" else "$secondsLeft"}",
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 20.sp,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      }

      // Color Betting Options: Green, Violet, Red
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Green
          ColorBetButton(
            title = "Green",
            odds = "2x",
            bgColor = Color(0xFF10B981),
            isSelected = selectedBetType == "Green",
            modifier = Modifier.weight(1f),
            testTag = "bet_green",
            onClick = {
              selectedBetType = "Green"
              selectedNumber = null
            }
          )

          // Violet
          ColorBetButton(
            title = "Violet",
            odds = "4.5x",
            bgColor = Color(0xFF8B5CF6),
            isSelected = selectedBetType == "Violet",
            modifier = Modifier.weight(1f),
            testTag = "bet_violet",
            onClick = {
              selectedBetType = "Violet"
              selectedNumber = null
            }
          )

          // Red
          ColorBetButton(
            title = "Red",
            odds = "2x",
            bgColor = Color(0xFFEF4444),
            isSelected = selectedBetType == "Red",
            modifier = Modifier.weight(1f),
            testTag = "bet_red",
            onClick = {
              selectedBetType = "Red"
              selectedNumber = null
            }
          )
        }
      }

      // Numbers 0-9 Grid (9x)
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = KhelvNavySurface,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "SELECT NUMBER (9x Return)",
              color = KhelvGold,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              (0..4).forEach { num ->
                NumberBall(
                  number = num,
                  isSelected = selectedNumber == num,
                  onClick = {
                    selectedNumber = num
                    selectedBetType = "Number $num"
                  }
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              (5..9).forEach { num ->
                NumberBall(
                  number = num,
                  isSelected = selectedNumber == num,
                  onClick = {
                    selectedNumber = num
                    selectedBetType = "Number $num"
                  }
                )
              }
            }
          }
        }
      }

      // Big / Small Buttons
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (selectedBetType == "Big") KhelvBluePrimary else KhelvNavyCard,
            modifier = Modifier
              .weight(1f)
              .clickable {
                selectedBetType = "Big"
                selectedNumber = null
              }
          ) {
            Column(
              modifier = Modifier.padding(vertical = 10.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text("Big (5-9)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("2x payout", color = KhelvTextSecondary, fontSize = 11.sp)
            }
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (selectedBetType == "Small") KhelvBluePrimary else KhelvNavyCard,
            modifier = Modifier
              .weight(1f)
              .clickable {
                selectedBetType = "Small"
                selectedNumber = null
              }
          ) {
            Column(
              modifier = Modifier.padding(vertical = 10.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text("Small (0-4)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("2x payout", color = KhelvTextSecondary, fontSize = 11.sp)
            }
          }
        }
      }

      // Bet Amount and Place Bet
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = KhelvNavySurface,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              listOf(20.0, 50.0, 100.0, 500.0).forEach { amt ->
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (betAmount == amt) KhelvGold else KhelvNavyCard,
                  modifier = Modifier
                    .weight(1f)
                    .clickable { betAmount = amt }
                ) {
                  Text(
                    text = "₹${amt.toInt()}",
                    color = if (betAmount == amt) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier
                      .padding(vertical = 8.dp)
                      .wrapContentWidth(Alignment.CenterHorizontally)
                  )
                }
              }
            }

            Button(
              onClick = {
                if (user.balance < betAmount) {
                  viewModel.showToast("Insufficient balance! Please deposit first.")
                } else {
                  val ok = viewModel.repository.placeBet(betAmount, "WinGo 30s")
                  if (ok) {
                    viewModel.showToast("Bet of ₹${betAmount.toInt()} placed on $selectedBetType!")
                  }
                }
              },
              enabled = secondsLeft > 5,
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("wingo_bet_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text(
                text = if (secondsLeft > 5) "PLACE BET ₹${betAmount.toInt()} ON $selectedBetType" else "WAITING FOR RESULT...",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
              )
            }
          }
        }
      }

      // Past Game Records
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = KhelvNavySurface,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "RECENT RESULTS",
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            pastResults.forEach { r ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(r.period, color = KhelvTextMuted, fontSize = 12.sp)
                Box(
                  modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(r.color),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "${r.number}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                }
                Text(
                  text = r.bigSmall,
                  color = if (r.bigSmall == "Big") KhelvGold else KhelvBlueLight,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ColorBetButton(
  title: String,
  odds: String,
  bgColor: Color,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
  testTag: String,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = bgColor,
    modifier = modifier
      .clickable(onClick = onClick)
      .testTag(testTag)
  ) {
    Column(
      modifier = Modifier.padding(vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = title,
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 14.sp
      )
      Text(
        text = odds,
        color = Color.White.copy(alpha = 0.85f),
        fontSize = 11.sp
      )
    }
  }
}

@Composable
private fun NumberBall(
  number: Int,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val ballColor = when {
    number == 0 || number == 5 -> Color(0xFF8B5CF6)
    number % 2 == 1 -> Color(0xFF10B981)
    else -> Color(0xFFEF4444)
  }

  Box(
    modifier = Modifier
      .size(44.dp)
      .clip(CircleShape)
      .background(if (isSelected) KhelvGold else ballColor)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "$number",
      color = if (isSelected) Color.Black else Color.White,
      fontWeight = FontWeight.Black,
      fontSize = 16.sp
    )
  }
}
