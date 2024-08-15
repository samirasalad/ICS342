package edu.merostate.assignment2

data class CreateUserRequest(
    val email: String,
    val name: String,
    val password: String
)

data class LoginUserRequest(
    val email: String,
    val password: String
)

data class User(
    val id: String,
    val token: String
)

data class CreateTodoRequest(
    val description: String,
    val completed: Boolean = false
)

data class UpdateTodoRequest(
    val description: String,
    val completed: Boolean
)

data class TodoItem(
    val id: Int,
    val description: String,
    var isCompleted: Boolean = false
)
