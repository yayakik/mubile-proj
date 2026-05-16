package com.example.budgetplanner.UIpages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetplanner.R
import com.example.budgetplanner.ViewModell.MainDashBoardViewModel
import com.example.budgetplanner.ViewModell.MainDashboardViewModelFactory
import com.example.expensetrackingapp.Data.ExpenseEntity
import com.example.expensetrackingapp.Data.ExpenseUserDataBase
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingActionButton // for the about app
import com.example.budgetplanner.AboutActivity

// ========================================
// CATEGORY IMAGE SELECTOR
// ========================================
fun getCategoryImage(category: String): Int = when (category.lowercase()) {
    "groceries" -> R.drawable.groceries
    "entertainment" -> R.drawable.entertainment
    "gas" -> R.drawable.gas
    "shopping" -> R.drawable.shopping
    "news paper" -> R.drawable.newspaper
    "transport" -> R.drawable.transport
    "rent" -> R.drawable.rent
    else -> R.drawable.other
}

// ========================================
// MAIN DASHBOARD SCREEN
// ========================================
@Composable
fun MainDashboardScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    userId: Int
) {
    //  database identification to use in the button
    val context = LocalContext.current

    val db = ExpenseUserDataBase.getDatabase(LocalContext.current)
    val viewModel: MainDashBoardViewModel =
        viewModel(factory = MainDashboardViewModelFactory(db, userId))

  //  val list = viewModel.userexpanses.value ?: emptyList()
    val list = viewModel.userexpanses.value
    // Contains the About Button
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AboutActivity::class.java)
                    context.startActivity(intent)
                },
                containerColor = Color(0xFF3F51F5),
                contentColor = Color.White
            ) {
                // Cool icon for the About :)
                Icon(imageVector = Icons.Default.Info, contentDescription = "About App")
            }}
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ===== Greeting =====
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.shopping),
                        contentDescription = null,
                        modifier = Modifier.size(45.dp)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = viewModel.userinfo.value?.name ?: "User",
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp
                        )
                        Text(
                            text = "Good Morning",
                            fontSize = 20.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // ===== Balance Card =====
            item {
                BalanceCard(userId, viewModel)
            }

            // ===== Recent Expenses Header =====
            item {
                Text(
                    text = "Recent Expenses",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ===== Expenses List =====
            if (list.isEmpty()) {
                item {
                    Text(
                        text = "No expenses yet.",
                        color = Color.Gray
                    )
                }
            } else {
                items(list) { expense ->
                    ExpenseCard(
                        expense = expense,
                        onDeleteClick = { id ->
                            viewModel.deleteExpenseById(id)
                        }
                    )
                }
            }
        }
    }
}

// ========================================
// EXPENSE CARD WITH IMAGE
// ========================================
@Composable
fun ExpenseCard(
    expense: ExpenseEntity,
    onDeleteClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(0xFFE8E8E8), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = getCategoryImage(expense.category)),
                        contentDescription = expense.category,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = expense.category,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = expense.date,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = "$${expense.amount}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = { onDeleteClick(expense.expenseid) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = "Delete",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
// ========================================
// BALANCE CARD
// ========================================
@Composable
fun BalanceCard(userId: Int, viewModel: MainDashBoardViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3F51F5)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                text = "Available Balance",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp
            )

            Text(
                text = viewModel.BalancePrice.value.toString(),
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Column {
                    Text("Budget", color = Color.White.copy(0.7f))
                    Text(
                        viewModel.userinfo.value?.budget.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text("Total Expenses", color = Color.White.copy(0.7f))
                    Text(
                        viewModel.userinfo.value?.totExpense.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ========================================
// BOTTOM NAVIGATION BAR
// ========================================
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar(containerColor = Color.White) {

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("dashboard") },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("addExpense") },
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            label = { Text("Add") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("account") },
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
            label = { Text("Account") }
        )
    }
}