package edu.merostate.assignment2

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.metrostate.assignment2.ApiClient
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = ApiClient.apiService

    fun loginUser(email: String, password: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            try {
                val user = apiService.loginUser("52a25973-7aef-478f-a43a-2a1de8d62c26", LoginUserRequest(email, password))
                user?.let {
                    Log.d("LoginViewModel", "Saving User ID: ${it.id}, Token: ${it.token}")
                    SharedPreferencesManager.saveUserId(getApplication(), it.id)
                    SharedPreferencesManager.saveUserToken(getApplication(), it.token)
                }
                onResult(user)
            } catch (e: retrofit2.HttpException) {
                Log.e("Login", "HTTP Error: ${e.code()} ${e.message()}")
                onResult(null)
            } catch (e: Exception) {
                Log.e("Login", "Error logging in: ${e.message}")
                onResult(null)
            }
        }
    }
}
