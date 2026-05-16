package com.example.budgetplanner.ViewModell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.budgetplanner.ExpenseViewModel
import com.example.expensetrackingapp.Data.ExpenseUserDataBase
class EditFactory(
    private val db: ExpenseUserDataBase,
    private val userId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditViewModel::class.java)) {
            return EditViewModel(db, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
