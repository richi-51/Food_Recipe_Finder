package com.example.quotes_app.presentation.profile

import com.example.quotes_app.data.local.SessionManager
import com.example.quotes_app.data.local.UserDao
import com.example.quotes_app.domain.User
import com.example.quotes_app.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadUserProfile_whenUserIsLoggedIn_loadsUserFromDao() = runTest {
        val fakeSession = FakeProfileSessionManager()
        val fakeDao = FakeProfileUserDao()

        fakeDao.insertUser(User("john", "John Doe", "secret"))
        fakeSession.login("john")

        val viewModel = ProfileViewModel(fakeSession, fakeDao)

        val user = viewModel.userState.value
        assertNotNull(user)
        assertEquals("John Doe", user?.name)
    }

    @Test
    fun loadUserProfile_whenUserNotLoggedIn_userStateIsNull() = runTest {
        val fakeSession = FakeProfileSessionManager()
        val fakeDao = FakeProfileUserDao()

        val viewModel = ProfileViewModel(fakeSession, fakeDao)

        val user = viewModel.userState.value
        assertNull(user)
    }

    @Test
    fun logout_clearsUserSession() = runTest {
        val fakeSession = FakeProfileSessionManager()
        val fakeDao = FakeProfileUserDao()
        fakeSession.login("john")

        val viewModel = ProfileViewModel(fakeSession, fakeDao)
        viewModel.logout()

        assertFalse(fakeSession.isLoggedIn())
        assertNull(fakeSession.getUsername())
    }
}

class FakeProfileSessionManager : SessionManager(null) {
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

class FakeProfileUserDao : UserDao {
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
