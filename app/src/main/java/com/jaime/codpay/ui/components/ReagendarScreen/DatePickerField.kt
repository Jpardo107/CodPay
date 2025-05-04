package com.jaime.codpay.ui.components.ReagendarScreen

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val year = selectedDate?.year ?: calendar.get(Calendar.YEAR)
    val month = selectedDate?.monthValue?.minus(1) ?: calendar.get(Calendar.MONTH)
    val day = selectedDate?.dayOfMonth ?: calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = remember {
        DatePickerDialog(context, { _, y, m, d ->
            onDateSelected(LocalDate.of(y, m + 1, d))
        }, year, month, day)
    }

    OutlinedTextField(
        value = selectedDate?.format(dateFormat) ?: "",
        onValueChange = {},
        label = { Text(label, color = Color.Gray) },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Seleccionar fecha",
                    tint = Color(0xFF7C4DFF)
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() }
            .padding(32.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            cursorColor = Color.DarkGray,
            focusedBorderColor = Color.LightGray, // Verde
            unfocusedBorderColor = Color.LightGray,
            focusedLabelColor = Color.Gray, // Verde
            unfocusedLabelColor = Color.Gray,
        )
    )
}
