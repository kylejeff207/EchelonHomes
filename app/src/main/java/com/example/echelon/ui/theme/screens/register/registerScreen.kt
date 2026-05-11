package com.example.echelon.ui.theme.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.echelon.R
import com.example.echelon.data.AuthViewModel
import com.example.echelon.navigation.ROUTE_LOGIN

@Composable
fun RegisterScreen(navController: NavController){
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("")}
    var confirmpassword by remember { mutableStateOf("")}
    var userRole by remember { mutableStateOf("Buyer") } // Changed from Searcher to Buyer

    val authViewModel : AuthViewModel = viewModel()
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ){
        Box(
            modifier = Modifier
                .size(160.dp)
                .shadow(16.dp, CircleShape)
                .background(Color.White, CircleShape)
                .border(6.dp, Color.White, CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Create Account",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF2E7D32)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            label = {Text(text = "Username")},
            onValueChange = {username = it},
            placeholder = { Text(text = "Enter Username")},
            leadingIcon = {Icon(Icons.Default.Person,contentDescription = null)},
            modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value =email,
            onValueChange = {email =it},
            placeholder = { Text(text = "Enter Email")},
            label = { Text(text = "Email Address")},
            leadingIcon = { Icon(Icons.Default.Email,contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                capitalization = KeyboardCapitalization.None
            ),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value =password,
            onValueChange = { password =it},
            placeholder = { Text(text = "Enter Password")},
            label = { Text(text = "Password")},
            leadingIcon = { Icon(Icons.Default.Lock,contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value =confirmpassword,
            onValueChange = { confirmpassword=it},
            placeholder = { Text(text = "Confirm Password")},
            label = { Text(text = "Confirm Password")},
            leadingIcon = { Icon(Icons.Default.Check,contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))
        
        Text(text = "Select your role:", fontWeight = FontWeight.SemiBold)
        Text(text = "I am...", fontWeight = FontWeight.SemiBold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp)
        ) {
            RadioButton(
                selected = userRole == "Buyer",
                onClick = { userRole = "Buyer" }
            )
            Text(text = "Buyer")
            
            Spacer(modifier = Modifier.weight(1f))
            
            RadioButton(
                selected = userRole == "Uploader",
                onClick = { userRole = "Uploader" }
            )
            Text(text = "Uploader")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { 
                authViewModel.signup(
                    username = username.trim(),
                    email = email.trim(),
                    password = password,
                    confirmpassword = confirmpassword,
                    userRole = userRole,
                    navController = navController,
                    context = context
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            shape = MaterialTheme.shapes.medium,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) { 
            Text(text = "REGISTER", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.padding(bottom = 20.dp)) {
            Text(text = "Already Registered? ", color = Color.Gray)
            Text (text = "Login Here",
                color = Color(0xFFD32F2F),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { 
                    navController.navigate(ROUTE_LOGIN)
                }
            )
        }

    }

}


@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = rememberNavController())
}
