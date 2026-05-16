package com.example.budgetplanner.UIpages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.budgetplanner.R
import com.example.budgetplanner.ViewModell.MainDashBoardViewModel
import com.example.budgetplanner.ViewModell.MainDashboardViewModelFactory
import com.example.expensetrackingapp.Data.ExpenseUserDataBase

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    user_Id: Int,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit
){
    val db = ExpenseUserDataBase.getDatabase(LocalContext.current)
    print("User Id: ${user_Id} ")
    val viewModel: MainDashBoardViewModel =
        viewModel(factory = MainDashboardViewModelFactory(db, user_Id))
    val user = viewModel.userinfo.value
    if (user == null) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    } else {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()).padding(bottom = 200.dp)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { onEditClick() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit Profile",
                        tint = Color.Unspecified
                    )
                }
            }
            // ===== User Image =====
            Image(
                painter = painterResource(id = R.drawable.boy),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(400.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ===== Name =====

            Text(
                text = user.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ===== Email =====
            Text(
                text = user.email,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ===== User Info Cards =====
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoCard(icon = Icons.Default.ShoppingCart, title = "Budget", value = user.budget.toString())
                InfoCard(icon = Icons.Default.Lock, title = "Password", value = "********")
                Button(
                    onClick = { onLogoutClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Logout", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}
    @Composable
    fun InfoCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1)),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = value,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }


