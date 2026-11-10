package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TransactionStatus
import com.example.model.TransactionType
import com.example.model.WalletTransaction
import com.example.ui.theme.*
import com.example.viewmodel.KhelvViewModel
import java.util.Locale

enum class WalletSection {
  OVERVIEW, DEPOSIT, WITHDRAW, HISTORY
}

@Composable
fun WalletScreen(
  viewModel: KhelvViewModel,
  modifier: Modifier = Modifier
) {
  val user by viewModel.user.collectAsState()
  val transactions by viewModel.transactions.collectAsState()
  var currentSection by remember { mutableStateOf(WalletSection.OVERVIEW) }

  // Deposit state
  var selectedChannel by remember { mutableStateOf("Phonepe_QR") }
  var depositAmount by remember { mutableDoubleStateOf(500.0) }

  // Withdraw state
  var withdrawMethod by remember { mutableStateOf("UPI") }
  var withdrawAmountInput by remember { mutableStateOf("500") }
  var upiAddressInput by remember { mutableStateOf("user@okhdfcbank") }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Balance Gradient Card
    item {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFFE11D48), Color(0xFF2563EB))
              )
            )
            .padding(18.dp)
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text("👛", fontSize = 20.sp)
                Text(
                  text = "Available Balance",
                  color = Color.White.copy(alpha = 0.9f),
                  fontSize = 13.sp
                )
              }

              IconButton(
                onClick = { viewModel.showToast("Balance refreshed: ₹${String.format(Locale.US, "%.2f", user.balance)}") },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White, modifier = Modifier.size(18.dp))
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "₹${String.format(Locale.US, "%.2f", user.balance)}",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("Total Deposit", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                Text("₹${user.totalDeposit.toInt()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
              Column(horizontalAlignment = Alignment.End) {
                Text("Total Withdrawal", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                Text("₹${user.totalWithdraw.toInt()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
            }
          }
        }
      }
    }

    // 2. Section Selector Tabs: Deposit, Withdraw, History
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          WalletSection.OVERVIEW to "Overview",
          WalletSection.DEPOSIT to "Deposit",
          WalletSection.WITHDRAW to "Withdraw",
          WalletSection.HISTORY to "History"
        ).forEach { (section, title) ->
          val isSelected = currentSection == section
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) KhelvBluePrimary else KhelvNavySurface,
            modifier = Modifier
              .weight(1f)
              .clickable { currentSection = section }
              .testTag("wallet_tab_${title.lowercase()}")
          ) {
            Box(
              modifier = Modifier.padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = title,
                color = if (isSelected) Color.White else KhelvTextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }

    // 3. Section Content
    when (currentSection) {
      WalletSection.OVERVIEW -> {
        // Quick Actions 4-Grid
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
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              WalletActionPill(
                emoji = "💳",
                label = "Deposit",
                color = KhelvGold,
                onClick = { currentSection = WalletSection.DEPOSIT }
              )
              WalletActionPill(
                emoji = "🏧",
                label = "Withdraw",
                color = KhelvBlueLight,
                onClick = { currentSection = WalletSection.WITHDRAW }
              )
              WalletActionPill(
                emoji = "📜",
                label = "History",
                color = KhelvGreen,
                onClick = { currentSection = WalletSection.HISTORY }
              )
              WalletActionPill(
                emoji = "🎁",
                label = "Voucher",
                color = KhelvOrange,
                onClick = { viewModel.showGiftRedeemDialog.value = true }
              )
            }
          }
        }

        // Wallet Breakdown
        item {
          Surface(
            shape = RoundedCornerShape(18.dp),
            color = KhelvNavySurface,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("WALLET CHANNELS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Spacer(modifier = Modifier.height(12.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text("Main Wallet (100%)", color = KhelvTextSecondary, fontSize = 12.sp)
                  Text("₹${String.format(Locale.US, "%.2f", user.balance)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text("3rd Party Wallet (0%)", color = KhelvTextSecondary, fontSize = 12.sp)
                  Text("₹0.00", color = KhelvTextMuted, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              Button(
                onClick = { viewModel.showToast("All balances synced with Main Wallet!") },
                colors = ButtonDefaults.buttonColors(containerColor = KhelvBluePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("Main Wallet Transfer", fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
            }
          }
        }
      }

      WalletSection.DEPOSIT -> {
        item {
          Surface(
            shape = RoundedCornerShape(18.dp),
            color = KhelvNavySurface,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Text("SELECT RECHARGE CHANNEL", color = KhelvGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)

              // Channel badges
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                listOf(
                  "Phonepe_QR" to "PhonePe QR",
                  "UPI_QR" to "UPI x QR",
                  "Paytm_QR" to "Paytm QR",
                  "USDT" to "USDT Crypto"
                ).forEach { (code, label) ->
                  val isSelected = selectedChannel == code
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) KhelvBluePrimary else KhelvNavyCard,
                    modifier = Modifier
                      .weight(1f)
                      .clickable { selectedChannel = code }
                  ) {
                    Box(
                      modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = label,
                        color = if (isSelected) Color.White else KhelvTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
              }

              Text("QUICK AMOUNT", color = KhelvTextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)

              // Preset amounts grid
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                listOf(100.0, 300.0, 500.0, 1000.0).forEach { amt ->
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (depositAmount == amt) KhelvGold else KhelvNavyCard,
                    modifier = Modifier
                      .weight(1f)
                      .clickable { depositAmount = amt }
                  ) {
                    Box(
                      modifier = Modifier.padding(vertical = 10.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = "₹${amt.toInt()}",
                        color = if (depositAmount == amt) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                      )
                    }
                  }
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                listOf(2000.0, 5000.0, 10000.0, 50000.0).forEach { amt ->
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (depositAmount == amt) KhelvGold else KhelvNavyCard,
                    modifier = Modifier
                      .weight(1f)
                      .clickable { depositAmount = amt }
                  ) {
                    Box(
                      modifier = Modifier.padding(vertical = 10.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = "₹${amt.toInt()}",
                        color = if (depositAmount == amt) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                      )
                    }
                  }
                }
              }

              // Instant Deposit Action Button
              Button(
                onClick = {
                  viewModel.deposit(depositAmount, selectedChannel)
                  currentSection = WalletSection.HISTORY
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(50.dp)
                  .testTag("submit_deposit_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(14.dp)
              ) {
                Text(
                  text = "DEPOSIT ₹${depositAmount.toInt()} VIA $selectedChannel",
                  fontWeight = FontWeight.Black,
                  fontSize = 15.sp,
                  color = Color.White
                )
              }
            }
          }
        }
      }

      WalletSection.WITHDRAW -> {
        item {
          Surface(
            shape = RoundedCornerShape(18.dp),
            color = KhelvNavySurface,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Text("WITHDRAWAL METHOD", color = KhelvGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                listOf("UPI" to "UPI ID", "BANK" to "Bank Card", "USDT" to "USDT TRC20").forEach { (method, label) ->
                  val isSelected = withdrawMethod == method
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) KhelvBluePrimary else KhelvNavyCard,
                    modifier = Modifier
                      .weight(1f)
                      .clickable { withdrawMethod = method }
                  ) {
                    Box(
                      modifier = Modifier.padding(vertical = 12.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = label,
                        color = if (isSelected) Color.White else KhelvTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                      )
                    }
                  }
                }
              }

              // Account info input
              OutlinedTextField(
                value = upiAddressInput,
                onValueChange = { upiAddressInput = it },
                label = { Text(if (withdrawMethod == "UPI") "Enter UPI ID" else if (withdrawMethod == "BANK") "Account No / IFSC" else "TRC20 Wallet Address") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = KhelvBluePrimary,
                  unfocusedBorderColor = KhelvNavyBorder,
                  focusedTextColor = Color.White,
                  unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
              )

              // Amount input
              OutlinedTextField(
                value = withdrawAmountInput,
                onValueChange = { withdrawAmountInput = it },
                label = { Text("Withdrawal Amount (Min ₹200)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = KhelvBluePrimary,
                  unfocusedBorderColor = KhelvNavyBorder,
                  focusedTextColor = Color.White,
                  unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
              )

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1E2238)
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Text("• Daily remaining withdrawal times: 3", color = KhelvTextSecondary, fontSize = 11.sp)
                  Text("• Withdrawal amount range: ₹200.00 - ₹50,000.00", color = KhelvTextSecondary, fontSize = 11.sp)
                  Text("• Arrives directly in your bank account in 5-15 mins", color = KhelvTextSecondary, fontSize = 11.sp)
                }
              }

              Button(
                onClick = {
                  val amt = withdrawAmountInput.toDoubleOrNull() ?: 0.0
                  viewModel.withdraw(amt, withdrawMethod, upiAddressInput)
                  currentSection = WalletSection.HISTORY
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(50.dp)
                  .testTag("submit_withdraw_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(14.dp)
              ) {
                Text(
                  text = "CONFIRM WITHDRAWAL",
                  fontWeight = FontWeight.Black,
                  fontSize = 15.sp,
                  color = Color.White
                )
              }
            }
          }
        }
      }

      WalletSection.HISTORY -> {
        item {
          Text("TRANSACTION HISTORY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        items(transactions) { tx ->
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = KhelvNavySurface,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                val icon = when (tx.type) {
                  TransactionType.DEPOSIT -> "📥"
                  TransactionType.WITHDRAW -> "📤"
                  TransactionType.WIN -> "🎉"
                  TransactionType.BET -> "🎰"
                  TransactionType.BONUS, TransactionType.REWARD -> "🎁"
                }
                Text(icon, fontSize = 22.sp)

                Column {
                  Text(tx.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  Text("${tx.method} • ${tx.timestamp}", color = KhelvTextMuted, fontSize = 11.sp)
                }
              }

              Column(horizontalAlignment = Alignment.End) {
                val prefix = if (tx.type == TransactionType.WITHDRAW || tx.type == TransactionType.BET) "-" else "+"
                val amountColor = if (prefix == "+") KhelvGreen else Color(0xFFEF4444)
                Text(
                  text = "$prefix₹${String.format(Locale.US, "%.2f", tx.amount)}",
                  color = amountColor,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )

                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = when (tx.status) {
                    TransactionStatus.SUCCESS -> Color(0xFF166534)
                    TransactionStatus.PENDING -> Color(0xFFB45309)
                    TransactionStatus.FAILED -> Color(0xFF991B1B)
                  }
                ) {
                  Text(
                    text = tx.status.name,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}

@Composable
private fun WalletActionPill(
  emoji: String,
  label: String,
  color: Color,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clickable(onClick = onClick)
      .padding(4.dp)
  ) {
    Box(
      modifier = Modifier
        .size(44.dp)
        .clip(CircleShape)
        .background(KhelvNavyCard),
      contentAlignment = Alignment.Center
    ) {
      Text(emoji, fontSize = 20.sp)
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
  }
}
