package com.example.quotes_app.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_shouldDisplayAllComponents() {
        val fakeSessionManager = FakeTestSessionManager()
        val fakeUserDao = FakeTestUserDao()
        val viewModel = AuthViewModel(fakeSessionManager, fakeUserDao)

        composeTestRule.setContent {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {},
                onNavigateToRegister = {}
            )
        }

        composeTestRule.onNodeWithText("Welcome Back").assertExists()
        composeTestRule.onNodeWithTag("usernameField").assertExists()
        composeTestRule.onNodeWithTag("passwordField").assertExists()
        composeTestRule.onNodeWithTag("loginButton").assertExists()
    }

    @Test
    fun usernameField_shouldAcceptInput() {
        val fakeSessionManager = FakeTestSessionManager()
        val fakeUserDao = FakeTestUserDao()
        val viewModel = AuthViewModel(fakeSessionManager, fakeUserDao)

        composeTestRule.setContent {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {},
                onNavigateToRegister = {}
            )
        }

        composeTestRule.onNodeWithTag("usernameField").performTextInput("admin")
        composeTestRule.onNodeWithText("admin").assertExists()
    }

    @Test
    fun loginButton_shouldBeClickable() {
        var isClicked = false
        val fakeSessionManager = FakeTestSessionManager()
        val fakeUserDao = FakeTestUserDao()
        val viewModel = AuthViewModel(fakeSessionManager, fakeUserDao)

        composeTestRule.setContent {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { isClicked = true },
                onNavigateToRegister = {}
            )
        }

        composeTestRule.onNodeWithTag("loginButton").performClick()
    }

    @Test
    fun loginScreen_fullFlowFailed_shouldDisplayErrorMessage() {
        val fakeSessionManager = FakeTestSessionManager()
        val fakeUserDao = FakeTestUserDao()
        val viewModel = AuthViewModel(fakeSessionManager, fakeUserDao)

        composeTestRule.setContent {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {},
                onNavigateToRegister = {}
            )
        }

        composeTestRule.onNodeWithTag("usernameField").performTextInput("wronguser")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("wrongpass")
        composeTestRule.onNodeWithTag("loginButton").performClick()

        // Waiting for delay inside AuthViewModel
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            viewModel.authState.value is AuthState.Error
        }

        composeTestRule.onNodeWithTag("errorMessage").assertExists()
        composeTestRule.onNodeWithText("Invalid username or password").assertExists()
    }

    @Test
    fun loginScreen_fullFlowSuccess_shouldCallLoginSuccess() {
        var successCalled = false
        val fakeSessionManager = FakeTestSessionManager()
        val fakeUserDao = FakeTestUserDao()
        fakeUserDao.insertTestUser("admin", "123456")

        val viewModel = AuthViewModel(fakeSessionManager, fakeUserDao)

        composeTestRule.setContent {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { successCalled = true },
                onNavigateToRegister = {}
            )
        }

        composeTestRule.onNodeWithTag("usernameField").performTextInput("admin")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("123456")
        composeTestRule.onNodeWithTag("loginButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            successCalled
        }

        assertTrue(successCalled)
    }
}

class FakeTestSessionManager : com.example.quotes_app.data.local.SessionManager(null) {
    private var currentUser: String? = null
    private var loggedIn = false

    override fun login(username: String) {
        currentUser = username
        loggedIn = true
    }

    override fun logout() {
        currentUser = null
        loggedIn = false
    }

    override fun isLoggedIn(): Boolean = loggedIn
    override fun getUsername(): String? = currentUser
}

class FakeTestUserDao : com.example.quotes_app.data.local.UserDao {
    private val users = mutableMapOf<String, com.example.quotes_app.domain.User>()

    fun insertTestUser(username: String, pass: String) {
        users[username] = com.example.quotes_app.domain.User(username, "Test User", pass)
    }

    override suspend fun insertUser(user: com.example.quotes_app.domain.User) {
        users[user.username] = user
    }

    override suspend fun getUserByUsername(username: String): com.example.quotes_app.domain.User? {
        return users[username]
    }

    override suspend fun updateUser(user: com.example.quotes_app.domain.User) {
        users[user.username] = user
    }
}
