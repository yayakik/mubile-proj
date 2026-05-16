package com.example.budgetplanner.ViewModell

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetrackingapp.Data.ExpenseUserDataBase
import kotlinx.coroutines.launch

class LoginViewModel(private val db: ExpenseUserDataBase): ViewModel() {

    var loginResult = mutableStateOf(false)
    var user_id = mutableStateOf<Int?>(null)
    var loginError = mutableStateOf("")

    fun getUserByEmailandPassword(email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = db.getUserDao().login(email, password)
                if (user != null) {
                    user_id.value = user.userId
                    loginResult.value = true
                    loginError.value = ""
                } else {
                    loginResult.value = false
                    loginError.value = "Email or Password is incorrect"
                }
            } catch (e: Exception) {
                loginResult.value = false
                loginError.value = "An error occurred: ${e.message}"
            }
        }
    }
}
