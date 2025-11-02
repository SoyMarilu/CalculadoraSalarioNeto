package com.example.calculadorasalarioneto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calculadorasalarioneto.ui.theme.CalculadoraSalarioNetoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculadoraSalarioNetoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "entrada") {
        composable("entrada") {
            PantallaEntrada(navController)
        }
        composable("resultados/{salarioBruto}/{retencion}/{deducciones}/{salarioNeto}") { backStackEntry ->
            val salarioBruto = backStackEntry.arguments?.getString("salarioBruto")?.toDouble() ?: 0.0
            val retencion = backStackEntry.arguments?.getString("retencion")?.toDouble() ?: 0.0
            val deducciones = backStackEntry.arguments?.getString("deducciones")?.toDouble() ?: 0.0
            val salarioNeto = backStackEntry.arguments?.getString("salarioNeto")?.toDouble() ?: 0.0

            PantallaResultados(
                navController = navController,
                salarioBruto = salarioBruto,
                retencion = retencion,
                deducciones = deducciones,
                salarioNeto = salarioNeto
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEntrada(navController: NavHostController) {
    var salarioBruto by remember { mutableStateOf("") }
    var numPagas by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var numHijos by remember { mutableStateOf("") }
    var tieneDiscapacidad by remember { mutableStateOf(false) }
    var mostrarError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Calculadora de Salario Neto",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = salarioBruto,
            onValueChange = { salarioBruto = it },
            label = { Text("Salario Bruto Anual (€)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = numPagas,
            onValueChange = { numPagas = it },
            label = { Text("Número de pagas") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = edad,
            onValueChange = { edad = it },
            label = { Text("Edad (opcional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = numHijos,
            onValueChange = { numHijos = it },
            label = { Text("Número de hijos (opcional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(
                checked = tieneDiscapacidad,
                onCheckedChange = { tieneDiscapacidad = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "¿Tiene discapacidad reconocida?",
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (mostrarError) {
            Text(
                text = "Por favor, rellena el salario bruto y el número de pagas",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                if (salarioBruto.isBlank() || numPagas.isBlank()) {
                    mostrarError = true
                } else {
                    mostrarError = false

                    val salario = salarioBruto.toDoubleOrNull() ?: 0.0
                    val pagas = numPagas.toIntOrNull() ?: 12
                    val edadInt = edad.toIntOrNull() ?: 30
                    val hijos = numHijos.toIntOrNull() ?: 0

                    val resultado = calcularSalario(
                        salarioBruto = salario,
                        numPagas = pagas,
                        edad = edadInt,
                        numHijos = hijos,
                        tieneDiscapacidad = tieneDiscapacidad
                    )

                    navController.navigate(
                        "resultados/${resultado.salarioBruto}/${resultado.retencion}/${resultado.deducciones}/${resultado.salarioNeto}"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calcular Salario Neto")
        }
    }
}

@Composable
fun PantallaResultados(
    navController: NavHostController,
    salarioBruto: Double,
    retencion: Double,
    deducciones: Double,
    salarioNeto: Double
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Resultados del Cálculo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        CampoResultado(
            etiqueta = "Salario Bruto Anual:",
            valor = salarioBruto,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoResultado(
            etiqueta = "Retención IRPF:",
            valor = retencion,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoResultado(
            etiqueta = "Deducciones:",
            valor = deducciones,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Salario Neto Anual:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = String.format("%.2f €", salarioNeto),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}

@Composable
fun CampoResultado(etiqueta: String, valor: Double, color: androidx.compose.ui.graphics.Color) {
    Column {
        Text(
            text = etiqueta,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = String.format("%.2f €", valor),
            fontSize = 20.sp,
            color = color
        )
    }
}

data class ResultadoCalculo(
    val salarioBruto: Double,
    val retencion: Double,
    val deducciones: Double,
    val salarioNeto: Double
)

fun calcularSalario(
    salarioBruto: Double,
    numPagas: Int,
    edad: Int,
    numHijos: Int,
    tieneDiscapacidad: Boolean
): ResultadoCalculo {

    val porcentajeIRPF = when {
        salarioBruto < 12450 -> 0.19
        salarioBruto < 20200 -> 0.24
        salarioBruto < 35200 -> 0.30
        salarioBruto < 60000 -> 0.37
        else -> 0.45
    }

    var retencionIRPF = salarioBruto * porcentajeIRPF

    var deducciones = 0.0

    deducciones += numHijos * 500.0

    if (tieneDiscapacidad) {
        deducciones += 1000.0
    }

    if (edad < 25) {
        deducciones += 300.0
    }

    retencionIRPF -= deducciones
    if (retencionIRPF < 0) retencionIRPF = 0.0

    val salarioNeto = salarioBruto - retencionIRPF

    return ResultadoCalculo(
        salarioBruto = salarioBruto,
        retencion = retencionIRPF,
        deducciones = deducciones,
        salarioNeto = salarioNeto
    )
}