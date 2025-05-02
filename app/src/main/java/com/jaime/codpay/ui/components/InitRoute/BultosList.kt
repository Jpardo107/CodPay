package com.jaime.codpay.ui.components.InitRoute

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Bulto( val numeroDespacho: String, val cliente: String)

@Composable
fun BultosList(
    bultos: List<Bulto> = listOf(
        Bulto("001", "Cliente ALPHA"),
        Bulto("002", "Cliente BETA"),
        Bulto("003", "Cliente CHARLY"),
        Bulto("004", "Cliente DELTA"),
        Bulto("005", "Cliente ECHO"),
        Bulto("006", "Cliente FOXTROT"),
        Bulto("007", "Cliente GAMMA"),
        Bulto("008", "Cliente HOTEL"),
        Bulto("009", "Cliente INDIAN"),
        Bulto("010", "Cliente KILO"),
        Bulto("011", "Cliente LIMA"),
        Bulto("012", "Cliente MIKE"),
        Bulto("013", "Cliente NOVEMBER"),
        Bulto("014", "Cliente OSCAR"),
        Bulto("015", "Cliente PAPA"),
        Bulto("016", "Cliente QUEBEC"),
    )
){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(Color(0xFFF9F1F8))
            .padding(8.dp)
    ) {
        //Cabecera
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = "N° despacho",
                modifier = Modifier.weight(1f).padding(start = 6.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "Cliente",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        //Lista de bultos
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(bultos) {bulto ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(text = bulto.numeroDespacho, modifier = Modifier.weight(1f))
                    Text(text = bulto.cliente, modifier = Modifier.weight(1f),)
                }
                Divider(color = Color.LightGray, thickness = 1.dp)
            }
        }
    }
}