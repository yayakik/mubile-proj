package com.example.budgetplanner.UIpages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetplanner.R
import com.example.budgetplanner.ViewModell.EditFactory
import com.example.budgetplanner.ViewModell.EditViewModel
import com.example.budgetplanner.ViewModell.MainDashBoardViewModel
import com.example.budgetplanner.ViewModell.MainDashboardViewModelFactory
import com.example.expensetrackingapp.Data.ExpenseUserDataBase

@Composable
fun EditAccountScreen(
    user_Id: Int,
    navController: NavHostController
) {
    val db = ExpenseUserDataBase.getDatabase(LocalContext.current)
    val mainViewModel: MainDashBoardViewModel =
        viewModel(factory = MainDashboardViewModelFactory(db, user_Id))
    val editViewModel: EditViewModel =
        viewModel(factory = EditFactory(db, user_Id))

    var budgetState = remember { mutableStateOf(0.0) }
    var passwordState = remember { mutableStateOf("") }


    LaunchedEffect(mainViewModel.userinfo.value) {
        mainViewModel.userinfo.value?.let { user ->
            budgetState.value = user.budget
            passwordState.value = user.Password.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text(
            text = "Edit Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3F51B5)
        )


        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8EAF6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Color(0xFF3F51B5),
                modifier = Modifier.size(200.dp)
            )
        }


        OutlinedTextField(
            value = budgetState.value.toString(),
            onValueChange = {
                budgetState.value = it.toDoubleOrNull() ?: 0.0
            },
            label = { Text("Income") },

            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Password") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null)
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )


        Button(
            onClick = {
                editViewModel.UpdateUserBudget(budgetState.value)
                editViewModel.UpdateUserPassword(passwordState.value)
                navController.navigateUp()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
        ) {
            Text("Save", fontSize = 16.sp, color = Color.White)
        }
    }
}