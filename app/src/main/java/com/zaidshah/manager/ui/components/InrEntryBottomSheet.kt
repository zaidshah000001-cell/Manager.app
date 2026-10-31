package com.zaidshah.manager.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaidshah.manager.data.local.entities.TransactionType
import com.zaidshah.manager.ui.theme.DarkText
import androidx.compose.material3.MaterialTheme
import com.zaidshah.manager.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InrEntryBottomSheet(
    onDismiss: () -> Unit,
    onSave: (type: TransactionType, amountPaise: Long, note: String, category: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(TransactionType.EARN) }
    var amountInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }
    var categoryInput by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Record Transaction",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(16.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = selectedType == TransactionType.EARN,
                    onClick = { selectedType = TransactionType.EARN },
                    shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                ) {
                    Text("Earn")
                }
                SegmentedButton(
                    selected = selectedType == TransactionType.SPEND,
                    onClick = { selectedType = TransactionType.SPEND },
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                ) {
                    Text("Spend")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amountInput,
                onValueChange = {
                    amountInput = it
                    errorMsg = null
                },
                label = { Text("Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                isError = errorMsg != null,
                supportingText = errorMsg?.let { { Text(it, color = Color.Red) } }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = noteInput,
                onValueChange = { noteInput = it },
                label = { Text("Note (e.g. Rapido earning, Fuel)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = categoryInput,
                onValueChange = { categoryInput = it },
                label = { Text("Category (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = DarkText)
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        val paise = CurrencyUtils.parseRupeesToPaise(amountInput)
                        if (paise == null || paise <= 0) {
                            errorMsg = "Please enter a valid amount greater than 0"
                        } else {
                            onSave(
                                selectedType,
                                paise,
                                noteInput.ifBlank { if (selectedType == TransactionType.EARN) "Income" else "Expense" },
                                categoryInput.ifBlank { "General" }
                            )
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
