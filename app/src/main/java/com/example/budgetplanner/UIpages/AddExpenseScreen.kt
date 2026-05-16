package com.example.budgetplanner.UIpages

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetplanner.ExpenseViewModel
import com.example.budgetplanner.ExpenseViewModelFactory
import com.example.budgetplanner.R
import com.example.expensetrackingapp.Data.ExpenseUserDataBase
import com.example.expensetrackingapp.Data.ExpenseEntity
import java.text.SimpleDateFormat
import java.util.*

data class CategoryItem(
    val imageRes: Int,
    val label: String,
    val color: Color
)

@Composable
fun AddExpenseScreen(modifier: Modifier = Modifier, user_Id: Int?) {

    val calendar = Calendar.getInstance()
    var category by remember { mutableStateOf("Entertainment") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }


    var saveMessage by remember { mutableStateOf("") }

    val db = ExpenseUserDataBase.getDatabase(LocalContext.current)
    val viewModel: ExpenseViewModel = viewModel(factory = ExpenseViewModelFactory(db))

    val categoriesList = listOf(
        "Groceries", "Entertainment", "Gas", "Shopping",
        "News Paper", "Transport", "Rent", "Other Category"
    )

    val categoriesData = listOf(
        CategoryItem(R.drawable.groceries, "Groceries", Color(0xFFCEE5F2)),
        CategoryItem(R.drawable.entertainment, "Entertainment", Color(0xFFD8B4FE)),
        CategoryItem(R.drawable.gas, "Gas", Color(0xFFFECACA)),
        CategoryItem(R.drawable.shopping, "Shopping", Color(0xFFFDE68A)),
        CategoryItem(R.drawable.newspaper, "News Paper", Color(0xFFFFE5B4)),
        CategoryItem(R.drawable.transport, "Transport", Color(0xFFBCEAD5)),
        CategoryItem(R.drawable.rent, "Rent", Color(0xFFFFD6E0)),
        CategoryItem(R.drawable.other, "Other Category", Color(0xFFE0E0E0))
    )

    val monthYearFormat = SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
    val currentMonthYear = monthYearFormat.format(calendar.time)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 200.dp)
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            "Add Expense",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ===== Category Dropdown =====
        Text("Category", fontSize = 16.sp)
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categoriesList.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            category = cat
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== Amount =====
        Text("Amount", fontSize = 16.sp)
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            placeholder = { Text("$00,000") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ===== Date TextField =====
        Text("Date", fontSize = 16.sp)
        OutlinedTextField(
            value = date,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Select Date") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ===== Inline Calendar =====
        Text(currentMonthYear, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color(0xFFEFEFEF), RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            Column {
                for (weekStart in 1..daysInMonth step 7) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (day in weekStart until weekStart + 7) {
                            if (day <= daysInMonth) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            if (day == selectedDay) Color(0xFF3F51B5) else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            selectedDay = day
                                            val selectedCal = Calendar.getInstance()
                                            selectedCal.set(
                                                calendar.get(Calendar.YEAR),
                                                calendar.get(Calendar.MONTH),
                                                day
                                            )
                                            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                                            date = sdf.format(selectedCal.time)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        day.toString(),
                                        color = if (day == selectedDay) Color.White else Color.Black
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.size(40.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== Categories Icons =====
        Text("Categories", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        for (i in 0 until categoriesData.size step 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (j in i until i + 4) {
                    val item = categoriesData[j]
                    CategoryIcon(
                        imageRes = item.imageRes,
                        label = item.label,
                        color = item.color,
                        selected = category == item.label
                    ) {
                        category = item.label
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== Save Button =====
        Button(
            onClick = {
                user_Id?.let { id ->
                    val expense = ExpenseEntity(
                        userId = id,
                        category = category,
                        amount = amount.toDouble(),
                        date = date
                    )
                    viewModel.AddExpense(expense)


                    saveMessage = "Expense saved successfully"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save", fontSize = 16.sp)
        }


        if (saveMessage.isNotEmpty()) {
            Text(
                text = saveMessage,
                color = Color(0xFF4CAF50),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
            LaunchedEffect(saveMessage) {
                kotlinx.coroutines.delay(1500)
                saveMessage = ""
            }

        }
    }
}

@Composable
fun CategoryIcon(
    imageRes: Int,
    label: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) color.copy(alpha = 0.8f) else color
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .shadow(if (selected) 4.dp else 0.dp, CircleShape)
                .background(backgroundColor, shape = CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = imageRes),
                contentDescription = label,
                tint = Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}