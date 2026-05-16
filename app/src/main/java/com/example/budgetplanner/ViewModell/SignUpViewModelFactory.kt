package com.example.budgetplanner.ViewModell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensetrackingapp.Data.ExpenseUserDataBase

class SignUpViewModelFactory(private val db : ExpenseUserDataBase): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}