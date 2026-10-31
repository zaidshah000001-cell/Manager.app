package com.zaidshah.manager.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidshah.manager.R
import com.zaidshah.manager.data.local.entities.GoalType
import com.zaidshah.manager.ui.components.CircularProgressRing
import com.zaidshah.manager.ui.theme.DarkText
import com.zaidshah.manager.ui.theme.ForestGreen
import com.zaidshah.manager.ui.theme.MutedText
import com.zaidshah.manager.ui.theme.SageTrack
import com.zaidshah.manager.ui.theme.WarmCream
import com.zaidshah.manager.util.CurrencyUtils

@Composable
fun GoalsScreen(viewModel: GoalsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var editingGoalType by remember { mutableStateOf<GoalType?>(null) }
    var targetInput by remember { mutableStateOf("") }

    if (editingGoalType != null) {
        AlertDialog(
            onDismissRequest = { editingGoalType = null },
            title = { Text("Edit Target") },
            text = {
                OutlinedTextField(
                    value = targetInput,
                    onValueChange = { targetInput = it },
                    label = { Text("New Target Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val paise = CurrencyUtils.parseRupeesToPaise(targetInput)
                        if (paise != null && paise > 0) {
                            viewModel.updateTarget(editingGoalType!!, paise)
                        }
                        editingGoalType = null
                    }
                ) {
                    Text("Save", color = ForestGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingGoalType = null }) {
                    Text("Cancel", color = DarkText)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.nav_goals),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Text(
                    text = stringResource(R.string.goals_subtitle),
                    fontSize = 13.sp,
                    color = MutedText
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SageTrack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = ForestGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card A: Your Progress
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.your_progress),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
                Text(
                    text = stringResource(R.string.progress_helper),
                    fontSize = 12.sp,
                    color = MutedText
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(
                        Triple(uiState.dailyProgress, stringResource(R.string.daily), GoalType.DAILY),
                        Triple(uiState.weeklyProgress, stringResource(R.string.weekly), GoalType.WEEKLY),
                        Triple(uiState.monthlyProgress, stringResource(R.string.monthly), GoalType.MONTHLY)
                    ).forEach { (prog, label, type) ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    editingGoalType = type
                                    targetInput = (prog.targetPaise / 100L).toString()
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressRing(
                                percentage = prog.percentage,
                                size = 80.dp,
                                strokeWidth = 7.dp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = label, fontSize = 12.sp, color = MutedText)
                            Text(
                                text = CurrencyUtils.formatPaise(prog.targetPaise),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card B: Completed Goals
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.completed_goals),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText
                    )
                    Text(
                        text = stringResource(R.string.see_all),
                        fontSize = 13.sp,
                        color = ForestGreen
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))

                if (uiState.completedGoals.isEmpty()) {
                    Text(
                        text = "No completed goals yet.",
                        fontSize = 13.sp,
                        color = MutedText,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        uiState.completedGoals.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SageTrack)
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = ForestGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = item.goalType.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DarkText
                                    )
                                    Text(
                                        text = CurrencyUtils.formatPaise(item.targetPaise),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(84.dp))
    }
}
