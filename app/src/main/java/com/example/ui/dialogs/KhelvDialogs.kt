package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import java.util.Locale
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.model.*
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.viewmodel.ActiveGameScreen
import com.example.viewmodel.KhelvViewModel

@Composable
fun AuthDialog(
  viewModel: KhelvViewModel,
  onDismiss: () -> Unit
) {
  var isRegister by remember { mutableStateOf(false) }
  var phone by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var inviteCode by remember { mutableStateOf("75482128162") }
  var agreedTerms by remember { mutableStateOf(true) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = KhelvNavySurface,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("auth_dialog")
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text("👑", fontSize = 20.sp)
            Text(
              text = "khelv.com",
              color = KhelvGold,
              fontWeight = FontWeight.Black,
              fontSize = 18.sp
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = KhelvTextSecondary)
          }
        }

        // Blue/Red Gradient Notice Banner (from Screenshot 3)
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color.Transparent,
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(
                androidx.compose.ui.graphics.Brush.horizontalGradient(
                  listOf(Color(0xFFDC2626), Color(0xFF1D4ED8))
                )
              )
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = if (isRegister) "Please register by phone number or email" else "Sign in to your Khelv account",
              color = Color.White,
              fontWeight = FontWeight.SemiBold,
              fontSize = 11.sp
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = KhelvNavyCard
          ) {
            Row(modifier = Modifier.padding(4.dp)) {
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (!isRegister) KhelvBluePrimary else Color.Transparent,
                modifier = Modifier.clickable { isRegister = false }
              ) {
                Text(
                  text = "Log In",
                  color = if (!isRegister) Color.White else KhelvTextSecondary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
              }
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isRegister) Color(0xFFEF4444) else Color.Transparent,
                modifier = Modifier.clickable { isRegister = true }
              ) {
                Text(
                  text = "Register Phone",
                  color = if (isRegister) Color.White else KhelvTextSecondary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
              }
            }
          }
        }

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Phone number (+91)") },
          placeholder = { Text("Please enter the phone number", color = KhelvTextMuted) },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = KhelvBluePrimary,
            unfocusedBorderColor = KhelvNavyBorder,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = password,
          onValueChange = { password = it },
          label = { Text("Set password") },
          visualTransformation = PasswordVisualTransformation(),
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = KhelvBluePrimary,
            unfocusedBorderColor = KhelvNavyBorder,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp)
        )

        if (isRegister) {
          OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = KhelvBluePrimary,
              unfocusedBorderColor = KhelvNavyBorder,
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
          )

          OutlinedTextField(
            value = inviteCode,
            onValueChange = { inviteCode = it },
            label = { Text("Invite code") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = KhelvBluePrimary,
              unfocusedBorderColor = KhelvNavyBorder,
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { agreedTerms = !agreedTerms }
          ) {
            Checkbox(
              checked = agreedTerms,
              onCheckedChange = { agreedTerms = it },
              colors = CheckboxDefaults.colors(checkedColor = Color(0xFFEF4444))
            )
            Text(
              text = "I have read and agree ",
              color = KhelvTextSecondary,
              fontSize = 12.sp
            )
            Text(
              text = "【Privacy Agreement】",
              color = Color(0xFFEF4444),
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }

        Button(
          onClick = {
            if (isRegister && !agreedTerms) {
              viewModel.showToast("Please agree to Privacy Agreement")
              return@Button
            }
            viewModel.showToast(if (isRegister) "Registered successfully! +₹100 bonus added." else "Welcome back!")
            onDismiss()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("auth_submit_btn"),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isRegister) Color(0xFFEF4444) else KhelvBluePrimary
          ),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text(if (isRegister) "Register" else "Log In", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        TextButton(
          onClick = { isRegister = !isRegister },
          modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
          Text(
            text = if (isRegister) "Already have an account? Log In" else "New user? Register with Invite Code",
            color = KhelvBlueLight,
            fontSize = 13.sp
          )
        }
      }
    }
  }
}

@Composable
fun GiftRedeemDialog(
  viewModel: KhelvViewModel,
  onDismiss: () -> Unit
) {
  var codeInput by remember { mutableStateOf("KHELV2026") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = KhelvNavySurface,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("gift_dialog")
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text("🎁", fontSize = 24.sp)
            Column {
              Text("Hi!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              Text("We have a gift for you", color = KhelvGold, fontSize = 12.sp)
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = KhelvTextSecondary)
          }
        }

        Text(
          text = "Please enter the gift code below:",
          color = KhelvTextSecondary,
          fontSize = 13.sp
        )

        OutlinedTextField(
          value = codeInput,
          onValueChange = { codeInput = it },
          label = { Text("Gift code (e.g. KHELV2026)") },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = KhelvBluePrimary,
            unfocusedBorderColor = KhelvNavyBorder,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("KHELV2026", "WELCOME100", "LUCKY777").forEach { code ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF1E2238),
              modifier = Modifier.clickable { codeInput = code }
            ) {
              Text(
                text = code,
                color = KhelvBlueLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }
        }

        Button(
          onClick = { viewModel.redeemGift(codeInput) },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("redeem_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("Receive", fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color.White)
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFF16192C)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text("• One code claims one gift. Cash is credited directly.", color = KhelvTextMuted, fontSize = 11.sp)
            Text("• Coupons and cash vouchers are valid immediately.", color = KhelvTextMuted, fontSize = 11.sp)
          }
        }
      }
    }
  }
}

@Composable
fun CustomerServiceDialog(
  viewModel: KhelvViewModel,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = KhelvNavySurface,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("support_dialog")
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text("🤖", fontSize = 24.sp)
            Column {
              Text("Self Service Center", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              Text("24/7 Intelligent Service", color = KhelvGreen, fontSize = 11.sp)
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = KhelvTextSecondary)
          }
        }

        val supportTopics = listOf(
          "Deposit Not Received (Instant Check)" to "💳",
          "Withdrawal Problem / Acceleration" to "🏧",
          "IFSC Modification & Bank Verification" to "🏦",
          "Change ID Login Password" to "🔐",
          "Live Human Chat Agent (Telegram)" to "💬"
        )

        supportTopics.forEach { (topic, emoji) ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E2238),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                viewModel.showToast("Support ticket created for: $topic. Agent will respond.")
                onDismiss()
              }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(emoji, fontSize = 18.sp)
                Text(topic, color = Color.White, fontSize = 13.sp)
              }
              Text("›", color = KhelvTextSecondary, fontSize = 18.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
fun RebateDialog(
  viewModel: KhelvViewModel,
  onDismiss: () -> Unit
) {
  val user by viewModel.user.collectAsState()
  var selectedTab by remember { mutableStateOf("All") }
  val tabs = listOf("All", "Lottery", "Casino", "Rummy/Slots")

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = KhelvNavySurface,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("rebate_dialog")
    ) {
      Column(
        modifier = Modifier
          .padding(18.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Rebate",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = KhelvTextSecondary)
          }
        }

        // Category Pills (Screenshot 1)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          tabs.forEach { tab ->
            val isSelected = tab == selectedTab
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) Color(0xFFEF4444) else Color(0xFF1E2238),
              modifier = Modifier.clickable { selectedTab = tab }
            ) {
              Text(
                text = tab,
                color = if (isSelected) Color.White else KhelvTextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
              )
            }
          }
        }

        // All-Total betting rebate card (Screenshot 1)
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color(0xFF11173D),
          border = androidx.compose.foundation.BorderStroke(1.dp, KhelvNavyBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "All-Total betting rebate",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              )
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF1E3A8A)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(12.dp))
                  Text("Real-time count", color = Color(0xFF93C5FD), fontSize = 10.sp)
                }
              }
            }

            // Big Rebate Balance
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text("🪙", fontSize = 24.sp)
              Text(
                text = String.format(Locale.US, "%.2f", user.todayRebate),
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp
              )
            }

            // Upgrade VIP badge
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF1E2958),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "Upgrade VIP level to increase rebate rate",
                color = Color(0xFF93C5FD),
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }

            // Today rebate & Total rebate grid
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1E2238),
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Text("Today rebate", color = KhelvTextSecondary, fontSize = 11.sp)
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = String.format(Locale.US, "%.2f", user.todayRebate),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1E2238),
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Text("Total rebate", color = KhelvTextSecondary, fontSize = 11.sp)
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = String.format(Locale.US, "%.2f", user.totalRebate),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                  )
                }
              }
            }

            Text(
              text = "Automatic code washing at 01:00:00 every morning",
              color = KhelvTextMuted,
              fontSize = 11.sp
            )

            Button(
              onClick = {
                viewModel.claimRebate()
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("one_click_rebate_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
              shape = RoundedCornerShape(24.dp)
            ) {
              Text(
                text = "One-Click Rebate",
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }
          }
        }

        // Rebate history section
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Rebate history",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )

          Button(
            onClick = {
              viewModel.showToast("All rebate washings settled up to date.")
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2238)),
            shape = RoundedCornerShape(22.dp)
          ) {
            Text("All history", color = Color(0xFF93C5FD), fontSize = 13.sp)
          }
        }
      }
    }
  }
}

@Composable
fun NotificationDialog(
  viewModel: KhelvViewModel,
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableStateOf("Notification") }
  val notices = remember {
    mutableStateListOf(
      Triple(
        "New Member",
        "2026-11-03 12:14:35",
        "Thank you for becoming a beloved member of this platform. We provide many industry leading games. This is the world's leading gaming platform. Try the lottery game developed by us. While enjoying the best gaming experience, you can also join unlimited agents and stay at home to earn money."
      ),
      Triple(
        "System Upgrade & Speedup",
        "2026-11-08 09:30:12",
        "Khelv v20 has upgraded server nodes in Mumbai and Singapore for zero-lag live Aviator crash sync and instant 30-second WinGo lottery settlements."
      ),
      Triple(
        "VIP High Roller Rebate",
        "2026-11-09 01:00:00",
        "Daily code washing has credited ₹15.20 into your active rebate wallet. Upgrade to VIP 1 for 0.20% higher returns on every single bet!"
      )
    )
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = KhelvNavySurface,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("notification_dialog")
    ) {
      Column(
        modifier = Modifier
          .padding(18.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Notification",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = KhelvTextSecondary)
          }
        }

        // Tabs: Notification / Information (Screenshot 2)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          listOf("Notification", "Information").forEach { tab ->
            val isSelected = tab == selectedTab
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) KhelvBluePrimary else Color(0xFF1E2238),
              modifier = Modifier
                .weight(1f)
                .clickable { selectedTab = tab }
            ) {
              Text(
                text = tab,
                color = if (isSelected) Color.White else KhelvTextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(vertical = 10.dp)
              )
            }
          }
        }

        // Notice Cards (Screenshot 2)
        notices.forEachIndexed { index, (title, time, body) ->
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF151C47),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text("✉️", fontSize = 14.sp)
                  Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                }
                IconButton(
                  onClick = { notices.removeAt(index) },
                  modifier = Modifier.size(24.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = KhelvTextSecondary,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }

              Text(
                text = time,
                color = KhelvTextMuted,
                fontSize = 11.sp
              )

              Spacer(modifier = Modifier.height(2.dp))

              Text(
                text = body,
                color = KhelvTextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
              )
            }
          }
        }

        Text(
          text = "No more",
          color = KhelvTextMuted,
          fontSize = 12.sp,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center,
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
        )

        // Floating Robot Support Avatar in corner
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Surface(
            shape = CircleShape,
            color = Color(0xFF3B82F6),
            shadowElevation = 6.dp,
            modifier = Modifier
              .size(46.dp)
              .clickable {
                viewModel.showCustomerService.value = true
                onDismiss()
              }
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text("🤖", fontSize = 24.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
fun GameRulesTricksDialog(
  viewModel: KhelvViewModel,
  onDismiss: () -> Unit
) {
  var selectedGameTab by remember { mutableStateOf("WinGo / Colour") }
  val gameTabs = listOf("WinGo / Colour", "Aviator / Crash", "Mines 5x5", "Dragon Tiger", "Slots & Fish", "Cricket")

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = KhelvNavySurface,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("rules_dialog")
    ) {
      Column(
        modifier = Modifier
          .padding(18.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text("📖", fontSize = 22.sp)
            Column {
              Text(
                text = "Game Rules & Winning Tricks",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
              Text(
                text = "Formulas, odds & pro strategies",
                color = KhelvGold,
                fontSize = 11.sp
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = KhelvTextSecondary)
          }
        }

        // Horizontal Category Tabs
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          gameTabs.forEach { tab ->
            val isSelected = tab == selectedGameTab
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) KhelvBluePrimary else Color(0xFF1E2238),
              modifier = Modifier.clickable { selectedGameTab = tab }
            ) {
              Text(
                text = tab,
                color = if (isSelected) Color.White else KhelvTextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }

        // Tab Content
        when (selectedGameTab) {
          "WinGo / Colour" -> {
            RuleCard(
              title = "🟢 Win Go 30s / 1m / 3m / 5m (Colour Prediction)",
              badge = "Most Popular",
              badgeColor = Color(0xFF10B981)
            ) {
              Text(
                text = "Rules:\n" +
                  "• 3 Colors: Green (1, 3, 7, 9) = 2x Payout; Red (2, 4, 6, 8) = 2x Payout; Violet (0, 5) = 4.5x Payout.\n" +
                  "• Numbers: Exact prediction (0 to 9) pays 9x.\n" +
                  "• Big / Small: Numbers 0-4 are Small (2x), 5-9 are Big (2x).\n\n" +
                  "⚡ The 3X Winning Investment Formula:\n" +
                  "Never bet all-in. Use the 5-Stage 3X Multiplier Strategy to guarantee profit:\n" +
                  "1. Round 1: Bet ₹10. If WIN -> Profit +₹9.60, reset to ₹10.\n" +
                  "2. Round 2: Bet ₹30 (if round 1 lost). If WIN -> Net Profit +₹19.20.\n" +
                  "3. Round 3: Bet ₹90 (if round 2 lost). If WIN -> Net Profit +₹48.60.\n" +
                  "4. Round 4: Bet ₹270. If WIN -> Net Profit +₹136.00.\n" +
                  "5. Round 5: Bet ₹810. If WIN -> Net Profit +₹398.00.\n\n" +
                  "📊 Trend Chart Reading:\n" +
                  "• ABAB Trend: Alternate Red/Green. Follow alternate bets.\n" +
                  "• AABB Trend: 2 Red, 2 Green pattern.\n" +
                  "• Dragon Trend (AAAAAA): 5+ consecutive same color. Never bet against the Dragon streak!",
                color = KhelvTextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
              )
            }
          }

          "Aviator / Crash" -> {
            RuleCard(
              title = "✈️ Aviator & Crash Multiplier",
              badge = "High Payout",
              badgeColor = Color(0xFFEF4444)
            ) {
              Text(
                text = "Rules:\n" +
                  "• Plane takes off with multiplier scaling from 1.00x to 100x+.\n" +
                  "• Cash out before the plane flies away to collect Bet × Multiplier.\n\n" +
                  "⚡ The 2-Bet Hedging Strategy:\n" +
                  "Place two bets simultaneously before takeoff:\n" +
                  "• Bet 1 (e.g. ₹100): Set Auto-Cashout strictly at 1.50x. When it hits 1.50x, it returns ₹150, which covers BOTH your bets!\n" +
                  "• Bet 2 (e.g. ₹50): Let ride risk-free to 2.50x or 4.00x for 100% pure profit.\n\n" +
                  "⚠️ Cooldown Trick:\n" +
                  "If the plane crashes below 1.10x (instant crash), skip the next 2 rounds. Big multipliers (10x-50x) typically appear after 3-4 consecutive 1.5x-2.0x runs.",
                color = KhelvTextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
              )
            }
          }

          "Mines 5x5" -> {
            RuleCard(
              title = "💎 Mines (5x5 Grid)",
              badge = "98% RTP",
              badgeColor = Color(0xFF059669)
            ) {
              Text(
                text = "Rules:\n" +
                  "• 25 hidden tiles (Gems vs Mines).\n" +
                  "• Each gem increases your cashout multiplier. Hitting a mine ends the round.\n\n" +
                  "⚡ 3-Mines Safe Corner Formula:\n" +
                  "• Set Mine Count = 3.\n" +
                  "• Pick only 3 tiles: Top-Left, Bottom-Right, and Center.\n" +
                  "• At 3 gems with 3 mines, payout is ~1.62x. Cash out immediately!\n" +
                  "• Statistically, opening 3 tiles has over 85% success probability. Consistent small wins compound fast.",
                color = KhelvTextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
              )
            }
          }

          "Dragon Tiger" -> {
            RuleCard(
              title = "🐉 Dragon Tiger & Andar Bahar",
              badge = "Card Classic",
              badgeColor = Color(0xFF7C3AED)
            ) {
              Text(
                text = "Rules:\n" +
                  "• Dragon vs Tiger: 1 card dealt to each side. Highest card wins (King=13 is highest, Ace=1 is lowest).\n" +
                  "• Andar Bahar: Joker card is shown. Dealer deals to Andar/Bahar alternately until matching rank appears.\n\n" +
                  "⚡ Streaks & Deal-Side Advantage:\n" +
                  "• Dragon Tiger Streak: Check the bead plate. 72% of winning streaks last at least 2 rounds. When a side wins 2 consecutive times, follow that side for round 3.\n" +
                  "• Andar Bahar First Card Edge: Statistically, the side dealt first has a 51.5% edge over the opposing side. Bet on first side when streak breaks.",
                color = KhelvTextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
              )
            }
          }

          "Slots & Fish" -> {
            RuleCard(
              title = "🎰 JILI Slots & Fishing Games",
              badge = "Jackpot Frenzy",
              badgeColor = Color(0xFFF59E0B)
            ) {
              Text(
                text = "Top Games: Charge Buffalo, Super Ace, Royal Fishing, Dinosaur Tycoon.\n\n" +
                  "⚡ Volatility Cycling Strategy:\n" +
                  "• Run 20 minimum-bet spins. If 2 or more scatter/free-spin triggers appear, the slot machine is in 'hot cycle'.\n" +
                  "• Increase bet to 2x or 3x for the next 15 spins to trigger the Free Game Jackpot.\n" +
                  "• In Fishing: Never waste bullets on high-HP Boss Dragons alone. Focus on mid-tier fish (10x-30x) to maintain ammo balance until Rage Mode activates.",
                color = KhelvTextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
              )
            }
          }

          "Cricket" -> {
            RuleCard(
              title = "🏏 Cricket Exchange & Live Sports",
              badge = "Sports Live",
              badgeColor = Color(0xFF2563EB)
            ) {
              Text(
                text = "Rules:\n" +
                  "• Back (Betting on a team to win) and Lay (Betting against a team).\n\n" +
                  "⚡ Live In-Play Hedging Trick:\n" +
                  "• Back the favorite team at the start at higher odds (e.g. 1.80).\n" +
                  "• After a good powerplay (1-6 overs), when odds drop to 1.25, 'Lay' or Cash Out to lock guaranteed profit no matter which team wins.",
                color = KhelvTextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
              )
            }
          }
        }

        Button(
          onClick = {
            onDismiss()
            when (selectedGameTab) {
              "WinGo / Colour" -> viewModel.openGame(ActiveGameScreen.WINGO)
              "Aviator / Crash" -> viewModel.openGame(ActiveGameScreen.AVIATOR)
              "Mines 5x5" -> viewModel.openGame(ActiveGameScreen.MINES)
              else -> viewModel.openGame(ActiveGameScreen.AVIATOR)
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("try_game_now_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = KhelvBluePrimary),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("Play & Test Formula Now", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      }
    }
  }
}

@Composable
private fun RuleCard(
  title: String,
  badge: String,
  badgeColor: Color,
  content: @Composable () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color(0xFF151C47),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = badgeColor.copy(alpha = 0.2f)
        ) {
          Text(
            text = badge,
            color = badgeColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      content()
    }
  }
}

@Composable
fun AdminLoginDialog(
  viewModel: KhelvViewModel,
  onDismiss: () -> Unit
) {
  var selectedRole by remember { mutableStateOf(AdminRole.SUPER_ADMIN) }
  var username by remember { mutableStateOf("admin") }
  var password by remember { mutableStateOf("khelv@2026") }
  var code2FA by remember { mutableStateOf("682914") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = Color(0xFF0F172A),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("admin_login_dialog")
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFF8B5CF6)))),
              contentAlignment = Alignment.Center
            ) {
              Text("🛡️", fontSize = 18.sp)
            }
            Column {
              Text("Admin Control Portal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              Text("2FA Authorized Access", color = Color(0xFF10B981), fontSize = 11.sp)
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = KhelvTextSecondary)
          }
        }

        // Role Selector Pills
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          AdminRole.values().forEach { role ->
            val isSelected = role == selectedRole
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B),
              modifier = Modifier
                .weight(1f)
                .clickable { selectedRole = role }
            ) {
              Text(
                text = role.label,
                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(vertical = 10.dp)
              )
            }
          }
        }

        OutlinedTextField(
          value = username,
          onValueChange = { username = it },
          label = { Text("Admin Username") },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF3B82F6),
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = password,
          onValueChange = { password = it },
          label = { Text("Admin Password") },
          visualTransformation = PasswordVisualTransformation(),
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF3B82F6),
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = code2FA,
          onValueChange = { code2FA = it },
          label = { Text("2FA Authenticator Code (6-digit)") },
          placeholder = { Text("682914") },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF10B981),
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp)
        )

        // Helper demo credentials pill
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF1E293B),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "🔐 Quick Access: admin / khelv@2026 • 2FA: 682914",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            modifier = Modifier.padding(8.dp)
          )
        }

        Button(
          onClick = {
            viewModel.loginAdmin(username, password, code2FA, selectedRole)
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("admin_login_submit_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Verify 2FA & Launch Admin Panel", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      }
    }
  }
}
