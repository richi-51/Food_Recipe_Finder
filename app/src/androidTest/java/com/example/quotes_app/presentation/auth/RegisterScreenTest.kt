package com.example.quotes_app.presentation.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun registerScreen_shouldDisplayAllComponents() {
        val fakeSessionManager = FakeTestSessionManager()
        val fakeUserDao = FakeTestUserDao()
        val viewModel = AuthViewModel(fakeSessionManager, fakeUserDao)

        composeTestRule.setContent {
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = {},
                onNavigateBack = {}
            )
        }

        composeTestRule.onNodeWithText("Create Account").assertExists()
        composeTestRule.onNodeWithTag("registerUsernameField").assertExists()
        composeTestRule.onNodeWithTag("registerNameField").assertExists()
        composeTestRule.onNodeWithTag("registerPasswordField").assertExists()
        composeTestRule.onNodeWithTag("registerConfirmPasswordField").assertExists()
        composeTestRule.onNodeWithTag("registerButton").assertExists()
    }

    @Test
    fun registerScreen_passwordMismatch_showsErrorMessage() {
        val fakeSessionManager = FakeTestSessionManager()
        val fakeUserDao = FakeTestUserDao()
        val viewModel = AuthViewModel(fakeSessionManager, fakeUserDao)

        composeTestRule.setContent {
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = {},
                onNavigateBack = {}
            )
        }

        composeTestRule.onNodeWithTag("registerUsernameField").performTextInput("newuser")
        composeTestRule.onNodeWithTag("registerNameField").performTextInput("New User")
        composeTestRule.onNodeWithTag("registerPasswordField").performTextInput("pass123")
        composeTestRule.onNodeWithTag("registerConfirmPasswordField").performTextInput("pass999")
        composeTestRule.onNodeWithTag("registerButton").performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            viewModel.authState.value is AuthState.Error
        }

        composeTestRule.onNodeWithTag("registerErrorMessage").assertExists()
        composeTestRule.onNodeWithText("Passwords do not match").assertExists()
    }
}
