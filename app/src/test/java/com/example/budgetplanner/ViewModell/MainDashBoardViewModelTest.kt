package com.example.budgetplanner.ViewModell

import com.example.budgetplanner.MainDispatcherRule
import com.example.expensetrackingapp.Data.ExpenseDAO
import com.example.expensetrackingapp.Data.ExpenseEntity
import com.example.expensetrackingapp.Data.ExpenseUserDataBase
import com.example.expensetrackingapp.Data.UserDAO
import com.example.expensetrackingapp.Data.UserEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class MainDashBoardViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var db: ExpenseUserDataBase
    private lateinit var userDao: UserDAO
    private lateinit var expenseDao: ExpenseDAO
    private lateinit var viewModel: MainDashBoardViewModel

    private val testUserId = 1

    // Dummy Data
    private val dummyUser = UserEntity(testUserId, "Test", "email", "pass", 1000.0, 0.0)
    private val dummyExpense = ExpenseEntity(
        userId = testUserId,
        amount = 100.0,
        date = "2024-01-01",
        category = "Food"
    )

    @Before
    fun setup() {
        db = mock()
        userDao = mock()
        expenseDao = mock()

        whenever(db.getUserDao()).thenReturn(userDao)
        whenever(db.getExpenseDao()).thenReturn(expenseDao)
    }

    @Test
    fun `initial state verification`() = runTest {
        whenever(userDao.getUserById(testUserId)).thenReturn(null)
        whenever(expenseDao.get_all_Expense_aboutUser(testUserId)).thenReturn(emptyList())

        viewModel = MainDashBoardViewModel(db, testUserId)
        advanceUntilIdle()
        
        assertEquals(emptyList<ExpenseEntity>(), viewModel.userexpanses.value)
        assertEquals(0.0, viewModel.BalancePrice.value, 0.0)
    }

    @Test
    fun `load userinfo success`() = runTest {
        whenever(userDao.getUserById(testUserId)).thenReturn(dummyUser)
        whenever(expenseDao.get_all_Expense_aboutUser(testUserId)).thenReturn(emptyList())

        viewModel = MainDashBoardViewModel(db, testUserId)
        advanceUntilIdle()

        assertEquals(dummyUser.userId, viewModel.userinfo.value?.userId)
    }

    @Test
    fun `load userExpances triggers data update`() = runTest {
        whenever(userDao.getUserById(testUserId)).thenReturn(dummyUser)
        whenever(expenseDao.get_all_Expense_aboutUser(testUserId)).thenReturn(listOf(dummyExpense))

        viewModel = MainDashBoardViewModel(db, testUserId)
        advanceUntilIdle()

        verify(userDao).updateTotExpense(testUserId, 100.0)
    }

    @Test
    fun `CalcBalancePrice with zero expenses`() = runTest {
        // Return dummyUser for all calls to getUserById
        whenever(userDao.getUserById(testUserId)).thenReturn(dummyUser)
        whenever(expenseDao.get_all_Expense_aboutUser(testUserId)).thenReturn(emptyList())

        viewModel = MainDashBoardViewModel(db, testUserId)
        advanceUntilIdle()

        assertEquals(1000.0, viewModel.BalancePrice.value, 0.01)
    }

    @Test
    fun `deleteExpenseById success`() = runTest {
        whenever(userDao.getUserById(testUserId)).thenReturn(dummyUser)
        whenever(expenseDao.get_all_Expense_aboutUser(testUserId)).thenReturn(emptyList())
        
        viewModel = MainDashBoardViewModel(db, testUserId)
        advanceUntilIdle()

        viewModel.deleteExpenseById(1)
        advanceUntilIdle()

        verify(expenseDao).deleteExpensePerId(1)
        // verify it reloads
        verify(expenseDao, atLeast(2)).get_all_Expense_aboutUser(testUserId)
    }
}
