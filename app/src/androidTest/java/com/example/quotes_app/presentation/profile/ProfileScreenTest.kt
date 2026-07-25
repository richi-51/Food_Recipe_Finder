package com.example.quotes_app.presentation.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quotes_app.data.local.SessionManager
import com.example.quotes_app.data.local.UserDao
import com.example.quotes_app.domain.User
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun profileScreen_shouldDisplayUserInfoAndLogoutButton() {
        val fakeSession = FakeProfileTestSession()
        val fakeDao = FakeProfileTestUserDao()
        val viewModel = ProfileViewModel(fakeSession, fakeDao)

        composeTestRule.setContent {
            ProfileScreen(
                viewModel = viewModel,
                onNavigateToEditProfile = {},
                onNavigateToSettings = {},
                onLogout = {}
            )
        }

        composeTestRule.onNodeWithText("Profile").assertExists()
        composeTestRule.onNodeWithTag("logoutButton").assertExists()
    }

    @Test
    fun profileScreen_logoutButton_triggersCallback() {
        var logoutClicked = false
        val fakeSession = FakeProfileTestSession()
        val fakeDao = FakeProfileTestUserDao()
        val viewModel = ProfileViewModel(fakeSession, fakeDao)

        composeTestRule.setContent {
            ProfileScreen(
                viewModel = viewModel,
                onNavigateToEditProfile = {},
                onNavigateToSettings = {},
                onLogout = { logoutClicked = true }
            )
        }

        composeTestRule.onNodeWithTag("logoutButton").performClick()
        assertTrue(logoutClicked)
    }
}

class FakeProfileTestSession : SessionManager(null) {
    override fun isLoggedIn(): Boolean = true
    override fun getUsername(): String = "testuser"
}

class FakeProfileTestUserDao : UserDao {
    override suspend fun insertUser(user: User) {}
    override suspend fun getUserByUsername(username: String): User = User("testuser", "Test User", "123")
    override suspend fun updateUser(user: User) {}
}
