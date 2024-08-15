package edu.merostate.assignment2

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TodoApiService {
    @POST("/api/users/register")
    suspend fun registerUser(
        @Query("apikey") apiKey: String,
        @Body request: CreateUserRequest
    ): User

    @POST("/api/users/login")
    suspend fun loginUser(
        @Query("apikey") apiKey: String,
        @Body request: LoginUserRequest
    ): User

    @GET("/api/users/{user_id}/todos")
    suspend fun getTodos(
        @Path("user_id") userId: String,
        @Header("Authorization") bearerToken: String,
        @Query("apikey") apiKey: String,
    ): List<TodoItem>

    @POST("/api/users/{user_id}/todos")
    suspend fun createTodo(
        @Path("user_id") userId: String,
        @Query("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Body request: CreateTodoRequest
    ): TodoItem

    @PUT("/api/users/{user_id}/todos/{id}")
    suspend fun editTodo(
        @Path("user_id") userId: String,
        @Path("id") todoId: Int,
        @Header("Authorization") bearerToken: String,
        @Query("apikey") apiKey: String,
        @Body request: UpdateTodoRequest
    ): TodoItem
}
