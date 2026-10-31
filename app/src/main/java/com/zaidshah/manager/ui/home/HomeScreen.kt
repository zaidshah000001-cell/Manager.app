package com.zaidshah.manager.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidshah.manager.R
import com.zaidshah.manager.data.local.entities.TransactionType
import com.zaidshah.manager.ui.components.CircularProgressRing
import com.zaidshah.manager.ui.components.InrEntryBottomSheet
import com.zaidshah.manager.ui.components.PresetAvatarChooser
import com.zaidshah.manager.ui.theme.DarkText
import com.zaidshah.manager.ui.theme.ForestGreen
import com.zaidshah.manager.ui.theme.MutedText
import com.zaidshah.manager.ui.theme.SageTrack
import com.zaidshah.manager.ui.theme.WarmCream
import com.zaidshah.manager.util.CurrencyUtils
import com.zaidshah.manager.util.DateUtils

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToLoans: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showAvatarDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        InrEntryBottomSheet(
            onDismiss = { showAddDialog = false },
            onSave = { type, paise, note, cat ->
                viewModel.addTransaction(type, paise, note, cat)
            }
        )
    }

    if (showAvatarDialog) {
        PresetAvatarChooser(
            selectedId = uiState.avatarId,
            onSelect = { onNavigateToSettings() },
            onDismiss = { showAvatarDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.hey_user, uiState.profileName),
                    fontSize = 14.sp,
                    color = MutedText
                )
                Text(
                    text = DateUtils.getGreeting(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SageTrack)
                    .clickable { showAvatarDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = ForestGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Card A: Daily earnings
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showAddDialog = true },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.daily_earnings),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CircularProgressRing(
                        percentage = uiState.goalPercentage,
                        caption = stringResource(R.string.daily_goal),
                        size = 94.dp,
                        strokeWidth = 7.dp
                    )

                    Column(
                        modifier = Modifier.padding(start = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column {
                            Text(text = stringResource(R.string.earned), fontSize = 12.sp, color = MutedText)
                            Text(
                                text = CurrencyUtils.formatPaise(uiState.earnedPaise),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        }
                        Column {
                            Text(text = stringResource(R.string.spent), fontSize = 12.sp, color = MutedText)
                            Text(
                                text = CurrencyUtils.formatPaise(uiState.spentPaise),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        }
                        Column {
                            Text(text = stringResource(R.string.net), fontSize = 12.sp, color = MutedText)
                            Text(
                                text = CurrencyUtils.formatPaise(uiState.netPaise),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.netPaise >= 0) ForestGreen else Color.Red
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Keep the selected-day indicator in the same centre position.
                // Horizontal swipes change the dates, not the indicator's location.
                var dateDrag by remember { mutableStateOf(0f) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(uiState.selectedDate) {
                            detectHorizontalDragGestures(
                                onDragStart = { dateDrag = 0f },
                                onHorizontalDrag = { change, dragAmount ->
                                    change.consume()
                                    dateDrag += dragAmount
                                },
                                onDragEnd = {
                                    val steps = (kotlin.math.abs(dateDrag) / 55f).toInt().coerceAtLeast(1).coerceAtMost(7)
                                    if (kotlin.math.abs(dateDrag) > 30f) {
                                        val delta = if (dateDrag < 0) steps.toLong() else -steps.toLong()
                                        viewModel.setSelectedDate(uiState.selectedDate.plusDays(delta))
                                    }
                                    dateDrag = 0f
                                },
                                onDragCancel = { dateDrag = 0f }
                            )
                        },
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val base = uiState.selectedDate
                    (-2..2).forEach { offset ->
                        val date = base.plusDays(offset.toLong())
                        val isSelected = offset == 0
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) ForestGreen else Color.Transparent)
                                .clickable { viewModel.setSelectedDate(date) }
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = date.dayOfWeek.name.take(3),
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else MutedText
                            )
                            Text(
                                text = "${date.dayOfMonth}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else DarkText
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Card B: Loans & Lending
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToLoans() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.loans_lending),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MutedText
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (uiState.recentLoans.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_loans_yet),
                        fontSize = 13.sp,
                        color = MutedText,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        uiState.recentLoans.forEach { tile ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SageTrack)
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tile.initial,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = tile.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkText,
                                    maxLines = 1
                                )
                                Text(text = tile.direction, fontSize = 10.sp, color = MutedText)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = CurrencyUtils.formatPaise(tile.balancePaise),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Card C: Recent transactions
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.recent_transactions),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText
                    )
                    Text(
                        text = stringResource(R.string.see_all),
                        fontSize = 13.sp,
                        color = ForestGreen,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (uiState.recentTransactions.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_transactions_yet),
                        fontSize = 13.sp,
                        color = MutedText,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    uiState.recentTransactions.forEachIndexed { index, tx ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SageTrack),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (tx.type == TransactionType.EARN) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = if (tx.type == TransactionType.EARN) ForestGreen else DarkText,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = tx.note,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = DarkText
                                    )
                                    Text(
                                        text = DateUtils.formatDisplayDate(tx.occurredAt),
                                        fontSize = 11.sp,
                                        color = MutedText
                                    )
                                }
                            }
                            Text(
                                text = CurrencyUtils.formatPaise(
                                    tx.amountPaise,
                                    showSign = true
                                ),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (tx.type == TransactionType.EARN) ForestGreen else DarkText
                            )
                        }
                        if (index < uiState.recentTransactions.lastIndex) {
                            Divider(color = SageTrack.copy(alpha = 0.5f), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(92.dp))
    }
}
