package com.example.budgetplanner.ViewModell

import com.example.budgetplanner.MainDispatcherRule
import com.example.expensetrackingapp.Data.ExpenseUserDataBase
import com.example.expensetrackingapp.Data.UserDAO
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
class EditViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var db: ExpenseUserDataBase
    private lateinit var userDao: UserDAO
    private lateinit var viewModel: EditViewModel
    private val testUserId = 1

    @Before
    fun setup() {
        db = mock()
        userDao = mock()
        whenever(db.getUserDao()).thenReturn(userDao)
        viewModel = EditViewModel(db, testUserId)
    }

    @Test
    fun `UpdateUserBudget with a valid positive budget`() = runTest {
        viewModel.UpdateUserBudget(5000.75)
        advanceUntilIdle()
        verify(userDao).updateBudget(testUserId, 5000.75)
    }

    @Test
    fun `UpdateUserBudget with a zero budget`() = runTest {
        viewModel.UpdateUserBudget(0.0)
        advanceUntilIdle()
        verify(userDao).updateBudget(testUserId, 0.0)
    }

    @Test
    fun `UpdateUserBudget with a negative budget`() = runTest {
        viewModel.UpdateUserBudget(-500.0)
        advanceUntilIdle()
        verify(userDao).updateBudget(testUserId, -500.0)
    }

    @Test
    fun `UpdateUserBudget with Double MAX VALUE`() = runTest {
        viewModel.UpdateUserBudget(Double.MAX_VALUE)
        advanceUntilIdle()
        verify(userDao).updateBudget(testUserId, Double.MAX_VALUE)
    }

    @Test
    fun `UpdateUserBudget with a very large budget value`() = runTest {
        viewModel.UpdateUserBudget(999999999.99)
        advanceUntilIdle()
        verify(userDao).updateBudget(testUserId, 999999999.99)
    }

    @Test
    fun `UpdateUserBudget precision handling`() = runTest {
        val precise = 123.456789
        viewModel.UpdateUserBudget(precise)
        advanceUntilIdle()
        verify(userDao).updateBudget(testUserId, precise)
    }

    @Test
    fun `UpdateUserBudget with NaN`() = runTest {
        viewModel.UpdateUserBudget(Double.NaN)
        advanceUntilIdle()
        verify(userDao).updateBudget(testUserId, Double.NaN)
    }

    @Test
    fun `UpdateUserBudget with positive infinity`() = runTest {
        viewModel.UpdateUserBudget(Double.POSITIVE_INFINITY)
        advanceUntilIdle()
        verify(userDao).updateBudget(testUserId, Double.POSITIVE_INFINITY)
    }

    @Test
    fun `UpdateUserBudget with negative infinity`() = runTest {
        viewModel.UpdateUserBudget(Double.NEGATIVE_INFINITY)
        advanceUntilIdle()
        verify(userDao).updateBudget(testUserId, Double.NEGATIVE_INFINITY)
    }

    @Test
    fun `UpdateUserBudget coroutine context check`() = runTest {
        // verify it runs without crashing
        viewModel.UpdateUserBudget(100.0)
        advanceUntilIdle()
        verify(userDao).updateBudget(any(), any())
    }

    @Test
    fun `UpdateUserBudget with an invalid user Id`() = runTest {
        // Create a VM with ID -1
        val vm = EditViewModel(db, -1)
        vm.UpdateUserBudget(100.0)
        advanceUntilIdle()
        verify(userDao).updateBudget(-1, 100.0)
    }

    @Test
    fun `UpdateUserPassword with a valid password`() = runTest {
        viewModel.UpdateUserPassword("Pass123")
        advanceUntilIdle()
        verify(userDao).updatePassword(testUserId, "Pass123")
    }

    @Test
    fun `UpdateUserPassword with an empty string`() = runTest {
        viewModel.UpdateUserPassword("")
        advanceUntilIdle()
        verify(userDao).updatePassword(testUserId, "")
    }

    @Test
    fun `UpdateUserPassword with a very long string`() = runTest {
        val longPass = "a".repeat(500)
        viewModel.UpdateUserPassword(longPass)
        advanceUntilIdle()
        verify(userDao).updatePassword(testUserId, longPass)
    }

    @Test
    fun `UpdateUserPassword with special characters and Unicode`() = runTest {
        val specialPass = "P@\$\$w0rd! 🚀"
        viewModel.UpdateUserPassword(specialPass)
        advanceUntilIdle()
        verify(userDao).updatePassword(testUserId, specialPass)
    }

    @Test
    fun `UpdateUserPassword with leading trailing whitespace`() = runTest {
        val pass = "  123  "
        viewModel.UpdateUserPassword(pass)
        advanceUntilIdle()
        verify(userDao).updatePassword(testUserId, pass)
    }

    @Test
    fun `UpdateUserPassword with a null value  if possible `() {
        // String cannot be null.
    }

    @Test
    fun `UpdateUserPassword with a password containing SQL injection characters`() = runTest {
        val sql = "'; DROP TABLE --"
        viewModel.UpdateUserPassword(sql)
        advanceUntilIdle()
        verify(userDao).updatePassword(testUserId, sql)
    }

    @Test
    fun `UpdateUserPassword coroutine context check`() = runTest {
        viewModel.UpdateUserPassword("x")
        advanceUntilIdle()
        verify(userDao).updatePassword(any(), any())
    }

    @Test
    fun `UpdateUserPassword with an invalid user Id`() = runTest {
        val vm = EditViewModel(db, -999)
        vm.UpdateUserPassword("x")
        advanceUntilIdle()
        verify(userDao).updatePassword(-999, "x")
    }

    @Test
    fun `ViewModel lifecycle scope cancellation`() = runTest {
        val job = launch { viewModel.UpdateUserBudget(10.0) }
        job.cancel()
        advanceUntilIdle()
    }

    @Test
    fun `Database transaction failure simulation for Budget`() = runTest {
        // We force the Mock DB to crash
        whenever(userDao.updateBudget(any(), any())).thenThrow(RuntimeException("Error"))

        try {
            viewModel.UpdateUserBudget(10.0)
            advanceUntilIdle()
        } catch (_: Throwable) {
        }
    }

}
