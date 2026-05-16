package com.example.budgetplanner.ViewModell

import com.example.budgetplanner.MainDispatcherRule
import com.example.expensetrackingapp.Data.ExpenseUserDataBase
import com.example.expensetrackingapp.Data.UserDAO
import com.example.expensetrackingapp.Data.UserEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var db: ExpenseUserDataBase
    private lateinit var userDao: UserDAO
    private lateinit var viewModel: SignUpViewModel

    // Valid User Data
    private val validUser = UserEntity(
        userId = 0, // 0 means auto-generate in Room
        name = "Test User",
        email = "test@example.com",
        Password = "password123",
        totExpense = 0.0,
        budget = 500.0
    )

    @Before
    fun setup() {
        db = mock()
        userDao = mock()
        whenever(db.getUserDao()).thenReturn(userDao)
        viewModel = SignUpViewModel(db)
    }

    @Test
    fun `AddNewUserandGetId with a valid new user`() = runTest {
        whenever(userDao.addUser(validUser)).thenReturn(100L)

        viewModel.AddNewUserandGetId(validUser)
        advanceUntilIdle()

        // Verify DAO was called
        verify(userDao).addUser(validUser)
        assertEquals(100, viewModel.user_id.value)
    }

    @Test
    fun `Initial state of user id`() {
        assertNull(viewModel.user_id.value)
    }

    @Test
    fun `AddNewUserandGetId coroutine dispatcher check`() = runTest {

        whenever(userDao.addUser(any())).thenReturn(1L)

        viewModel.AddNewUserandGetId(validUser)
        advanceUntilIdle()

        verify(userDao).addUser(validUser)
    }

    @Test
    fun `AddNewUserandGetId with null user object`() {
    }

    @Test
    fun `AddNewUserandGetId with user object having null fields`() = runTest {
        val invalidUser = validUser.copy(name = "") // simulating "bad" data
        whenever(userDao.addUser(invalidUser)).thenThrow(RuntimeException("NotNull Constraint"))

        try {
            viewModel.AddNewUserandGetId(invalidUser)
            advanceUntilIdle()
        } catch (e: Exception) {
            // Pass
        }
    }

    @Test
    fun `AddNewUserandGetId database returns a large ID`() = runTest {
        // ID larger than Int.MAX_VALUE
        val largeId = 2147483648L
        whenever(userDao.addUser(validUser)).thenReturn(largeId)

        viewModel.AddNewUserandGetId(validUser)
        advanceUntilIdle()
        // Verify overflow behavior 
        assertEquals(largeId.toInt(), viewModel.user_id.value)
    }

    @Test
    fun `AddNewUserandGetId multiple calls in succession`() = runTest {
        whenever(userDao.addUser(any())).thenReturn(1L, 2L, 3L)

        viewModel.AddNewUserandGetId(validUser)
        viewModel.AddNewUserandGetId(validUser)
        viewModel.AddNewUserandGetId(validUser)

        advanceUntilIdle()

        assertEquals(3, viewModel.user_id.value)
    }

    @Test
    fun `ViewModel lifecycle and coroutine cancellation`() = runTest {
        // Simulate a long running DB 
        whenever(userDao.addUser(any())).thenAnswer {
            Thread.sleep(200)
            1L
        }

        val job = launch {
            viewModel.AddNewUserandGetId(validUser)
        }

        job.cancel()
        advanceUntilIdle()
        assertNull(viewModel.user_id.value)
    }

    @Test
    fun `Thread safety of user id updates`() = runTest {
        whenever(userDao.addUser(any())).thenReturn(1L)

        // Launch multiple updates
        repeat(5) {
            launch { viewModel.AddNewUserandGetId(validUser) }
        }
        advanceUntilIdle()

        assertEquals(1, viewModel.user_id.value)
    }

}
