package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import java.util.Locale
import kotlin.random.Random

data class TileState(
  val index: Int,
  val isMine: Boolean,
  val isRevealed: Boolean = false
)

@Composable
fun MinesGameScreen(
  viewModel: KhelvViewModel,
  onBack: () -> Unit
) {
  BackHandler(onBack = onBack)

  val user by viewModel.user.collectAsState()
  var betAmount by remember { mutableDoubleStateOf(50.0) }
  var mineCount by remember { mutableIntStateOf(3) }
  var isGameActive by remember { mutableStateOf(false) }
  var isGameOver by remember { mutableStateOf(false) }
  var tiles by remember { mutableStateOf(List(25) { TileState(it, false) }) }
  var gemsFound by remember { mutableIntStateOf(0) }

  val multiplier: Double = when (gemsFound) {
    0 -> 1.0
    1 -> 1.15
    2 -> 1.35
    3 -> 1.62
    4 -> 1.98
    5 -> 2.45
    6 -> 3.10
    7 -> 4.00
    8 -> 5.25
    else -> 5.25 + (gemsFound - 8) * 1.5
  }

  fun startNewRound() {
    if (user.balance < betAmount) {
      viewModel.showToast("Insufficient balance to place bet!")
      return
    }
    val ok = viewModel.repository.placeBet(betAmount, "Mines 5x5")
    if (!ok) return

    // Pick random mine locations
    val mineIndices = (0 until 25).shuffled().take(mineCount).toSet()
    tiles = List(25) { idx ->
      TileState(index = idx, isMine = mineIndices.contains(idx), isRevealed = false)
    }
    gemsFound = 0
    isGameActive = true
    isGameOver = false
  }

  fun onTileClick(idx: Int) {
    if (!isGameActive || isGameOver) return
    val current = tiles[idx]
    if (current.isRevealed) return

    if (current.isMine) {
      // Hit a mine! Reveal all
      tiles = tiles.map { it.copy(isRevealed = true) }
      isGameActive = false
      isGameOver = true
      viewModel.showToast("Boom! You hit a mine.")
    } else {
      // Found a gem!
      tiles = tiles.map { if (it.index == idx) it.copy(isRevealed = true) else it }
      gemsFound += 1
    }
  }

  fun cashOut() {
    if (!isGameActive || gemsFound == 0) return
    val winAmount = betAmount * multiplier
    viewModel.repository.creditWin(winAmount, "Mines 5x5")
    viewModel.showToast("Cashed out ₹${String.format(Locale.US, "%.2f", winAmount)} (${String.format(Locale.US, "%.2f", multiplier)}x)!")
    tiles = tiles.map { it.copy(isRevealed = true) }
    isGameActive = false
    isGameOver = true
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
          modifier = Modifier.testTag("mines_back")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
          )
        }

        Text(
          text = "💣 MINES 5x5",
          color = KhelvGreen,
          fontWeight = FontWeight.Black,
          fontSize = 18.sp
        )

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
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Status & Multiplier Banner
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF16192C),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "MINES: $mineCount / 25",
              color = KhelvTextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "GEMS FOUND: $gemsFound",
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "NEXT MULTIPLIER",
              color = KhelvTextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${String.format(Locale.US, "%.2f", multiplier)}x",
              color = if (gemsFound > 0) KhelvGold else KhelvTextMuted,
              fontSize = 20.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }

      // 5x5 Grid of Tiles
      LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1f),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(25) { idx ->
          val tile = tiles[idx]
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = when {
              !tile.isRevealed -> Color(0xFF222642)
              tile.isMine -> Color(0xFFDC2626)
              else -> Color(0xFF059669)
            },
            modifier = Modifier
              .aspectRatio(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable(enabled = isGameActive && !tile.isRevealed) {
                onTileClick(idx)
              }
              .testTag("mine_tile_$idx")
          ) {
            Box(contentAlignment = Alignment.Center) {
              if (tile.isRevealed) {
                Text(
                  text = if (tile.isMine) "💣" else "💎",
                  fontSize = 24.sp
                )
              } else {
                Text(
                  text = "⭐",
                  color = Color(0xFF3B4268),
                  fontSize = 16.sp
                )
              }
            }
          }
        }
      }

      // Bottom Control Area
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF16192C),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          if (!isGameActive) {
            // Bet selection
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              listOf(20.0, 50.0, 100.0, 500.0).forEach { amt ->
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (betAmount == amt) KhelvBluePrimary else Color(0xFF222642),
                  modifier = Modifier
                    .weight(1f)
                    .clickable { betAmount = amt }
                ) {
                  Text(
                    text = "₹${amt.toInt()}",
                    color = Color.White,
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
              onClick = { startNewRound() },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("start_mines_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text(
                text = "START GAME (₹${betAmount.toInt()})",
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = Color.White
              )
            }
          } else {
            // Active: Cash out option
            val currentWin = betAmount * multiplier
            Button(
              onClick = { cashOut() },
              enabled = gemsFound > 0,
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("mines_cashout_btn"),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (gemsFound > 0) Color(0xFFFFB800) else Color(0xFF4B5563)
              ),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text(
                text = if (gemsFound > 0)
                  "CASH OUT ₹${String.format(Locale.US, "%.2f", currentWin)} (${String.format(Locale.US, "%.2f", multiplier)}x)"
                else "PICK A GEM TO UNLOCK CASHOUT",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = if (gemsFound > 0) Color.Black else Color.White
              )
            }
          }
        }
      }
    }
  }
}
