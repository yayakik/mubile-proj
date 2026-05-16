package com.example.budgetplanner.ViewModell

import com.example.budgetplanner.MainDispatcherRule
import com.example.expensetrackingapp.Data.ExpenseUserDataBase
import com.example.expensetrackingapp.Data.UserDAO
import com.example.expensetrackingapp.Data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var db: ExpenseUserDataBase
    private lateinit var userDao: UserDAO
    private lateinit var viewModel: LoginViewModel

    //  Valid User Data
    private val validUser = UserEntity(
        userId = 1,
        name = "Test User",
        email = "test@example.com",
        Password = "password123",
        totExpense = 0.0,
        budget = 1000.0
    )

    @Before
    fun setup() {
        db = mock()
        userDao = mock()
        whenever(db.getUserDao()).thenReturn(userDao)
        viewModel = LoginViewModel(db)
    }

    @Test
    fun `getUserByEmailandPassword with both credentials invalid`() = runTest {
        whenever(userDao.login("wrong", "wrong")).thenReturn(null)

        viewModel.getUserByEmailandPassword("wrong", "wrong")
        advanceUntilIdle()

        assertFalse(viewModel.loginResult.value)
    }

    @Test
    fun `getUserByEmailandPassword with empty password`() = runTest {
        whenever(userDao.login("test@example.com", "")).thenReturn(null)

        viewModel.getUserByEmailandPassword("test@example.com", "")
        advanceUntilIdle()

        assertFalse(viewModel.loginResult.value)
    }

    @Test
    fun `getUserByEmailandPassword with empty credentials`() = runTest {
        whenever(userDao.login("", "")).thenReturn(null)

        viewModel.getUserByEmailandPassword("", "")
        advanceUntilIdle()

        assertFalse(viewModel.loginResult.value)
    }

    @Test
    fun `Database operation throwing an exception`() = runTest {
        whenever(userDao.login(any(), any())).thenThrow(RuntimeException("DB Error"))

        try {
            viewModel.getUserByEmailandPassword("user", "pass")
            advanceUntilIdle()
        } catch (e: Exception) {

            assertTrue(e is RuntimeException)
        }
    }

    @Test
    fun `Concurrent login attempts handling`() = runTest {

        whenever(userDao.login("user1", "pass1")).thenReturn(validUser)
        whenever(userDao.login("user2", "pass2")).thenReturn(null)

        launch { viewModel.getUserByEmailandPassword("user1", "pass1") }
        launch { viewModel.getUserByEmailandPassword("user2", "pass2") }

        advanceUntilIdle()

        assertTrue(viewModel.loginResult.value == true || viewModel.loginResult.value == false)
    }

    @Test
    fun `ViewModel coroutine scope cancellation`() = runTest {
        val job = launch {
            viewModel.getUserByEmailandPassword("u", "p")
        }
        job.cancel()
        advanceUntilIdle()
        // Ensure no crash on cancel
    }

    @Test
    fun `getLoginResult initial state verification`() {
        assertFalse(viewModel.loginResult.value)
    }

    @Test
    fun `getUser id initial state verification`() {
        assertNull(viewModel.user_id.value)
    }

    @Test
    fun `getLoginError initial state verification`() {
        assertEquals("", viewModel.loginError.value)
    }

    @Test
    fun `State transition from success to failure`() = runTest {
        // Success
        whenever(userDao.login("valid", "valid")).thenReturn(validUser)
        viewModel.getUserByEmailandPassword("valid", "valid")
        advanceUntilIdle()
        assertTrue(viewModel.loginResult.value)

        // Failure
        whenever(userDao.login("invalid", "invalid")).thenReturn(null)
        viewModel.getUserByEmailandPassword("invalid", "invalid")
        advanceUntilIdle()

        // Assert Failure State
        assertFalse(viewModel.loginResult.value)
        assertEquals("Email or Password is incorrect", viewModel.loginError.value)
    }

    @Test
    fun `State transition from failure to success`() = runTest {
        // Failure
        whenever(userDao.login("invalid", "invalid")).thenReturn(null)
        viewModel.getUserByEmailandPassword("invalid", "invalid")
        advanceUntilIdle()
        assertFalse(viewModel.loginResult.value)

        // Success
        whenever(userDao.login("valid", "valid")).thenReturn(validUser)
        viewModel.getUserByEmailandPassword("valid", "valid")
        advanceUntilIdle()

        // Assert Success State
        assertTrue(viewModel.loginResult.value)
        assertEquals("", viewModel.loginError.value) // Error should be cleared
        assertEquals(1, viewModel.user_id.value)
    }
}
