package com.example.budgetplanner.ViewModell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensetrackingapp.Data.ExpenseUserDataBase

class MainDashboardViewModelFactory(
    private val db: ExpenseUserDataBase,
    private val userId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainDashBoardViewModel::class.java)) {
            return MainDashBoardViewModel(db, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
