package edu.metrostate.assignment2

import edu.merostate.assignment2.CreateAccountViewModel

import edu.merostate.assignment2.TodoApiService
import edu.merostate.assignment2.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class, InstantTaskExecutorExtension::class)
class CreateAccountViewModelTest {

    @Test
    fun `createAccount should succeed when api call is successful`() = runTest {

        val mockUser = mockk<User>()
        val mockApiService = mockk<TodoApiService>()
        //val viewModel = CreateAccountViewModel(mockApiService)
        val viewModel = CreateAccountViewModel(mockk(relaxed = true))

        coEvery { mockApiService.registerUser(any(), any()) } returns mockUser


        var result: User? = null
        viewModel.createAccount("hajji@gmail.com", "Naima", "Hajji@123") {
            result = it
        }


        coVerify { mockApiService.registerUser(any(), any()) }
        assertEquals(mockUser, result)
    }

    @Test
    fun `createAccount should return null when api call fails`(): Unit = runTest {

        val mockApiService = mockk<TodoApiService>()
        val viewModel = CreateAccountViewModel(mockk(relaxed = true))

        coEvery { mockApiService.registerUser(any(), any()) } throws Exception("Network error")


        var result: User? = null
        viewModel.createAccount("test@gmail.com", "Test User", "password123") {
            result = it
        }


        coVerify { mockApiService.registerUser(any(), any()) }
        assertNull(result)
    }
}