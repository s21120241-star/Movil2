package com.example.movil2.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.json.JSONObject

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    val uiState by remember { derivedStateOf { viewModel.uiState } }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            "Perfil Académico",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (uiState) {
            is ProfileUiState.Loading -> {
                CircularProgressIndicator()
            }
            is ProfileUiState.Error -> {
                Text(
                    (uiState as ProfileUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            is ProfileUiState.Success -> {
                val json = (uiState as ProfileUiState.Success).json
                val nombre = json.optString("nombre")
                val matricula = json.optString("matricula")
                val carrera = json.optString("carrera")
                val especialidad = json.optString("especialidad")
                val semestre = json.optInt("semActual")
                val cdtosAcumulados = json.optInt("cdtosAcumulados")
                val cdtosActuales = json.optInt("cdtosActuales")
                val estatus = json.optString("estatus")
                val fechaReins = json.optString("fechaReins")
                val urlFoto = json.optString("urlFoto")
                val fotoCompleta = "https://sicenet.something.edu.mx/fotos/$urlFoto" // Ajusta el dominio real

                AsyncImage(
                    model = fotoCompleta,
                    contentDescription = "Foto del alumno",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileItem("Nombre", nombre)
                        ProfileItem("Matrícula", matricula)
                        ProfileItem("Carrera", carrera)
                        ProfileItem("Especialidad", especialidad)
                        ProfileItem("Semestre actual", semestre.toString())
                        ProfileItem("Créditos acumulados", cdtosAcumulados.toString())
                        ProfileItem("Créditos actuales", cdtosActuales.toString())
                        ProfileItem("Estatus", estatus)
                        ProfileItem("Fecha de reinscripción", fechaReins)

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onLogout() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cerrar sesión")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
}
