package com.example.budgetplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensetrackingapp.Data.ExpenseUserDataBase

class ExpenseViewModelFactory(private val db: ExpenseUserDataBase) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            return ExpenseViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}