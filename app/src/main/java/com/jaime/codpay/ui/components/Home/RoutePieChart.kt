package com.jaime.codpay.ui.components.Home

import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun RoutePieChart(
    totalBultos: Int,
    entregados: Int,
    reagendados: Int,
    cancelados: Int,
    modifier: Modifier = Modifier
) {
    // Log para ver cuándo se recompone el Composable y con qué datos
    Log.d(
        "RoutePieChart_Recompose",
        "Params: totalBultos=$totalBultos, entregados=$entregados, reagendados=$reagendados, cancelados=$cancelados"
    )

    // Los colores pueden ser recordados ya que no cambian entre recomposiciones
    val chartColors = remember {
        listOf(
            Color.parseColor("#4CAF50"), // verde para entregados
            Color.parseColor("#FFC107"), // amarillo para reagendados
            Color.parseColor("#F44336"),  // rojo para cancelados
            Color.parseColor("#2196F3")  // azul para por entregar
        )
    }
    AndroidView(
        modifier = Modifier,
        factory = {
            context ->
            Log.d("RoutePieChart_Factory", "Creando PieChart instance")
            PieChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    700
                )
                description.isEnabled = false
                legend.isEnabled = false
                setDrawEntryLabels(true)
                setEntryLabelColor(Color.BLACK)
                setEntryLabelTextSize(16f)

                isDrawHoleEnabled = true
                holeRadius = 55f
                transparentCircleRadius = 55f
                setHoleColor(Color.TRANSPARENT)

                this.data = PieData(PieDataSet(null, ""))
            }
        },
                update = { pieChart ->
            // --- UPDATE: Se ejecuta cuando los parámetros (totalBultos, etc.) cambian ---
            Log.d(
                "RoutePieChart_Update",
                "Actualizando PieChart con: totalBultos=$totalBultos, entregados=$entregados, reagendados=$reagendados, cancelados=$cancelados"
            )

            val porEntregar = totalBultos - (entregados + reagendados + cancelados)
            Log.d("RoutePieChart_Update", "porEntregar calculado: $porEntregar")

            val entries = mutableListOf<PieEntry>()
            val currentColors = mutableListOf<Int>() // Colores para las entries actuales

            // Añadir entries y sus colores correspondientes
            // El orden en que añades a entries y currentColors debe coincidir
            if (entregados > 0) {
                entries.add(PieEntry(entregados.toFloat(), "Entregado")) // Cambiado "Entregados" a "Entregado" por consistencia
                currentColors.add(chartColors[0]) // Verde
            }
            if (reagendados > 0) {
                entries.add(PieEntry(reagendados.toFloat(), "Reprogramado")) // Cambiado "Reagendados" a "Reprogramado"
                currentColors.add(chartColors[1]) // Amarillo
            }
            if (cancelados > 0) {
                entries.add(PieEntry(cancelados.toFloat(), "Fallido")) // Cambiado "Cancelados" a "Fallido"
                currentColors.add(chartColors[2]) // Rojo
            }
            if (porEntregar > 0) {
                entries.add(PieEntry(porEntregar.toFloat(), "Por entregar"))
                currentColors.add(chartColors[3]) // Azul
            }

            Log.d("RoutePieChart_Update", "Número de entries creadas: ${entries.size}")
            entries.forEachIndexed { index, entry ->
                Log.d("RoutePieChart_Update", "Entry ${index + 1}: label=${entry.label}, value=${entry.value}")
            }

                    if (entries.isNotEmpty()) {
                        val dataSet = PieDataSet(entries, "Resultados de Ruta").apply {
                            setColors(currentColors)
                            sliceSpace = 3f // Espacio entre porciones

                            // --- Configuración para las líneas y posición de las etiquetas de valor/entrada ---
                            valueLinePart1OffsetPercentage = 40f // Porcentaje desde el centro donde empieza la línea
                            valueLinePart1Length = 0.5f // Longitud de la primera parte de la línea
                            valueLinePart2Length = 0.3f // Longitud de la segunda parte de la línea
                            valueLineWidth = 1f
                            valueLineColor = Color.DKGRAY // Color de las líneas guía
                            xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE // Etiquetas de valor X fuera
                            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE // Etiquetas de valor Y fuera (para que el texto esté fuera)

                            // --- Configuración de los valores numéricos (opcional si solo quieres etiquetas fuera) ---
                            setDrawValues(true) // Mostrar los valores numéricos (ej: "1", "2")
                            valueTextColor = Color.BLACK
                            valueTextSize = 12f // Ajusta el tamaño
                            valueFormatter = object : ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    return value.toInt().toString()
                                }
                            }
                        }

                        // Las etiquetas de las entradas (los nombres "Entregado", etc.) se controlan a nivel de PieChart
                        // y ya están habilitadas con setDrawEntryLabels(true) en el factory.
                        // El PieDataSet controla cómo y dónde se dibujan los *valores* y sus líneas.

                        pieChart.data = PieData(dataSet)
                    } else {
                        pieChart.clear()
                    }

            // Actualizar el texto central

            pieChart.centerText = "$totalBultos\nTotal"

            // Notificar al gráfico que los datos han cambiado y necesita redibujarse
            pieChart.invalidate()
            Log.d("RoutePieChart_Update", "PieChart invalidado")
        }
    )
}