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
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
import androidx.compose.material3.MaterialTheme
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
    var upiInput by remember { mutableStateOf("") }
    var chosenPerson by remember { mutableStateOf<Long?>(null) }
    var chosenLoan by remember { mutableStateOf<Long?>(null) }
    var showHistory by remember { mutableStateOf(false) }
    var step by remember { mutableStateOf("details") }
    var repaymentText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val dateFmt = remember { DateTimeFormatter.ofPattern("dd MMM yyyy").withZone(ZoneId.systemDefault()) }
    val person = uiState.people.find { it.person.id == chosenPerson }
    val selected = person?.loans?.find { it.loan.id == chosenLoan }

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
                    OutlinedTextField(value = upiInput, onValueChange = { upiInput = it },
                        label = { Text("UPI ID (optional)") }, modifier = Modifier.fillMaxWidth())
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
                            viewModel.addLoan(nameInput.trim(), "", upiInput.trim(), directionInput, paise)
                            nameInput = ""
                            amountInput = ""
                            upiInput = ""
                            showAddLoanDialog = false
                        }
                    }
                ) {
                    Text("Save", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLoanDialog = false }) {
                    Text("Cancel", color = DarkText)
                }
            }
        )
    }

    if (person != null && selected != null) {
        val balance = selected.outstandingPaise
        val loan = selected.loan
        AlertDialog(
            onDismissRequest = { chosenPerson = null; chosenLoan = null; step = "details" },
            title = { Text(if (step == "history") "Repayment history" else person.person.name) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (step == "history") {
                        if (selected.repayments.isEmpty()) Text("No repayments recorded")
                        selected.repayments.forEach { r ->
                            Text("${dateFmt.format(r.occurredAt)} · ${CurrencyUtils.formatPaise(r.amountPaise)}")
                            Spacer(Modifier.height(8.dp))
                        }
                        TextButton(onClick = { step = "details" }) { Text("Back") }
                    } else {
                        Text("Original: ${CurrencyUtils.formatPaise(loan.principalPaise)}")
                        Text("Taken / given: ${dateFmt.format(loan.issuedAt)}")
                        Text("Due: ${loan.dueAt?.let { dateFmt.format(it) } ?: "Not set"}")
                        Text("UPI ID: ${person.person.upiId.ifBlank { "Not set" }}")
                        Text("Repaid: ${CurrencyUtils.formatPaise(selected.totalRepaidPaise)}")
                        Spacer(Modifier.height(8.dp))
                        Text("Outstanding: ${CurrencyUtils.formatPaise(balance)}",
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(12.dp))
                        if (step == "details") {
                            if (balance > 0L) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = {
                                        repaymentText = ""; errorText = ""; step = "pay"
                                    }) { Text("Partial payment") }
                                    Button(onClick = {
                                        repaymentText = (balance / 100.0).toString()
                                        errorText = ""; step = "pay"
                                    }) { Text("Full payment") }
                                }
                            }
                            TextButton(onClick = { step = "history" }) { Text("Payment history") }
                        }
                        if (step == "pay") {
                            OutlinedTextField(
                                value = repaymentText, onValueChange = { repaymentText = it; errorText = "" },
                                label = { Text("Amount (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            val paise = CurrencyUtils.parseRupeesToPaise(repaymentText)
                            val valid = paise != null && paise > 0 && paise <= balance
                            if (errorText.isNotBlank()) Text(errorText, color = Color.Red)
                            Spacer(Modifier.height(8.dp))
                            if (loan.direction == LoanDirection.BORROWED && person.person.upiId.isNotBlank()) {
                                OutlinedButton(onClick = {
                                    if (!valid) errorText = "Enter a valid amount"
                                    else {
                                        val uri = Uri.Builder().scheme("upi").authority("pay")
                                            .appendQueryParameter("pa", person.person.upiId)
                                            .appendQueryParameter("pn", person.person.name)
                                            .appendQueryParameter("am", String.format(java.util.Locale.US, "%.2f", paise!! / 100.0))
                                            .appendQueryParameter("cu", "INR").build()
                                        try { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
                                        catch (_: Exception) { errorText = "No UPI app available" }
                                    }
                                }) { Text("Open UPI app") }
                            } else Text("UPI unavailable for this loan. Record verified external payment.", fontSize = 12.sp)
                            Text("Opening UPI does not confirm payment. Verify it before recording.",
                                fontSize = 12.sp, color = MutedText)
                            Button(onClick = {
                                if (valid) step = "confirm" else errorText = "Enter a valid amount"
                            }) { Text("Payment completed") }
                        }
                        if (step == "confirm") {
                            Text("Have you verified the transfer of ${CurrencyUtils.formatPaise(CurrencyUtils.parseRupeesToPaise(repaymentText) ?: 0L)}?")
                            Button(onClick = {
                                val p = CurrencyUtils.parseRupeesToPaise(repaymentText)
                                if (p != null && p > 0L && p <= balance) {
                                    viewModel.recordRepayment(loan.id, p)
                                    chosenPerson = null; chosenLoan = null; step = "details"
                                }
                            }) { Text("Confirm verified payment") }
                            TextButton(onClick = { step = "pay" }) { Text("Cancel") }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { chosenPerson = null; chosenLoan = null; step = "details" }) { Text("Close") }
            },
            shape = RoundedCornerShape(24.dp)
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
                    tint = MaterialTheme.colorScheme.primary
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
                            color = MaterialTheme.colorScheme.primary
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
                Pair(LoanFilter.LENT, stringResource(R.string.lent)),
                Pair(LoanFilter.ALL, "History")
            ).forEach { (f, label) ->
                val isHistory = label == "History"
                val isSelected = if (isHistory) showHistory else !showHistory && uiState.filter == f
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { showHistory = isHistory; if (!isHistory) viewModel.setFilter(f) }
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
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { showAddLoanDialog = true }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (showHistory) {
                    val completed = uiState.people.flatMap { p ->
                        p.loans.filter { it.isFullyRepaid }.map { p to it }
                    }
                    if (completed.isEmpty()) Text("No settled loans yet", color = MutedText)
                    completed.forEach { (p, item) ->
                        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                            .background(SageTrack).clickable {
                                chosenPerson = p.person.id; chosenLoan = item.loan.id; step = "history"
                            }.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(p.person.name)
                            Text(CurrencyUtils.formatPaise(item.loan.principalPaise))
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                } else if (uiState.people.none { p -> p.loans.any { !it.isFullyRepaid } }) {
                    Text(
                        text = stringResource(R.string.no_loans_yet),
                        fontSize = 13.sp,
                        color = MutedText,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    uiState.people.filter { p -> p.loans.any { !it.isFullyRepaid } }.forEachIndexed { index, personWithLoans ->
                        val totalOutstanding = personWithLoans.loans.sumOf { it.outstandingPaise }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SageTrack)
                                .clickable {
                                    val active = personWithLoans.loans.firstOrNull { !it.isFullyRepaid }
                                    if (active != null) {
                                        chosenPerson = personWithLoans.person.id
                                        chosenLoan = active.loan.id
                                        step = "details"
                                    }
                                }
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
                                        color = MaterialTheme.colorScheme.primary
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
                                        text = "${personWithLoans.loans.count { !it.isFullyRepaid }} active loan(s)",
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
