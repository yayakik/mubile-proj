package com.example.budgetplanner

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetplanner.UIpages.*
import com.example.budgetplanner.ui.theme.BudgetPlannerTheme
import androidx.compose.foundation.layout.padding
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        val savedUserId = sharedPref.getInt("userId", -1)

        setContent {
            BudgetPlannerTheme {
                val navController = rememberNavController()

                AppNavigation(
                    navController = navController,
                    isLoggedIn = isLoggedIn,
                    savedUserId = if (savedUserId != -1) savedUserId else null
                )
            }
        }
    }
}

@SuppressLint("ComposableDestinationInComposeScope")
@Composable
fun AppNavigation(
    navController: NavHostController,
    isLoggedIn: Boolean,
    savedUserId: Int?
) {

    var currentUserId by remember { mutableStateOf(savedUserId) }


    //val start = if (isLoggedIn && savedUserId != null) "dashboard" else "login"
    val start = "splash"

    NavHost(navController = navController, startDestination = start) {

        // ===== Login Screen =====
        composable("login") {
            LoginScreen(
                onLoginClick = { id ->
                    currentUserId = id
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate("signup") }
            )
        }

        // ===== SignUp Screen =====
        composable("signup") {
            SignUpScreen(
                onSignUpClick = { id ->
                    currentUserId = id
                    navController.navigate("dashboard") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onBackToLoginClick = { navController.popBackStack() }
            )
        }

        // ===== Dashboard Screen =====
        composable("dashboard") {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                currentUserId?.let { userId ->
                    MainDashboardScreen(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        userId = userId
                    )
                }
            }
        }

        // ===== Add Expense =====
        composable("addExpense") {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                AddExpenseScreen(
                    modifier = Modifier.padding(innerPadding),
                    user_Id = currentUserId
                )
            }
        }

        // ===== Account Screen =====
        composable("account") {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                currentUserId?.let { userId ->
                    AccountScreen(
                        modifier = Modifier.padding(innerPadding),
                        user_Id = userId,
                        onEditClick = { navController.navigate("editAccount") },
                        onLogoutClick = {

                            // delete  info  SharedPreferences
                            val ctx = navController.context
                            val sharedPref = ctx.getSharedPreferences("user_prefs", MODE_PRIVATE)
                            sharedPref.edit()
                                .putBoolean("isLoggedIn", false)
                                .putInt("userId", -1)
                                .apply()


                            navController.navigate("login") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }

        // ===== Edit Account =====
        composable("editAccount") {
            currentUserId?.let { userId ->
                EditAccountScreen(
                    user_Id = userId,
                    navController = navController
                )
            }
        }
        // ===== splash =====
        composable("splash") {
            SplashScreen(
                navController = navController,
                isLoggedIn = isLoggedIn,
                savedUserId = savedUserId
            )
        }
    }
}