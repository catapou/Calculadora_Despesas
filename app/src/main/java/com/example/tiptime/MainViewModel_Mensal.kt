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
        // Inicializa com uma despesa vazia se a lista estiver vazia
        if (expenses.isEmpty()) {
            expenses.add(Expense("", ""))
        }
    }

    private val _remainingAmount = mutableStateOf("")
    val remainingAmount get() = _remainingAmount.value

    private val salary: Double
        get() = salaryInput.value.toDoubleOrNull() ?: 0.0

    private val totalExpenses: Double
        get() = expenses.sumByDouble { it.amount.toDoubleOrNull() ?: 0.0 }

    init {
        updateRemainingAmount()
    }

    fun addExpense() {
        expenses.add(Expense("", ""))
        updateRemainingAmount()
    }

    fun removeExpense() {
        if (expenses.size > 1) {
            expenses.removeAt(expenses.size - 1)
            updateRemainingAmount()
        }
    }

    fun updateExpenseName(index: Int, newName: String) {
        if (index in expenses.indices) {
            expenses[index] = expenses[index].copy(name = newName)
            updateRemainingAmount()
        }
    }

    fun updateExpenseAmount(index: Int, newAmount: String) {
        if (index in expenses.indices) {
            expenses[index] = expenses[index].copy(amount = newAmount)
            updateRemainingAmount()
        }
    }

    private fun updateRemainingAmount() {
        val remaining = calculateRemainingAmount(salary, totalExpenses)
        _remainingAmount.value = remaining
    }

    private fun calculateRemainingAmount(salary: Double, expenses: Double): String {
        val remainingAmount = salary - expenses
        return NumberFormat.getCurrencyInstance(Locale.FRANCE).format(remainingAmount)
    }
}
