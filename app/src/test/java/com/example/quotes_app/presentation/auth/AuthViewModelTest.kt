package com.example.quotes_app.presentation.auth

import com.example.quotes_app.data.local.SessionManager
import com.example.quotes_app.data.local.UserDao
import com.example.quotes_app.domain.User
import com.example.quotes_app.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeSessionManager: FakeSessionManager
    private lateinit var fakeUserDao: FakeUserDao
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        fakeSessionManager = FakeSessionManager()
        fakeUserDao = FakeUserDao()
        viewModel = AuthViewModel(fakeSessionManager, fakeUserDao)
    }

    @Test
    fun login_withEmptyUsername_shouldShowError() = runTest {
        viewModel.login("", "123456")
        testScheduler.advanceUntilIdle()
        val state = viewModel.authState.value
        assertTrue(state is AuthState.Error)
        assertEquals("Username and password cannot be empty", (state as AuthState.Error).message)
    }

    @Test
    fun login_withEmptyPassword_shouldShowError() = runTest {
        viewModel.login("admin", "")
        testScheduler.advanceUntilIdle()
        val state = viewModel.authState.value
        assertTrue(state is AuthState.Error)
        assertEquals("Username and password cannot be empty", (state as AuthState.Error).message)
    }

    @Test
    fun login_withInvalidCredentials_shouldShowError() = runTest {
        viewModel.login("nonexistent", "wrongpass")
        testScheduler.advanceUntilIdle()
        val state = viewModel.authState.value
        assertTrue(state is AuthState.Error)
        assertEquals("Invalid username or password", (state as AuthState.Error).message)
    }

    @Test
    fun login_withValidCredentials_shouldShowSuccess() = runTest {
        val user = User("admin", "Administrator", "123456")
        fakeUserDao.insertUser(user)

        viewModel.login("admin", "123456")
        testScheduler.advanceUntilIdle()
        val state = viewModel.authState.value

        assertTrue(state is AuthState.Success)
        assertTrue(fakeSessionManager.isLoggedIn())
        assertEquals("admin", fakeSessionManager.getUsername())
    }

    @Test
    fun register_withEmptyFields_shouldShowError() = runTest {
        viewModel.register("", "Full Name", "pass", "pass")
        testScheduler.advanceUntilIdle()
        val state = viewModel.authState.value
        assertTrue(state is AuthState.Error)
        assertEquals("All fields must be filled", (state as AuthState.Error).message)
    }

    @Test
    fun register_withPasswordMismatch_shouldShowError() = runTest {
        viewModel.register("john", "John Doe", "pass123", "pass456")
        testScheduler.advanceUntilIdle()
        val state = viewModel.authState.value
        assertTrue(state is AuthState.Error)
        assertEquals("Passwords do not match", (state as AuthState.Error).message)
    }

    @Test
    fun register_withExistingUser_shouldShowError() = runTest {
        fakeUserDao.insertUser(User("existing", "Existing User", "123"))

        viewModel.register("existing", "New User", "123", "123")
        testScheduler.advanceUntilIdle()
        val state = viewModel.authState.value
        assertTrue(state is AuthState.Error)
        assertEquals("Username already exists", (state as AuthState.Error).message)
    }

    @Test
    fun register_withValidCredentials_shouldInsertUserAndShowSuccess() = runTest {
        viewModel.register("newuser", "New User", "password123", "password123")
        testScheduler.advanceUntilIdle()
        val state = viewModel.authState.value

        assertTrue(state is AuthState.Success)
        val createdUser = fakeUserDao.getUserByUsername("newuser")
        assertEquals("New User", createdUser?.name)
    }

    @Test
    fun resetState_shouldResetToIdle() = runTest {
        viewModel.login("", "")
        testScheduler.advanceUntilIdle()
        viewModel.resetState()
        assertEquals(AuthState.Idle, viewModel.authState.value)
    }
}

class FakeSessionManager : SessionManager(null) {
    private var loggedInUser: String? = null
    private var loggedIn = false

    override fun login(username: String) {
        loggedInUser = username
        loggedIn = true
    }

    override fun logout() {
        loggedInUser = null
        loggedIn = false
    }

    override fun isLoggedIn(): Boolean = loggedIn
    override fun getUsername(): String? = loggedInUser
}

class FakeUserDao : UserDao {
    private val users = mutableMapOf<String, User>()

    override suspend fun insertUser(user: User) {
        users[user.username] = user
    }

    override suspend fun getUserByUsername(username: String): User? {
        return users[username]
    }

    override suspend fun updateUser(user: User) {
        users[user.username] = user
    }
}
