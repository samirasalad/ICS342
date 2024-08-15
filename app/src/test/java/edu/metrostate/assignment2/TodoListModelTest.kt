package edu.metrostate.assignment2

import edu.merostate.assignment2.TodoApiService
import edu.merostate.assignment2.TodoItem
import edu.merostate.assignment2.TodoListViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
//import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class, InstantTaskExecutorExtension::class)
class TodoListViewModelTest {

    @Test
    fun `fetchTodos should succeed when api call is successful`() = runTest {

        val mockTodos = listOf(mockk<TodoItem>())
        val mockApiService = mockk<TodoApiService>()

        //val viewModel = TodoListViewModel(mockApiService)
        val viewModel = TodoListViewModel(mockk(relaxed = true))

        coEvery { mockApiService.getTodos(any(), any(), any()) } returns mockTodos


        viewModel.fetchTodos()


        coVerify { mockApiService.getTodos(any(), any(), any()) }
        assertEquals(mockTodos, viewModel.todos.value)
    }

    @Test
    fun `fetchTodos should return error when api call fails`() = runTest {

        val mockApiService = mockk<TodoApiService>()
        val viewModel = TodoListViewModel(mockk(relaxed = true))

        coEvery { mockApiService.getTodos(any(), any(), any()) } throws Exception("Network error")


        viewModel.fetchTodos()


        coVerify { mockApiService.getTodos(any(), any(), any()) }
        assertEquals("Failed to fetch todos", viewModel.error.value)
    }

    @Test
    fun `createTodo should succeed when api call is successful`() = runTest {

        val mockTodo = mockk<TodoItem>()
        val mockApiService = mockk<TodoApiService>()
        val viewModel = TodoListViewModel(mockk(relaxed = true))

        coEvery { mockApiService.createTodo(any(), any(), any(), any()) } returns mockTodo


        viewModel.createTodo("New Todo Item")


        coVerify { mockApiService.createTodo(any(), any(), any(), any()) }

        coVerify { mockApiService.getTodos(any(), any(), any()) }
    }

    @Test
    fun `createTodo should return error when api call fails`() = runTest {

        val mockApiService = mockk<TodoApiService>()
        val viewModel = TodoListViewModel(mockk(relaxed = true))

        coEvery { mockApiService.createTodo(any(), any(), any(), any()) } throws Exception("Network error")


        viewModel.createTodo("New Todo Item")


        coVerify { mockApiService.createTodo(any(), any(), any(), any()) }
        assertEquals("Failed to create todo: Network error", viewModel.error.value)
    }



}