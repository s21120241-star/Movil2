package com.example.movil2.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.movil2.ui.carga.SyncHeader
import org.json.JSONObject

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    val uiState by remember { derivedStateOf { viewModel.uiState } }

    LaunchedEffect(Unit) { viewModel.loadProfile() }

    Column(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ProfileUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }
            }
            is ProfileUiState.Success -> {
                val json = state.json
                val urlFoto = "https://sicenet.surguanajuato.tecnm.mx/fotos/${json.optString("urlFoto")}"

                Column(modifier = Modifier.fillMaxSize()) {
                    // Encabezado con Nombre y Matrícula
                    ProfileHeader(json.optString("nombre"), json.optString("matricula"), urlFoto)

                    // Lista de Datos
                    Column(modifier = Modifier.padding(16.dp)) {
                        ProfileDataRow("Carrera", json.optString("carrera"))
                        ProfileDataRow("Especialidad", json.optString("especialidad"))
                        ProfileDataRow("Semestre Actual", json.optString("semActual"))
                        ProfileDataRow("Créditos Acumulados", json.optString("cdtosAcumulados"))
                        ProfileDataRow("Créditos Actuales", json.optString("cdtosActuales"))
                        ProfileDataRow("Estatus", json.optString("estatus"))
                        ProfileDataRow("Fecha Reinscripción", json.optString("fechaReins"))
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Pie de Página con Sincronización y Logout
                    SyncHeader("Perfil Sincronizado", state.lastSync.toString())
                    Button(
                        onClick = { onLogout() },
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cerrar Sesión")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(nombre: String, matricula: String, fotoUrl: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = fotoUrl,
            contentDescription = "Foto de Perfil",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(matricula, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun ProfileDataRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            fontSize = 11.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), thickness = 0.5.dp)
    }
}