package edu.metrostate.assignment2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.merostate.assignment2.ui.theme.Assignment2Theme
import androidx.compose.runtime.livedata.observeAsState
import edu.merostate.assignment2.CreateAccountViewModel
import edu.merostate.assignment2.LoginViewModel
import edu.merostate.assignment2.TodoItem
import edu.merostate.assignment2.TodoListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("MainActivity", "onCreate called")
        setContent {
            TodoListNavigation()
        }
    }
}

@Composable
fun TodoListNavigation(startDestination: String = "login") {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController) }
        composable("createAccount") { CreateAccountScreen(navController) }
        composable("todoList") { Content() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(viewModel: TodoListViewModel = viewModel()) {
    var bottomSheet by remember { mutableStateOf(false) }

    val todoItems by viewModel.todos.observeAsState(emptyList())
    var newItem by remember { mutableStateOf("") }
    var showErrorMessage by remember { mutableStateOf(false) }
    val error by viewModel.error.observeAsState()
    var showAlertDialog by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        error?.let {
            showAlertDialog = true
            viewModel.clearError()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchTodos()
    }

    Assignment2Theme {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "ToDo",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF8796E2),
                        titleContentColor = Color.Black
                    )
                )
            }, floatingActionButton = {
                FloatingActionButton(
                    onClick = { bottomSheet = true },
                    shape = FloatingActionButtonDefaults.smallShape,
                    containerColor = Color(0xFF47B6D8)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,  // Using built-in Material Icon
                        contentDescription = "Add"
                    )
                }
            }) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                TodoList(todoItems, viewModel)
            }
            if (bottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { bottomSheet = false },
                    modifier = Modifier.fillMaxHeight(0.75f)
                ) {
                    BottomsheetContent(
                        userText = newItem,
                        showErrorMessage = showErrorMessage,
                        onNewItemChange = {
                            newItem = it
                            showErrorMessage = false
                        },
                        onSaveClick = {
                            if (newItem.isNotBlank()) {
                                Log.d("Content", "Creating new todo with description: $newItem")
                                viewModel.createTodo(
                                    description = newItem
                                )
                                newItem = ""
                                bottomSheet = false
                                showErrorMessage = false
                            } else {
                                showErrorMessage = true
                            }
                        },
                        onCancelClick = {
                            newItem = ""
                            bottomSheet = false
                            showErrorMessage = false
                        }
                    )
                }
            }
            if (showAlertDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showAlertDialog = false
                        viewModel.clearError()
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showAlertDialog = false
                                viewModel.clearError()
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    text = { Text(error ?: "Unknown error") }
                )
            }
        }
    }
}

@Composable
fun BottomsheetContent(
    userText: String,
    showErrorMessage: Boolean,
    onNewItemChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding()
    ) {
        OutlinedTextField(
            value = userText,
            onValueChange = onNewItemChange,
            label = {
                Text(text = stringResource(id = R.string.new_todo))
            },
            trailingIcon = {
                IconButton(onClick = { onNewItemChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "")
                }
            },
            isError = showErrorMessage,
            modifier = Modifier.fillMaxWidth()
        )
        if (showErrorMessage) {
            Text(
                text = stringResource(id = R.string.error_message),
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onSaveClick,  // Make sure you have onSaveClick properly defined
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF376494))
        ) {
            Text(text = stringResource(R.string.save))  // Correct reference to the string resource
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = onCancelClick,  // Make sure you have onCancelClick properly defined
            modifier = Modifier.weight(1f)
        ) {
            Text(text = stringResource(id = R.string.cancel))  // Correct reference to the string resource
        }
    }
}

@Composable
fun LoginScreen(navController: NavHostController, viewModel: LoginViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            when {
                email.isBlank() || password.isBlank() -> {
                    errorMessage = "Please enter both email and password"
                }
                else -> {
                    viewModel.loginUser(email, password) { user ->
                        if (user != null) {
                            navController.navigate("todoList")
                        } else {
                            errorMessage = "Login failed. Please enter the correct email and password."
                        }
                    }
                }
            }
        }) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Create an Account",
            color = Color.Blue,
            modifier = Modifier.clickable {
                navController.navigate("createAccount")
            }
        )
    }
}

@Composable
fun CreateAccountScreen(navController: NavHostController, viewModel: CreateAccountViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            },
            text = { Text(errorMessage) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            when {
                email.isBlank() || name.isBlank() || password.isBlank() -> {
                    errorMessage = "All fields are required"
                    showDialog = true
                }
                password.length < 8 -> {
                    errorMessage = "Password must be at least 8 characters long"
                    showDialog = true
                }
                password != confirmPassword -> {
                    errorMessage = "Passwords do not match"
                    showDialog = true
                }
                else -> {
                    viewModel.createAccount(email, name, password) { user ->
                        if (user != null) {
                            navController.navigate("todoList")
                        } else {
                            errorMessage = "Account creation failed. Please try again."
                            showDialog = true
                        }
                    }
                }
            }
        }) {
            Text("Create Account")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = {
            navController.navigate("login")
        }) {
            Text("Log In")
        }
    }
}

@Composable
fun TodoList(todoItems: List<TodoItem>, viewModel: TodoListViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .background(Color(0xFFC0E6FF)) // Blue color background for the ToDo list
    ) {
        items(todoItems) { todo ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(8.dp)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = todo.description,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    color = Color.Black
                )
                var isChecked by remember { mutableStateOf(todo.isCompleted) }
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        isChecked = it
                        viewModel.completeTodo(todo.id, todo.description, isChecked)
                    },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF39009C))
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContentPreview() {
    Assignment2Theme {
        Content()
    }
}
