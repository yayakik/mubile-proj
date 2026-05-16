package com.example.budgetplanner.ViewModell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.budgetplanner.ExpenseViewModel
import com.example.expensetrackingapp.Data.ExpenseUserDataBase

class LoginViewModelFactory(private val db : ExpenseUserDataBase): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}