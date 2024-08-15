package edu.metrostate.assignment2

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import edu.merostate.assignment2.TodoApiService
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {

    private const val BASE_URL = "https://todos.simpleapi.dev"
    private const val API_KEY = "52a25973-7aef-478f-a43a-2a1de8d62c26"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original: Request = chain.request()
            val request: Request = original.newBuilder()
                .header("apikey", API_KEY)
                .method(original.method(), original.body())
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .build()

    val apiService: TodoApiService by lazy {
        retrofit.create(TodoApiService::class.java)
    }
}
