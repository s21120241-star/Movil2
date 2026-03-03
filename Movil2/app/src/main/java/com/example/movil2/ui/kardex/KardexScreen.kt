package com.example.movil2.ui.kardex

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.movil2.ui.carga.LastSyncLabel

@Composable
fun KardexScreen(viewModel: KardexViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Kardex",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))

        when (val state = uiState) {
            is KardexUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is KardexUiState.Error -> {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
            is KardexUiState.Success -> {
                LastSyncLabel(state.lastSync)
                Spacer(modifier = Modifier.height(4.dp))
                if (state.promedio != "N/A") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            "Promedio general: ${state.promedio}",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.items) { item ->
                        KardexCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun KardexCard(item: Map<String, String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                item["nombreMateria"] ?: item["materia"] ?: "Materia",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                item["calificacion"]?.let {
                    Text("Calificación: $it", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                item["semestre"]?.let {
                    Text("Sem: $it", style = MaterialTheme.typography.bodySmall)
                }
                item["creditos"]?.let {
                    Text("Créditos: $it", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}