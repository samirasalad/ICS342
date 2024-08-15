package edu.merostate.assignment2

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.merostate.assignment2.CreateUserRequest
import edu.merostate.assignment2.SharedPreferencesManager
import edu.merostate.assignment2.User
import edu.metrostate.assignment2.ApiClient
import kotlinx.coroutines.launch

class CreateAccountViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = ApiClient.apiService

    fun createAccount(email: String, name: String, password: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            try {
                val request = CreateUserRequest(email, name, password)
                Log.d("CreateAccount", "Request: $request")
                val response = apiService.registerUser("52a25973-7aef-478f-a43a-2a1de8d62c26", request)
                Log.d("CreateAccount", "Response: $response")
                val let = response?.let { user ->
                    SharedPreferencesManager.saveUserId(getApplication(), user.id)
                    SharedPreferencesManager.saveUserToken(getApplication(), user.token)
                }
                onResult(response)
            } catch (e: retrofit2.HttpException) {
                Log.e("CreateAccount", "HTTP Error: ${e.code()} ${e.message()}")
                onResult(null)
            } catch (e: Exception) {
                Log.e("CreateAccount", "Error: ${e.message}")
                onResult(null)
            }
        }
    }
}
