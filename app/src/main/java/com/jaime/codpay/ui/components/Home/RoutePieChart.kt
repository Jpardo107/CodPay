package com.jaime.codpay.ui.components.Home

import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.runtime.Composable
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
){
    val porEntregar = totalBultos - (entregados + reagendados + cancelados)
    val entries = mutableListOf<PieEntry>()
    if (entregados > 0) {
        entries.add(PieEntry(entregados.toFloat(), "Entregados"))
    }
    if (reagendados > 0) {
        entries.add(PieEntry(reagendados.toFloat(), "Reagendados"))
    }
    if (cancelados > 0) {
        entries.add(PieEntry(cancelados.toFloat(), "Cancelados"))
    }
    if (porEntregar > 0) {
        entries.add(PieEntry(porEntregar.toFloat(), "Por entregar"))
    }
    val colors = listOf(
        Color.parseColor("#4CAF50"), // verde
        Color.parseColor("#FFC107"), // amarillo
        Color.parseColor("#F44336"),  // rojo
        Color.parseColor("#2196F3")  // azul
    )
    AndroidView(
        modifier = Modifier,
        factory = { context ->
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

                val dataSet = PieDataSet(entries, "").apply {
                    setColors(colors)
                    valueTextColor = Color.BLACK
                    valueTextSize = 14f
                }
                dataSet.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }

                dataSet.setDrawValues(true) // Oculta los números encima de cada segmento
                dataSet.setDrawIcons(false)
                dataSet.sliceSpace = 2f
                dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                dataSet.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE

                data = PieData(dataSet)
                centerText = "$totalBultos\nTotal"
                setCenterTextSize(18f)
                setCenterTextColor(Color.DKGRAY)
                invalidate()
            }
        }
    )
}