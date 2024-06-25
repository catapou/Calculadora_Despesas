package com.example.tiptime

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.util.Locale

data class Expense(var name: String, var amount: String)

class MainViewModel : ViewModel() {
    var salaryInput = mutableStateOf("")
    var expenses = mutableStateListOf(Expense("", ""))

    private var salary: Double = 0.0
        get() = salaryInput.value.toDoubleOrNull() ?: 0.0

    private var totalExpenses: Double = 0.0
        get() = expenses.mapNotNull { it.amount.toDoubleOrNull() }.sum()

    var remainingAmount: String = ""
        get() = calculateRemainingAmount(salary, totalExpenses)

    fun addExpense() {
        expenses.add(Expense("", ""))
    }

    fun removeExpense() {
        if (expenses.size > 1) expenses.removeAt(expenses.size - 1)
    }

    fun updateExpenseName(index: Int, newName: String) {
        expenses[index] = expenses[index].copy(name = newName)
    }

    fun updateExpenseAmount(index: Int, newAmount: String) {
        expenses[index] = expenses[index].copy(amount = newAmount)
    }

    private fun calculateRemainingAmount(salary: Double, expenses: Double): String {
        val remainingAmount = salary - expenses
        return NumberFormat.getCurrencyInstance(Locale.FRANCE).format(remainingAmount)
    }
}