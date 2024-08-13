package edu.metrostate.assignment1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.metrostate.assignment1.ui.theme.Assignment1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assignment1Theme {
                TodoApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp() {
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    val todoItems = remember { mutableStateListOf<TodoItem>() }
    var newTodoText by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todo List", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE), titleContentColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { isBottomSheetVisible = true }, containerColor = Color(0xFF03DAC5)) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TodoList(tasks = todoItems)
            if (isBottomSheetVisible) {
                TodoInputSheet(
                    todoText = newTodoText,
                    showError = isErrorVisible,
                    onTodoTextChange = { newTodoText = it; isErrorVisible = false },
                    onSaveTodo = {
                        if (newTodoText.isNotBlank()) {
                            todoItems.add(TodoItem(newTodoText))
                            newTodoText = ""
                            isBottomSheetVisible = false
                            isErrorVisible = false
                        } else {
                            isErrorVisible = true
                        }
                    },
                    onCancelTodo = {
                        newTodoText = ""
                        isBottomSheetVisible = false
                        isErrorVisible = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoInputSheet(
    todoText: String,
    showError: Boolean,
    onTodoTextChange: (String) -> Unit,
    onSaveTodo: () -> Unit,
    onCancelTodo: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onCancelTodo,
        modifier = Modifier.fillMaxHeight(0.75f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = todoText,
                onValueChange = onTodoTextChange,
                label = { Text(stringResource(id = R.string.new_task)) },
                trailingIcon = {
                    IconButton(onClick = { onTodoTextChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Text")
                    }
                },
                isError = showError,
                modifier = Modifier.fillMaxWidth()
            )
            if (showError) {
                Text(text = stringResource(id = R.string.error_message), color = Color.Red, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = onSaveTodo, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5))) {
                    Text(text = stringResource(id = R.string.save))
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(onClick = onCancelTodo, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        }
    }
}

data class TodoItem(val description: String, var isDone: Boolean = false)

@Composable
fun TodoList(tasks: List<TodoItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        items(tasks) { task ->
            TodoRow(task)
        }
    }
}

@Composable
fun TodoRow(task: TodoItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(task.description, modifier = Modifier.weight(1f))
        var isChecked by remember { mutableStateOf(task.isDone) }
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                task.isDone = it
            },
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF6200EE))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TodoAppPreview() {
    Assignment1Theme {
        TodoApp()
    }
}
