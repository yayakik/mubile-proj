package com.example.budgetplanner.ViewModell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetrackingapp.Data.ExpenseUserDataBase
import kotlinx.coroutines.launch

class EditViewModel(private val db: ExpenseUserDataBase,private val user_Id: Int): ViewModel(){
    fun UpdateUserBudget(newbudget: Double) {
        viewModelScope.launch {
            try {
                db.getUserDao().updateBudget(user_Id, newbudget)
            } catch (e: Exception) {}
        }
    }
    fun UpdateUserPassword(pass: String) {
        viewModelScope.launch {
            try {
                db.getUserDao().updatePassword(user_Id, pass)
            } catch (e: Exception) {}
        }
    }
}
