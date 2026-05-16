package com.example.budgetplanner.ViewModell

import com.example.budgetplanner.ExpenseViewModel
import com.example.budgetplanner.MainDispatcherRule
import com.example.expensetrackingapp.Data.ExpenseDAO
import com.example.expensetrackingapp.Data.ExpenseEntity
import com.example.expensetrackingapp.Data.ExpenseUserDataBase
import com.example.expensetrackingapp.Data.UserDAO
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    // create dummy 
    private val dummyExpense = ExpenseEntity(expenseid = 10, userId = 1, amount = 100.0, date = "2024-01-01", category = "Food")

    @Before
    fun setup() {
        db = mock()
        expenseDao = mock()
        userDao = mock()

        whenever(db.getExpenseDao()).thenReturn(expenseDao)
        whenever(db.getUserDao()).thenReturn(userDao)

        viewModel = ExpenseViewModel(db)
    }

    @Test
    fun `AddExpense success case`() = runTest {
        viewModel.AddExpense(dummyExpense)
        advanceUntilIdle()

        verify(expenseDao).addExpense(dummyExpense)
        verify(userDao).getUserById(dummyExpense.userId)
    }

    @Test
    fun `AddExpense success`() = runTest {
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
        val emptyExpense = dummyExpense.copy(date = "", category = "")

        viewModel.AddExpense(emptyExpense)
        advanceUntilIdle()

        verify(expenseDao).addExpense(emptyExpense)
    }

    @Test
    fun `AddExpense concurrency`() = runTest {
        repeat(5) {
            launch { 
                viewModel.AddExpense(dummyExpense)
            }
        }
        advanceUntilIdle()

        verify(expenseDao, times(5)).addExpense(dummyExpense)
    }

    @Test
    fun `get AllExpense per UserID success case`() = runTest {
        viewModel.get_AllExpense_per_UserID(dummyExpense)
        advanceUntilIdle()
        verify(expenseDao).get_all_Expense_aboutUser(dummyExpense.userId)
    }

    @Test
    fun `get AllExpense per Date success case`() = runTest {
        viewModel.get_AllExpense_per_Date(dummyExpense)
        advanceUntilIdle()
        verify(expenseDao).get_all_Expense_aboutUser_WithDate(dummyExpense.userId, dummyExpense.date)
    }

    @Test
    fun `DeleteExpensePerDate success case`() = runTest {
        viewModel.DeleteExpensePerDate(dummyExpense)
        advanceUntilIdle()
        verify(expenseDao).deleteExpensePerDate(dummyExpense.date)
    }

    @Test
    fun `DeleteExpensePerID success case`() = runTest {
        viewModel.DeleteExpensePerID(dummyExpense)
        advanceUntilIdle()
        verify(expenseDao).deleteExpensePerId(dummyExpense.expenseid)
    }

    @Test
    fun `deleteExpenseById success case`() = runTest {
        val expenseIdToDelete = 101
        viewModel.deleteExpenseById(expenseIdToDelete)
        advanceUntilIdle()
        verify(expenseDao).deleteExpensePerId(expenseIdToDelete)
    }

    @Test
    fun `Database interaction verification`() = runTest {
        viewModel.AddExpense(dummyExpense)
        viewModel.deleteExpenseById(5)
        advanceUntilIdle()

        verify(expenseDao).addExpense(dummyExpense)
        verify(expenseDao).deleteExpensePerId(5)
    }

    @Test
    fun `Coroutine cancellation check`() = runTest {
        val job = launch {
            viewModel.AddExpense(dummyExpense)
        }
        job.cancel() 
        advanceUntilIdle()
    }
}
