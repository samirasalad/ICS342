package edu.metrostate.assignment2

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import edu.merostate.assignment2.LoginViewModel
import edu.merostate.assignment2.TodoApiService
import edu.merostate.assignment2.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class, InstantTaskExecutorExtension::class)
class LoginViewModelTest {

    private val mockApiService = mockk<TodoApiService>()
    private lateinit var viewModel: LoginViewModel

    @BeforeEach
    fun setUp() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        viewModel = LoginViewModel(application)
    }

    @Test
    fun `loginUser should succeed with valid credentials`() = runTest {
        val expectedUser = User("123", "token")
        coEvery { mockApiService.loginUser(any(), any()) } returns expectedUser

        var resultUser: User? = null
        viewModel.loginUser("test@example.com", "password") { user ->
            resultUser = user
        }

        coVerify(exactly = 1) { mockApiService.loginUser(any(), any()) }
        assertEquals(expectedUser, resultUser)
    }

    @Test
    fun `loginUser should fail with invalid credentials`() = runTest {
        coEvery { mockApiService.loginUser(any(), any()) } throws Exception("Invalid credentials")

        var resultUser: User? = null
        viewModel.loginUser("test@example.com", "wrongpassword") { user ->
            resultUser = user
        }

        coVerify(exactly = 1) { mockApiService.loginUser(any(), any()) }
        assertNull(resultUser)
    }
}
