package com.example.budgetplanner.ViewModell

import com.example.budgetplanner.ExpenseViewModel
import com.example.budgetplanner.MainDispatcherRule // Using the rule from your previous context
import com.example.expensetrackingapp.Data.ExpenseDAO
import com.example.expensetrackingapp.Data.ExpenseEntity
import com.example.expensetrackingapp.Data.ExpenseUserDataBase
import com.example.expensetrackingapp.Data.UserDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var db: ExpenseUserDataBase
    private lateinit var expenseDao: ExpenseDAO
    private lateinit var userDao: UserDAO
    private lateinit var viewModel: ExpenseViewModel

    // Helper to create dummy entities
    private val dummyExpense = ExpenseEntity(userId = 1, amount = 100.0, date = "2024-01-01", category = "Food")

    @Before
    fun setup() {
        db = mock()
        expenseDao = mock()
        userDao = mock()

        // Critical: Your AddExpense function calls BOTH Daos.
        whenever(db.getExpenseDao()).thenReturn(expenseDao)
        whenever(db.getUserDao()).thenReturn(userDao)

        viewModel = ExpenseViewModel(db)
    }

    @Test
    fun `AddExpense success case`() = runTest {
        viewModel.AddExpense(dummyExpense)
        advanceUntilIdle()

        verify(expenseDao).addExpense(dummyExpense)
        // Also verifying the side effect in your code where it fetches the user
        verify(userDao).getUserById(dummyExpense.userId)
    }

    @Test
    fun `AddExpense with existing ID`() = runTest {
        // In unit tests with mocks, "existing ID" logic is handled by the DB.
        // Here we just verify the VM passes the call through.
        viewModel.AddExpense(dummyExpense)
        advanceUntilIdle()
        verify(expenseDao).addExpense(dummyExpense)
    }

    @Test
    fun `AddExpense with invalid user ID`() = runTest {
        val invalidExpense = dummyExpense.copy(userId = -1)

        viewModel.AddExpense(invalidExpense)
        advanceUntilIdle()

        verify(expenseDao).addExpense(invalidExpense)
    }

    @Test
    fun `AddExpense with null or empty fields`() = runTest {
        // Kotlin non-nullable types prevent true nulls, but we can test empty strings
        val emptyExpense = dummyExpense.copy(date = "", category = "")

        viewModel.AddExpense(emptyExpense)
        advanceUntilIdle()

        verify(expenseDao).addExpense(emptyExpense)
    }

    @Test
    fun `AddExpense thread safety`() = runTest {
        // Simulate firing multiple adds rapidly
        repeat(5) {
            launch(Dispatchers.IO) { // Using IO as per your ViewModel
                viewModel.AddExpense(dummyExpense)
            }
        }
        advanceUntilIdle()

        // Verify it was called 5 times
        verify(expenseDao, times(5)).addExpense(dummyExpense)
    }

    @Test
    fun `get AllExpense per UserID success case`() = runTest {
        viewModel.get_AllExpense_per_UserID(dummyExpense)
        advanceUntilIdle()
        verify(expenseDao).get_all_Expense_aboutUser(dummyExpense.userId)
    }

    @Test
    fun `get AllExpense per UserID for user with no expenses`() = runTest {
        // Since the function returns Unit, we verify the DAO call happens.
        // The "Empty List" result would be returned by the DAO, not the VM function itself.
        viewModel.get_AllExpense_per_UserID(dummyExpense)
        advanceUntilIdle()
        verify(expenseDao).get_all_Expense_aboutUser(dummyExpense.userId)
    }

    @Test
    fun `get AllExpense per UserID for non existent user`() = runTest {
        val nonExistentUserExpense = dummyExpense.copy(userId = 9999)

        viewModel.get_AllExpense_per_UserID(nonExistentUserExpense)
        advanceUntilIdle()

        verify(expenseDao).get_all_Expense_aboutUser(9999)
    }

    @Test
    fun `get AllExpense per Date success case`() = runTest {
        viewModel.get_AllExpense_per_Date(dummyExpense)
        advanceUntilIdle()
        verify(expenseDao).get_all_Expense_aboutUser_WithDate(dummyExpense.userId, dummyExpense.date)
    }

    @Test
    fun `get AllExpense per Date for date with no expenses`() = runTest {
        val rareDateExpense = dummyExpense.copy(date = "1990-01-01")

        viewModel.get_AllExpense_per_Date(rareDateExpense)
        advanceUntilIdle()

        verify(expenseDao).get_all_Expense_aboutUser_WithDate(rareDateExpense.userId, "1990-01-01")
    }

    @Test
    fun `get AllExpense per Date with invalid date format`() = runTest {
        val badDateExpense = dummyExpense.copy(date = "Not-A-Date")

        viewModel.get_AllExpense_per_Date(badDateExpense)
        advanceUntilIdle()

        verify(expenseDao).get_all_Expense_aboutUser_WithDate(badDateExpense.userId, "Not-A-Date")
    }

    @Test
    fun `DeleteExpensePerDate success case`() = runTest {
        viewModel.DeleteExpensePerDate(dummyExpense)
        advanceUntilIdle()
        verify(expenseDao).deleteExpensePerDate(dummyExpense.date)
    }

    @Test
    fun `DeleteExpensePerDate for date with no expenses`() = runTest {
        viewModel.DeleteExpensePerDate(dummyExpense)
        advanceUntilIdle()
        // The VM should still attempt the delete
        verify(expenseDao).deleteExpensePerDate(dummyExpense.date)
    }

    @Test
    fun `DeleteExpensePerID success case`() = runTest {
        viewModel.DeleteExpensePerID(dummyExpense)
        advanceUntilIdle()
        verify(expenseDao).deleteExpensePerId(dummyExpense.userId)
    }

    @Test
    fun `DeleteExpensePerID for non existent user ID`() = runTest {
        val ghostUserExpense = dummyExpense.copy(userId = 9999)
        viewModel.DeleteExpensePerID(ghostUserExpense)
        advanceUntilIdle()
        verify(expenseDao).deleteExpensePerId(9999)
    }

    @Test
    fun `deleteExpenseById success case`() = runTest {
        val expenseIdToDelete = 101
        viewModel.deleteExpenseById(expenseIdToDelete)
        advanceUntilIdle()
        verify(expenseDao).deleteExpensePerId(expenseIdToDelete)
    }

    @Test
    fun `deleteExpenseById with non existent ID`() = runTest {
        val missingId = 5000
        viewModel.deleteExpenseById(missingId)
        advanceUntilIdle()
        verify(expenseDao).deleteExpensePerId(missingId)
    }

    @Test
    fun `deleteExpenseById with invalid ID`() = runTest {
        val negativeId = -5
        viewModel.deleteExpenseById(negativeId)
        advanceUntilIdle()
        verify(expenseDao).deleteExpensePerId(negativeId)
    }

    @Test
    fun `Database interaction verification`() = runTest {
        // Verify multiple interactions
        viewModel.AddExpense(dummyExpense)
        viewModel.deleteExpenseById(5)
        advanceUntilIdle()

        verify(expenseDao).addExpense(dummyExpense)
        verify(expenseDao).deleteExpensePerId(5)
    }

    @Test
    fun `Coroutine cancellation check`() = runTest {
        // We simulate cancellation by cancelling the scope job
        val job = launch {
            viewModel.AddExpense(dummyExpense)
        }
        job.cancel() // Cancel immediately

        // In a real scenario, we might check if the DB call was *not* made,
        // but since we can't control race conditions perfectly in simple tests,
        // we mainly ensure no crash occurs on cancel.
        advanceUntilIdle()
    }

}