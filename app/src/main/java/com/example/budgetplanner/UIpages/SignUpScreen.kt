package com.example.budgetplanner.UIpages
import com.example.budgetplanner.R
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetplanner.ViewModell.SignUpViewModel
import com.example.budgetplanner.ViewModell.SignUpViewModelFactory
import com.example.expensetrackingapp.Data.ExpenseUserDataBase
import com.example.expensetrackingapp.Data.UserEntity

// Add these imports
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.budgetplanner.auth.GoogleAuthUiClient
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.example.budgetplanner.auth.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SignUpScreen(
    onSignUpClick: (Int) -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val db = ExpenseUserDataBase.getDatabase(LocalContext.current)
    val viewModel : SignUpViewModel = viewModel(factory = SignUpViewModelFactory(db))

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val googleAuthUiClient = remember {
        GoogleAuthUiClient(context)
    }

    suspend fun getOrCreateGoogleUser(googleUser: UserData): Int {
        val existingUser = db.getUserDao().getUserByEmail(googleUser.userId)
        return if (existingUser != null) {
            existingUser.userId
        } else {
            db.getUserDao().addUser(
                UserEntity(
                    name = googleUser.username ?: "Google User",
                    email = googleUser.userId,
                    Password = null,
                    budget = 0.0,
                    totExpense = 0.0,
                    isGoogleUser = true
                )
            ).toInt()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Account",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Sign Up", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            placeholder = { Text("Enter your Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("Enter your Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    viewModel.AddNewUserandGetId(
                        UserEntity(name = name, email = email, Password = password, budget = 0.0, totExpense = 0.0)
                    )
                } else {
                    println("Please fill all fields")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Sign Up", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider(modifier = Modifier.weight(1f))
            Text(
                text = "OR",
                modifier = Modifier.padding(horizontal = 8.dp),
                fontSize = 14.sp
            )
            Divider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
// Inside the Button composable for Google Sign-In
            onClick = {
                scope.launch {
                    try {
                        // Sign-in on Main thread if GoogleAuthUiClient is safe, else use withContext(Dispatchers.IO)
                        val result = withContext(Dispatchers.IO) { googleAuthUiClient.signIn() }

                        result.data?.let { googleUser ->

                            // DB operations must be in IO
                            val userId = withContext(Dispatchers.IO) {
                                val existingUser = db.getUserDao().getUserByEmail(googleUser.userId)
                                existingUser?.userId ?: db.getUserDao().addUser(
                                    UserEntity(
                                        name = googleUser.username ?: "Google User",
                                        email = googleUser.userId,
                                        Password = null,
                                        budget = 0.0,
                                        totExpense = 0.0,
                                        isGoogleUser = true
                                    )
                                ).toInt()
                            }

                            // Back to Main thread
                            val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            sharedPref.edit()
                                .putBoolean("isLoggedIn", true)
                                .putInt("userId", userId)
                                .apply()

                            onSignUpClick(userId)

                        } ?: run {
                            Toast.makeText(context, result.errorMessage ?: "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(context, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = "Google",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign in with Google")
        }




        val ctx = LocalContext.current
        LaunchedEffect(viewModel.user_id.value) {
            viewModel.user_id.value?.let { id ->


                val sharedPref = ctx.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

                sharedPref.edit()
                    .putBoolean("isLoggedIn", true)
                    .putInt("userId", id)
                    .apply()

                onSignUpClick(id)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBackToLoginClick) {
            Text("Back to Login", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}
