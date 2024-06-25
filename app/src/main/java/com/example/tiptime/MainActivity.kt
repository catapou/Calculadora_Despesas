package com.example.tiptime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Done
import com.example.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat
import java.util.Locale

data class Expense(var name: String, var amount: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TipTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    SalaryExpenseLayout()
                }
            }
        }
    }
}

@Composable
fun SalaryExpenseLayout() {
    var salaryInput by remember { mutableStateOf("") }
    var expenses by remember { mutableStateOf(listOf(Expense("", ""))) }

    val salary = salaryInput.toDoubleOrNull() ?: 0.0
    val totalExpenses = expenses.mapNotNull { it.amount.toDoubleOrNull() }.sum()
    val remainingAmount = calculateRemainingAmount(salary, totalExpenses)

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.salary_expense_calculator),
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.Start)
        )
        EditNumberField(
            value = salaryInput,
            onValueChanged = { if (it.isNumeric()) salaryInput = it },
            label = stringResource(R.string.salary_amount),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.expense_amounts),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(alignment = Alignment.Start)
        )
        expenses.forEachIndexed { index, expense ->
            ExpenseItem(
                expense = expense,
                onNameChanged = { newName ->
                    expenses = expenses.toMutableList().apply { this[index] = this[index].copy(name = newName) }
                },
                onAmountChanged = { newAmount ->
                    if (newAmount.isNumeric()) {
                        expenses = expenses.toMutableList().apply { this[index] = this[index].copy(amount = newAmount) }
                    }
                },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { expenses = expenses + Expense("", "") }) {
                Text(stringResource(R.string.add_expense))
            }
            Button(onClick = { if (expenses.size > 1) expenses = expenses.dropLast(1) }) {
                Text(stringResource(R.string.remove_expense))
            }
        }
        Text(
            text = stringResource(R.string.remaining_amount, remainingAmount),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(150.dp))
    }
}

@Composable
fun ExpenseItem(
    expense: Expense,
    onNameChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    modifier: Modifier
) {
    var isEditingName by remember { mutableStateOf(false) }
    var isEditingAmount by remember { mutableStateOf(false) }
    var tempAmount by remember { mutableStateOf(expense.amount) }

    Column(
        modifier = modifier
            .background(Color.Gray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isEditingName) {
                TextField(
                    value = expense.name,
                    singleLine = true,
                    onValueChange = onNameChanged,
                    label = { Text(stringResource(R.string.expense_name)) },
                    modifier = Modifier.weight(1f)
                )
            } else {
                Text(
                    text = if (expense.name.isNotBlank()) expense.name else stringResource(R.string.expense_name),
                    modifier = Modifier.weight(1f)
                )
            }
            IconButton(onClick = { isEditingName = !isEditingName }) {
                Icon(
                    imageVector = if (isEditingName) Icons.Filled.Done else Icons.Filled.Edit,
                    contentDescription = if (isEditingName) stringResource(R.string.done) else stringResource(R.string.edit)
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 8.dp)
        ) {
            if (isEditingAmount) {
                TextField(
                    value = tempAmount,
                    singleLine = true,
                    onValueChange = { if (it.isNumeric()) tempAmount = it },
                    label = { Text(if (expense.amount.isEmpty()) stringResource(R.string.expense_amount) else "") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        onAmountChanged(tempAmount)
                        isEditingAmount = false
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(stringResource(R.string.confirm))
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isEditingAmount = true }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (expense.amount.isNotBlank()) expense.amount else stringResource(R.string.expense_amount),
                        modifier = Modifier.padding(end = 4.dp),
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "€",
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
fun EditNumberField(
    value: String,
    onValueChanged: (String) -> Unit,
    label: String,
    modifier: Modifier
) {
    TextField(
        value = value,
        singleLine = true,
        onValueChange = onValueChanged,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}

private fun String.isNumeric(): Boolean {
    return this.toDoubleOrNull() != null
}

/**
 * Calculates the remaining amount after subtracting the expenses from the salary.
 * Formats the remaining amount according to the local currency.
 * Example would be "$10.00".
 */
private fun calculateRemainingAmount(salary: Double, expenses: Double): String {
    val remainingAmount = salary - expenses
    return NumberFormat.getCurrencyInstance(Locale.FRANCE).format(remainingAmount)
}

@Preview(showBackground = true)
@Composable
fun SalaryExpenseLayoutPreview() {
    TipTimeTheme {
        SalaryExpenseLayout()
    }
}
