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

    init {

        if (expenses.isEmpty()) {
            expenses.add(Expense("", ""))
        }
    }

    private var salary: Double = 0.0
        get() = salaryInput.value.toDoubleOrNull() ?: 0.0

    private var totalExpenses: Double = 0.0
        get() = expenses.sumByDouble { it.amount.toDoubleOrNull() ?: 0.0 }

    var remainingAmount: String = ""
        get() = calculateRemainingAmount(salary, totalExpenses)
        private set

    fun addExpense() {
        expenses.add(Expense("", ""))
    }

    fun removeExpense() {
        if (expenses.size > 1) expenses.removeAt(expenses.size - 1)
    }

    fun updateExpenseName(index: Int, newName: String) {
        if (index in expenses.indices) {
            expenses[index] = expenses[index].copy(name = newName)
        }
    }

    fun updateExpenseAmount(index: Int, newAmount: String) {
        if (index in expenses.indices) {
            expenses[index] = expenses[index].copy(amount = newAmount)
        }
    }

    private fun calculateRemainingAmount(salary: Double, expenses: Double): String {
        val remainingAmount = salary - expenses
        return NumberFormat.getCurrencyInstance(Locale.FRANCE).format(remainingAmount)
    }
}
