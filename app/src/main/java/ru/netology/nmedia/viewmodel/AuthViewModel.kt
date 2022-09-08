package ru.netology.nmedia.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.util.SingleLiveEvent

class AuthViewModel : ViewModel() {
    val data: LiveData<AuthState> = AppAuth.getInstance()
        .authStateFlow
        .asLiveData(Dispatchers.Default)

    val authenticated: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.id != 0L

    val state = SingleLiveEvent<Boolean>()

    fun authenticate(login: String, pass: String) = viewModelScope.launch {
        try {
            val authState =
                PostsApi.service.updateUser(login, pass).body() ?: throw ApiError(-1, "No user")
            AppAuth.getInstance().setAuth(authState.id, authState.token ?: "")
            state.value = true
        } catch (e: Exception) {
            state.value = false
        }
    }
}