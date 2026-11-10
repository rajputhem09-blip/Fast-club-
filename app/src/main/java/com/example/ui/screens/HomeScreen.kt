package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameCategory
import com.example.model.GameItem
import com.example.ui.theme.*
import com.example.viewmodel.ActiveGameScreen
import com.example.viewmodel.KhelvViewModel
import java.util.Locale

@Composable
fun HomeScreen(
  viewModel: KhelvViewModel,
  onNavigateToTab: (com.example.model.NavTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val selectedCat by viewModel.selectedCategory.collectAsState()
  val games by viewModel.gamesFlow.collectAsState()
  val websiteSettings by viewModel.websiteSettings.collectAsState()
  val liveWinners = viewModel.liveWinners
  val leaderboard = viewModel.leaderboard

  val filteredGames = remember(selectedCat, games) {
    val enabledOnly = games.filter { it.isEnabled }
    if (selectedCat == GameCategory.POPULAR) {
      enabledOnly.filter { it.badge in listOf("HOT", "TOP", "JACKPOT", "FAVORITE") }.ifEmpty { enabledOnly }
    } else {
      enabledOnly.filter { it.category == selectedCat }
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Hero Promo Banner
    item {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("home_hero_banner")
          .clickable { viewModel.showDepositDialog.value = true }
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF1E3A8A), Color(0xFF0F172A), Color(0xFFB45309))
              )
            )
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = KhelvRed
              ) {
                Text(
                  text = websiteSettings.heroBannerTitle,
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "UP TO ₹10,000",
                color = KhelvGoldLight,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp
              )
              Text(
                text = websiteSettings.heroBannerSubtitle,
                color = KhelvTextSecondary,
                fontSize = 12.sp
              )
            }

            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(GradientGold)),
              contentAlignment = Alignment.Center
            ) {
              Text("🎁", fontSize = 32.sp)
            }
          }
        }
      }
    }

    // 2. Audio Notice Ticker
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = KhelvNavySurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = "Notice",
            tint = KhelvGold,
            modifier = Modifier.size(18.dp)
          )

          Text(
            text = websiteSettings.announcementText,
            color = KhelvTextSecondary,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = KhelvBluePrimary,
            modifier = Modifier.clickable { viewModel.showCustomerService.value = true }
          ) {
            Text(
              text = "Detail",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }
    }

    // 2.5 Quick Rules & Winning Tricks Guide Banner
    item {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent,
        modifier = Modifier
          .fillMaxWidth()
          .clickable { viewModel.showGameRulesDialog.value = true }
          .testTag("game_rules_guide_banner")
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF4338CA), Color(0xFF6D28D9), Color(0xFFBE185D))
              )
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Text("🎯", fontSize = 24.sp)
              Column {
                Text(
                  text = "Game Rules, Tricks & Formulas",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
                Text(
                  text = "WinGo 3X Rule • Aviator 1.5x Auto-Cash • Mines 3-Corners",
                  color = Color(0xFFFDE047),
                  fontSize = 11.sp
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(20.dp),
              color = Color.White.copy(alpha = 0.2f)
            ) {
              Text(
                text = "View",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }

    // 3. Category Bar (Horizontal Scroll)
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        GameCategory.values().forEach { cat ->
          val isSelected = cat == selectedCat
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isSelected) KhelvBluePrimary else KhelvNavySurface,
            modifier = Modifier
              .clickable { viewModel.setCategory(cat) }
              .testTag("cat_${cat.name.lowercase()}")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              val emoji = when (cat) {
                GameCategory.POPULAR -> "🔥"
                GameCategory.SLOTS -> "🎰"
                GameCategory.FISHING -> "🦈"
                GameCategory.CRASH -> "✈️"
                GameCategory.CRICKET -> "🏏"
                GameCategory.LOTTERY -> "🎱"
                GameCategory.MINI_GAMES -> "💎"
                GameCategory.CARDS -> "🃏"
                GameCategory.CASINO_SPORTS -> "🎲"
              }
              Text(emoji, fontSize = 14.sp)
              Text(
                text = cat.title,
                color = if (isSelected) Color.White else KhelvTextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp
              )
            }
          }
        }
      }
    }

    // 4. Featured Games Grid Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${selectedCat.title} Games",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { viewModel.setCategory(GameCategory.POPULAR) }
        ) {
          Text("All", color = KhelvBlueLight, fontSize = 13.sp)
          Icon(Icons.Default.ChevronRight, contentDescription = "View all", tint = KhelvBlueLight, modifier = Modifier.size(16.dp))
        }
      }
    }

    // 5. Game Grid Items
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        filteredGames.chunked(2).forEach { rowItems ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            rowItems.forEach { game ->
              GameCard(
                game = game,
                modifier = Modifier.weight(1f),
                onClick = {
                  when {
                    game.id == "aviator" || game.id == "crash" || game.id == "limbo" || game.id == "rocket" || game.id == "plinko" -> {
                      viewModel.openGame(ActiveGameScreen.AVIATOR)
                    }
                    game.id == "mines" -> {
                      viewModel.openGame(ActiveGameScreen.MINES)
                    }
                    game.id.startsWith("wingo") || game.id == "colour_prediction" || game.id.startsWith("k3") || game.id.contains("lotre") || game.id == "trx_wingo" -> {
                      viewModel.openGame(ActiveGameScreen.WINGO)
                    }
                    else -> {
                      viewModel.showToast("Opening ${game.title} live table...")
                      viewModel.openGame(ActiveGameScreen.AVIATOR)
                    }
                  }
                }
              )
            }
            if (rowItems.size == 1) {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
        }
      }
    }

    // 6. Winning Information Live Stream
    item {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = KhelvNavySurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "⚡ WINNING INFORMATION",
            color = KhelvGold,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(10.dp))

          liveWinners.take(4).forEach { winner ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF25306B)),
                  contentAlignment = Alignment.Center
                ) {
                  Text("👤", fontSize = 14.sp)
                }

                Column {
                  Text(winner.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  Text(winner.gameName, color = KhelvTextMuted, fontSize = 11.sp)
                }
              }

              Text(
                text = "+₹${String.format(Locale.US, "%.2f", winner.amount)}",
                color = KhelvGreen,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
              )
            }
          }
        }
      }
    }

    // 7. Today's Earnings Leaderboard
    item {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = KhelvNavySurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "🏆 TODAY'S EARNINGS CHART",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.height(10.dp))

          leaderboard.forEach { entry ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Surface(
                  shape = CircleShape,
                  color = when (entry.rank) {
                    1 -> Color(0xFFFFD700)
                    2 -> Color(0xFFC0C0C0)
                    3 -> Color(0xFFCD7F32)
                    else -> Color(0xFF22284E)
                  },
                  modifier = Modifier.size(24.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = "${entry.rank}",
                      color = if (entry.rank <= 3) Color.Black else Color.White,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  }
                }

                Text(entry.username, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
              }

              Text(
                text = "₹${String.format(Locale.US, "%,.2f", entry.amount)}",
                color = KhelvGold,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }
        }
      }
    }

    // Extra bottom padding for floating bar
    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}

@Composable
private fun GameCard(
  game: GameItem,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = KhelvNavyCard,
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .testTag("game_card_${game.id}")
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)
        .background(
          Brush.verticalGradient(
            listOf(game.accentColor.copy(alpha = 0.25f), KhelvNavyCard)
          )
        )
        .padding(12.dp)
    ) {
      // Top badge
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = game.accentColor,
        modifier = Modifier.align(Alignment.TopEnd)
      ) {
        Text(
          text = game.badge,
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 10.sp,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }

      // Center Icon
      Box(
        modifier = Modifier.align(Alignment.Center)
      ) {
        Text(
          text = game.iconEmoji,
          fontSize = 38.sp
        )
      }

      // Bottom title & provider
      Column(modifier = Modifier.align(Alignment.BottomStart)) {
        Text(
          text = game.title,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          maxLines = 1
        )
        Text(
          text = game.provider,
          color = KhelvTextMuted,
          fontSize = 10.sp
        )
      }
    }
  }
}
