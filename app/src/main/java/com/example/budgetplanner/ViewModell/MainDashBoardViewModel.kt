package com.example.budgetplanner.ViewModell

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetrackingapp.Data.ExpenseEntity
import com.example.expensetrackingapp.Data.ExpenseUserDataBase
import com.example.expensetrackingapp.Data.UserEntity
import kotlinx.coroutines.launch

class MainDashBoardViewModel(
    private val db: ExpenseUserDataBase,
    private val userId: Int
) : ViewModel() {

    val userinfo = mutableStateOf<UserEntity?>(null)
    val userexpanses = mutableStateOf<List<ExpenseEntity>>(emptyList())
    val BalancePrice = mutableStateOf(0.0)

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                val user = db.getUserDao().getUserById(userId)
                userinfo.value = user
                
                val expenses = db.getExpenseDao().get_all_Expense_aboutUser(userId)
                userexpanses.value = expenses
                
                if (user != null) {
                    val total = expenses.sumOf { it.amount }
                    db.getUserDao().updateTotExpense(userId, total)
                    BalancePrice.value = user.budget - total
                }
            } catch (e: Exception) {}
        }
    }

    fun load_userinfo() {
        viewModelScope.launch {
            try {
                userinfo.value = db.getUserDao().getUserById(userId)
            } catch (e: Exception) {}
        }
    }

    fun load_userExpances() {
        viewModelScope.launch {
            try {
                userexpanses.value = db.getExpenseDao().get_all_Expense_aboutUser(userId)
                CalcBalancePrice()
            } catch (e: Exception) {}
        }
    }

    fun CalcBalancePrice() {
        val user = userinfo.value
        val expenses = userexpanses.value

        if (user != null) {
            val total = expenses.sumOf { it.amount }
            viewModelScope.launch {
                try {
                    db.getUserDao().updateTotExpense(userId, total)
                    BalancePrice.value = user.budget - total
                } catch (e: Exception) {}
            }
        }
    }

    fun deleteExpenseById(expenseId: Int) {
        viewModelScope.launch {
            try {
                db.getExpenseDao().deleteExpensePerId(expenseId)
                refreshData()
            } catch (e: Exception) {}
        }
    }
}
