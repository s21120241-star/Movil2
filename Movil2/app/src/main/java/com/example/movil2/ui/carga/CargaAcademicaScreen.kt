package com.example.movil2.ui.carga

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CargaAcademicaScreen(viewModel: CargaAcademicaViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Carga Académica",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))

        when (val state = uiState) {
            is CargaUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CargaUiState.Error -> {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
            is CargaUiState.Success -> {
                LastSyncLabel(state.lastSync)
                Spacer(modifier = Modifier.height(8.dp))
                if (state.items.isEmpty()) {
                    Text("No hay materias registradas en este período.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.items) { item ->
                            CargaCard(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CargaCard(item: Map<String, String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                item["nombreMateria"] ?: item["materia"] ?: "Materia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            item.filterKeys { it != "nombreMateria" && it != "materia" }.forEach { (k, v) ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("$k: ", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary)
                    Text(v, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}