package com.example.scanwithonline.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.scanwithonline.data.store.UserDataStore
import com.example.scanwithonline.ui.theme.ScanWithOnlineTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    // 1. Initialize UserDataStore and a coroutine scope
    val context = LocalContext.current
    val userDataStore = remember { UserDataStore(context) }
    val coroutineScope = rememberCoroutineScope()

    // 2. States for the input fields
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Male") }
    var about by remember { mutableStateOf("") }
    var otherInfo by remember { mutableStateOf("") }

    // 3. Load saved data when the screen is first composed
    LaunchedEffect(Unit) {
        // Collect data from DataStore Flows and update the state
        userDataStore.userName.collect { savedName ->
            if (savedName.isNotEmpty() && savedName != "User") name = savedName
        }
        userDataStore.userAge.collect { savedAge ->
            if (savedAge > 0) age = savedAge.toString()
        }
        userDataStore.userGender.collect { savedGender ->
            selectedGender = savedGender
        }
        userDataStore.userAbout.collect { savedAbout ->
            about = savedAbout
        }
        userDataStore.userOtherInfo.collect { savedOtherInfo ->
            otherInfo = savedOtherInfo
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- UI Input Fields ---
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            GenderSpinner(
                selectedGender = selectedGender,
                onGenderSelected = { selectedGender = it }
            )

            OutlinedTextField(
                value = about,
                onValueChange = { about = it },
                label = { Text("About You") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            OutlinedTextField(
                value = otherInfo,
                onValueChange = { otherInfo = it },
                label = { Text("Other Information") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            // 4. Submit Button to save the data
            Button(
                onClick = {
                    val ageInt = age.toIntOrNull()
                    if (name.isNotBlank() && ageInt != null && ageInt > 0) {
                        // Launch a coroutine to call the suspend function
                        coroutineScope.launch {
                            userDataStore.saveProfile(name, ageInt, selectedGender, about, otherInfo)
                            Toast.makeText(context, "Profile Updated!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack() // Go back to the previous screen
                        }
                    } else {
                        Toast.makeText(context, "Please fill all fields correctly.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Submit Profile")
            }
        }
    }
}

@Composable
fun GenderSpinner(selectedGender: String, onGenderSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val genders = listOf("Male", "Female", "Other")

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedGender,
            onValueChange = { /* Read Only */ },
            label = { Text("Gender") },
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    Modifier.clickable { expanded = !expanded }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            genders.forEach { gender ->
                DropdownMenuItem(
                    text = { Text(gender) },
                    onClick = {
                        onGenderSelected(gender)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ScanWithOnlineTheme {
        ProfileScreen(navController = rememberNavController())
    }
}
