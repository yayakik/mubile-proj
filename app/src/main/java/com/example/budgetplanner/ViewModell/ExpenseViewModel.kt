package com.example.budgetplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetrackingapp.Data.ExpenseUserDataBase
import com.example.expensetrackingapp.Data.ExpenseEntity
import kotlinx.coroutines.launch

class ExpenseViewModel(private val db: ExpenseUserDataBase) : ViewModel() {
    fun AddExpense(User_Expense: ExpenseEntity) {
        viewModelScope.launch {
            try {
                db.getExpenseDao().addExpense(User_Expense)
                db.getUserDao().getUserById(User_Expense.userId)
            } catch (e: Exception) {}
        }
    }
    fun get_AllExpense_per_UserID(User_Expense: ExpenseEntity) {
        viewModelScope.launch {
            try {
                db.getExpenseDao().get_all_Expense_aboutUser(User_Expense.userId)
            } catch (e: Exception) {}
        }
    }
    fun get_AllExpense_per_Date(User_Expense: ExpenseEntity) {
        viewModelScope.launch {
            try {
                db.getExpenseDao().get_all_Expense_aboutUser_WithDate(User_Expense.userId,User_Expense.date)
            } catch (e: Exception) {}
        }
    }
    fun DeleteExpensePerDate(User_Expense: ExpenseEntity){
        viewModelScope.launch {
            try {
                db.getExpenseDao().deleteExpensePerDate(User_Expense.date)
            } catch (e: Exception) {}
        }
    }
    fun DeleteExpensePerID(User_Expense: ExpenseEntity){
        viewModelScope.launch {
            try {
                db.getExpenseDao().deleteExpensePerId(User_Expense.expenseid)
            } catch (e: Exception) {}
        }
    }
    fun deleteExpenseById(expenseId: Int) {
        viewModelScope.launch {
            try {
                db.getExpenseDao().deleteExpensePerId(expenseId)
            } catch (e: Exception) {}
        }
    }
}
