package com.jaime.codpay.ui.screens

import android.graphics.Color.rgb
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jaime.codpay.R
import com.jaime.codpay.data.RutasRepository
import com.jaime.codpay.data.UserDataStore
import com.jaime.codpay.ui.navigation.Screen
import com.jaime.codpay.ui.viewmodel.LoginViewModel
import com.jaime.codpay.ui.viewmodel.LoginViewModelFactory
import com.jaime.codpay.ui.viewmodel.RutasViewModel
import com.jaime.codpay.ui.viewmodel.RutasViewModelFactory

@Composable
fun LoginScreen(navController: NavController) {


    var email by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var codigo by remember { mutableStateOf("") }
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(context))

    val loginResponse by loginViewModel.loginResponse.collectAsState()
    val isLoading by loginViewModel.isLoading.collectAsState()
    val error by loginViewModel.error.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 56.dp)
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.truck),
            contentDescription = "Logo de codpay",
            modifier = Modifier
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Bienvenido a CODPay", style = MaterialTheme.typography.displaySmall)
        Spacer(modifier = Modifier.height(24.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "Usuario")
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(rgb(231, 231, 231)),
                unfocusedContainerColor = Color(rgb(208, 208, 208)),
                disabledContainerColor = Color(0xFFF6F6F6),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = clave,
            onValueChange = { clave = it },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Contraseña")
            },
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(rgb(231, 231, 231)),
                unfocusedContainerColor = Color(rgb(208, 208, 208)),
                disabledContainerColor = Color(0xFFF6F6F6),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "¿Olvidaste tu contraseña?",
            color = Color(rgb(54, 73, 211)),
            modifier = Modifier
                .clickable { /* Acción futura */ }
                .align(Alignment.End)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                loginViewModel.login(email, clave)
            },
            enabled = !isLoading,
            modifier = Modifier
                .width(200.dp)
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AssignmentInd,
                contentDescription = "check",
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(if (isLoading) "Cargando..." else "Solicitar codigo")
        }
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = codigo,
            onValueChange = { codigo = it },
            label = { Text("Código recibido") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Código") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(rgb(231, 231, 231)),
                unfocusedContainerColor = Color(rgb(208, 208, 208)),
                disabledContainerColor = Color(0xFFF6F6F6),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón final para iniciar sesión con código
        Button(
            onClick = { loginViewModel.validarCodigo(email, codigo) },
            modifier = Modifier
                .width(200.dp)
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
        ) {
            Icon(
                imageVector = Icons.Default.VpnKey,
                contentDescription = "check",
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Iniciar sesión")
        }

    Spacer(modifier = Modifier.height(16.dp))


        if (isLoading) {
            CircularProgressIndicator()
        }



        LaunchedEffect(key1 = error) {
            if (error != null) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        }

        LaunchedEffect(key1 = loginResponse) {
            if (loginResponse?.status == "success") {
                navController.navigate(Screen.Home.route)
            }
        }
    }

}