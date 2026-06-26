package com.example.quotes_app.presentation.profile

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
class EditProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userDao: UserDao
) : ViewModel() {

    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val username = sessionManager.getUsername()
            if (username != null) {
                val user = userDao.getUserByUsername(username)
                _userState.value = user
            }
        }
    }

    fun updateName(newName: String) {
        if (newName.isBlank()) {
            _updateState.value = UpdateState.Error("Name cannot be empty")
            return
        }

        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            val currentUser = _userState.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(name = newName)
                userDao.updateUser(updatedUser)
                _userState.value = updatedUser
                _updateState.value = UpdateState.Success
            } else {
                _updateState.value = UpdateState.Error("User not found")
            }
        }
    }

    fun resetState() {
        _updateState.value = UpdateState.Idle
    }
}

sealed class UpdateState {
    object Idle : UpdateState()
    object Loading : UpdateState()
    object Success : UpdateState()
    data class Error(val message: String) : UpdateState()
}
