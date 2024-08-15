package edu.merostate.assignment2

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.metrostate.assignment2.ApiClient
import kotlinx.coroutines.launch

class TodoListViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiClient.apiService
    private val _todos = MutableLiveData<List<TodoItem>>()
    val todos: LiveData<List<TodoItem>> get() = _todos
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        fetchTodos()
    }

    fun fetchTodos() {
        val userId = SharedPreferencesManager.getUserId(getApplication())
        val token = SharedPreferencesManager.getUserToken(getApplication())

        if (userId == null || token == null) {
            Log.e("TodoListViewModel", "User ID or token is null. Cannot fetch todos.")
            _error.postValue("User ID or token is null. Cannot fetch todos.")
            return
        }

        viewModelScope.launch {
            try {
                Log.d("TodoListViewModel", "Fetching todos for user: $userId with token: $token")
                val todosList = apiService.getTodos(
                    userId = userId,
                    bearerToken = "Bearer $token",
                    apiKey = "52a25973-7aef-478f-a43a-2a1de8d62c26",
                )
                Log.d("TodoListViewModel", "Fetched todos: $todosList")
                _todos.postValue(todosList)
            } catch (e: Exception) {
                Log.e("TodoListViewModel", "Error fetching todos: ${e.message}")
                _error.postValue("Failed to fetch todos")
            }
        }
    }

    fun createTodo(description: String) {
        val userId = SharedPreferencesManager.getUserId(getApplication())
        val token = SharedPreferencesManager.getUserToken(getApplication())

        Log.d("TodoListViewModel", "User ID: $userId, Token: $token")

        if (userId == null || token == null) {
            Log.e("TodoListViewModel", "User ID or token is null. Cannot create todo.")
            _error.postValue("User ID or token is null. Cannot create todo.")
            return
        }

        viewModelScope.launch {
            try {
                Log.d("TodoListViewModel", "Creating todo for user: $userId with description: $description")
                val createTodoRequest = CreateTodoRequest(description)
                val newTodo = apiService.createTodo(userId, "52a25973-7aef-478f-a43a-2a1de8d62c26", "Bearer $token", createTodoRequest)
                Log.d("TodoListViewModel", "Created new todo: $newTodo")
                fetchTodos() // Refetch todos after creating a new one
            } catch (e: retrofit2.HttpException) {
                Log.e("TodoListViewModel", "HTTP error creating todo: ${e.code()} ${e.message()}")
                _error.postValue("HTTP error creating todo: ${e.message()}")
            } catch (e: Exception) {
                Log.e("TodoListViewModel", "Error creating todo: ${e.message}")
                _error.postValue("Failed to create todo: ${e.message}")
            }
        }
    }

    fun completeTodo(todoId: Int, description: String, completed: Boolean) {
        updateTodo(todoId, description, completed)
    }

    fun updateTodo(todoId: Int, description: String, completed: Boolean) {
        val userId = SharedPreferencesManager.getUserId(getApplication())
        val token = SharedPreferencesManager.getUserToken(getApplication())

        if (userId == null || token == null) {
            Log.e("TodoListViewModel", "User ID or token is null. Cannot update todo.")
            _error.postValue("User ID or token is null. Cannot update todo.")
            return
        }

        viewModelScope.launch {
            try {
                val updateTodoRequest = UpdateTodoRequest(description, completed)
                Log.d("TodoListViewModel", "Updating todo with request: $updateTodoRequest")
                val updatedTodo = apiService.editTodo(
                    userId = userId,
                    todoId = todoId,
                    bearerToken = "Bearer $token",
                    apiKey = "52a25973-7aef-478f-a43a-2a1de8d62c26",
                    request = updateTodoRequest
                )
                Log.d("TodoListViewModel", "Updated todo response: $updatedTodo")
                // Update the local list of todos with the updated todo item
                _todos.postValue(_todos.value?.map {
                    if (it.id == todoId) updatedTodo else it
                })
            } catch (e: retrofit2.HttpException) {
                Log.e("TodoListViewModel", "HTTP error updating todo: ${e.code()} ${e.message()}")
                _error.postValue("HTTP error updating todo: ${e.message()}")
            } catch (e: Exception) {
                Log.e("TodoListViewModel", "Error updating todo: ${e.message}")
                _error.postValue("Failed to update todo: ${e.message}")
            }
        }
    }

    fun clearError() {
        _error.postValue(null)
    }
}
