package com.example.quotes_app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotes_app.data.local.SessionManager
import com.example.quotes_app.data.local.UserDao
import com.example.quotes_app.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userDao: UserDao
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(username: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            kotlinx.coroutines.delay(1000)
            
            if (username.isBlank() || pass.isBlank()) {
                _authState.value = AuthState.Error("Username and password cannot be empty")
                return@launch
            }
            
            val user = userDao.getUserByUsername(username)
            if (user != null && user.passwordHash == pass) {
                sessionManager.login(username)
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Invalid username or password")
            }
        }
    }

    fun register(username: String, name: String, pass: String, confirmPass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            kotlinx.coroutines.delay(1000)
            
            if (username.isBlank() || name.isBlank() || pass.isBlank()) {
                _authState.value = AuthState.Error("All fields must be filled")
                return@launch
            }
            if (pass != confirmPass) {
                _authState.value = AuthState.Error("Passwords do not match")
                return@launch
            }
            
            val existingUser = userDao.getUserByUsername(username)
            if (existingUser != null) {
                _authState.value = AuthState.Error("Username already exists")
                return@launch
            }
            
            val newUser = User(username = username, name = name, passwordHash = pass)
            userDao.insertUser(newUser)
            _authState.value = AuthState.Success
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
