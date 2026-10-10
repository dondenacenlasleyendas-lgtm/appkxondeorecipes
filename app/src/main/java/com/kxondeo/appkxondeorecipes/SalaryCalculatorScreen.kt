@file:OptIn(ExperimentalMaterial3Api::class)

package com.kxondeo.appkxondeorecipes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kxondeo.appkxondeorecipes.ui.theme.KxRed
import java.util.Locale

@Composable
fun SalaryCalculatorScreen(
    appLanguage: AppLanguage,
    onMenuClick: () -> Unit
) {
    val isSpanish = appLanguage == AppLanguage.Spanish

    var hourlyRateStr by remember { mutableStateOf("12.50") }
    var regularHoursStr by remember { mutableStateOf("40") }
    var overtimeHoursStr by remember { mutableStateOf("5") }
    var overtimeMultiplierStr by remember { mutableStateOf("1.5") }
    var taxDeductionStr by remember { mutableStateOf("15.0") }

    val hourlyRate = hourlyRateStr.toDoubleOrNull() ?: 0.0
    val regularHours = regularHoursStr.toDoubleOrNull() ?: 0.0
    val overtimeHours = overtimeHoursStr.toDoubleOrNull() ?: 0.0
    val overtimeMultiplier = overtimeMultiplierStr.toDoubleOrNull() ?: 1.5
    val taxDeduction = taxDeductionStr.toDoubleOrNull() ?: 0.0

    val regularPay = regularHours * hourlyRate
    val overtimePay = overtimeHours * hourlyRate * overtimeMultiplier
    val grossSalary = regularPay + overtimePay
    val netSalary = grossSalary * (1 - taxDeduction / 100)
    val employerTotalCost = grossSalary * 1.30 // ~30% SS employer cost

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isSpanish) "Cálculo de Salario" else "Salary Calculator", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isSpanish) "Resumen Estimado (Semanal/Mensual)" else "Estimated Summary",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Salario Neto: ${String.format(Locale.getDefault(), "%.2f €", netSalary)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = KxRed
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Salario Bruto: ${String.format(Locale.getDefault(), "%.2f €", grossSalary)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Coste Total Empresa: ${String.format(Locale.getDefault(), "%.2f €", employerTotalCost)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            OutlinedTextField(
                value = hourlyRateStr,
                onValueChange = { hourlyRateStr = it },
                label = { Text(if (isSpanish) "Tarifa por hora (€/h)" else "Hourly Rate (€/h)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = regularHoursStr,
                onValueChange = { regularHoursStr = it },
                label = { Text(if (isSpanish) "Horas Ordinarias" else "Regular Hours") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = overtimeHoursStr,
                onValueChange = { overtimeHoursStr = it },
                label = { Text(if (isSpanish) "Horas Extra" else "Overtime Hours") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = taxDeductionStr,
                onValueChange = { taxDeductionStr = it },
                label = { Text(if (isSpanish) "Retención / IRPF (%)" else "Tax / Deduction (%)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}
