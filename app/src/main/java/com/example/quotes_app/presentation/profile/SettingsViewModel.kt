package com.example.quotes_app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotes_app.data.local.SessionManager
import com.example.quotes_app.data.local.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userDao: UserDao
) : ViewModel() {

    private val _passwordState = MutableStateFlow<PasswordState>(PasswordState.Idle)
    val passwordState: StateFlow<PasswordState> = _passwordState.asStateFlow()

    fun changePassword(oldPass: String, newPass: String, confirmPass: String) {
        if (oldPass.isBlank() || newPass.isBlank() || confirmPass.isBlank()) {
            _passwordState.value = PasswordState.Error("Fields cannot be empty")
            return
        }
        if (newPass != confirmPass) {
            _passwordState.value = PasswordState.Error("New passwords do not match")
            return
        }

        viewModelScope.launch {
            _passwordState.value = PasswordState.Loading
            val username = sessionManager.getUsername()
            if (username != null) {
                val user = userDao.getUserByUsername(username)
                if (user != null) {
                    if (user.passwordHash == oldPass) {
                        val updatedUser = user.copy(passwordHash = newPass)
                        userDao.updateUser(updatedUser)
                        _passwordState.value = PasswordState.Success
                    } else {
                        _passwordState.value = PasswordState.Error("Incorrect old password")
                    }
                } else {
                    _passwordState.value = PasswordState.Error("User not found")
                }
            } else {
                _passwordState.value = PasswordState.Error("Not logged in")
            }
        }
    }

    fun resetState() {
        _passwordState.value = PasswordState.Idle
    }
}

sealed class PasswordState {
    object Idle : PasswordState()
    object Loading : PasswordState()
    object Success : PasswordState()
    data class Error(val message: String) : PasswordState()
}
