package com.jaime.codpay.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.jaime.codpay.data.Envio
import com.jaime.codpay.ui.components.InitRoute.TitleSection
import com.jaime.codpay.ui.viewmodel.EntregarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechazarScreen(
    navController: NavController,
    envioJson: String,
    entregarViewModel: EntregarViewModel,
    onRechazarClick: (String) -> Unit
) {
    // Deserializar el JSON a un objeto Envio
    val gson = Gson()
    val envioData: Envio? = try {
        gson.fromJson(envioJson, Envio::class.java)
    } catch (e: Exception) {
        Log.e("RechazarScreen", "Error al deserializar el JSON", e)
        null
    }

    var motivo by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 40.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                TitleSection(nombre = "Rechaza pedido")

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 38.dp),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("N° envio: ${envioData?.numeroRefPedidoB2C}", modifier = Modifier.padding(vertical = 8.dp))
                        Text("Cliente: ${envioData?.clienteFinal?.nombreClienteFinal}", modifier = Modifier.padding(vertical = 8.dp))
                        Text("Fono: ${envioData?.clienteFinal?.telefonoClienteFinal}", modifier = Modifier.padding(vertical = 8.dp))
                        Text("Dirección: ${envioData?.clienteFinal?.direccionEntrega}, ${envioData?.clienteFinal?.comunaEntrega}", modifier = Modifier.padding(vertical = 8.dp))
                        Text("N° de paquetes: ${envioData?.cantidadPaquetes}", modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    OutlinedTextField(
                        value = motivo,
                        onValueChange = { motivo = it },
                        placeholder = { Text("Motivo del rechazo...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(120.dp),
                        maxLines = 4,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Color.DarkGray,
                            focusedBorderColor = Color.LightGray,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = Color.LightGray,
                            unfocusedLabelColor = Color.Gray,
                            containerColor = Color.Transparent,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray,
                            disabledPlaceholderColor = Color.Gray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Button(
                onClick = {
                    if (motivo.isNotBlank()) {
                        envioData?.let { envio ->
                            entregarViewModel.actualizarEstadoEnvio(envio.idEnvio, "Fallido") { exito ->
                                if (exito) {
                                    Toast.makeText(context, "Pedido rechazado correctamente", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Error al rechazar el pedido", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Rechazar", color = Color.White)
            }
        }
    }
}