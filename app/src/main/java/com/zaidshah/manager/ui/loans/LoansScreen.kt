package com.zaidshah.manager.ui.loans

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import com.zaidshah.manager.data.local.entities.LoanDirection
import com.zaidshah.manager.ui.theme.DarkText
import com.zaidshah.manager.ui.theme.ForestGreen
import com.zaidshah.manager.ui.theme.MutedText
import com.zaidshah.manager.ui.theme.SageTrack
import com.zaidshah.manager.ui.theme.WarmCream
import com.zaidshah.manager.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoansScreen(viewModel: LoansViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddLoanDialog by remember { mutableStateOf(false) }

    var nameInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var directionInput by remember { mutableStateOf(LoanDirection.BORROWED) }

    if (showAddLoanDialog) {
        AlertDialog(
            onDismissRequest = { showAddLoanDialog = false },
            title = { Text("Add Loan") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Person Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = directionInput == LoanDirection.BORROWED,
                            onClick = { directionInput = LoanDirection.BORROWED },
                            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                        ) {
                            Text("Borrowed")
                        }
                        SegmentedButton(
                            selected = directionInput == LoanDirection.LENT,
                            onClick = { directionInput = LoanDirection.LENT },
                            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                        ) {
                            Text("Lent")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val paise = CurrencyUtils.parseRupeesToPaise(amountInput)
                        if (nameInput.isNotBlank() && paise != null && paise > 0) {
                            viewModel.addLoan(nameInput.trim(), "", "", directionInput, paise)
                            nameInput = ""
                            amountInput = ""
                            showAddLoanDialog = false
                        }
                    }
                ) {
                    Text("Save", color = ForestGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLoanDialog = false }) {
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
                    text = stringResource(R.string.nav_loans),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Text(
                    text = stringResource(R.string.loans_subtitle),
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

        // Card A: Outstanding overview
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.outstanding_overview),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.you_borrowed),
                            fontSize = 11.sp,
                            color = MutedText
                        )
                        Text(
                            text = CurrencyUtils.formatPaise(uiState.borrowedPaise),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .width(1.dp)
                            .background(SageTrack)
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.you_lent),
                            fontSize = 11.sp,
                            color = MutedText
                        )
                        Text(
                            text = CurrencyUtils.formatPaise(uiState.lentPaise),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card B: Segmented Filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(
                Pair(LoanFilter.ALL, stringResource(R.string.all)),
                Pair(LoanFilter.BORROWED, stringResource(R.string.borrowed)),
                Pair(LoanFilter.LENT, stringResource(R.string.lent))
            ).forEach { (f, label) ->
                val isSelected = uiState.filter == f
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) ForestGreen else Color.Transparent)
                        .clickable { viewModel.setFilter(f) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else DarkText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card C: People
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.people),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText
                    )
                    Text(
                        text = stringResource(R.string.add_loan),
                        fontSize = 13.sp,
                        color = ForestGreen,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { showAddLoanDialog = true }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (uiState.people.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_loans_yet),
                        fontSize = 13.sp,
                        color = MutedText,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    uiState.people.forEachIndexed { index, personWithLoans ->
                        val totalOutstanding = personWithLoans.loans.sumOf { it.outstandingPaise }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SageTrack)
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = personWithLoans.person.name.firstOrNull()?.uppercase() ?: "P",
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = personWithLoans.person.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DarkText
                                    )
                                    Text(
                                        text = "${personWithLoans.loans.size} active loan(s)",
                                        fontSize = 11.sp,
                                        color = MutedText
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = CurrencyUtils.formatPaise(totalOutstanding),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MutedText
                                )
                            }
                        }
                        if (index < uiState.people.lastIndex) {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(84.dp))
    }
}
