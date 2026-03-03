package com.example.movil2.ui.califinal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.movil2.ui.carga.LastSyncLabel

@Composable
fun CalifFinalScreen(viewModel: CalifFinalViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Calificaciones Finales",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))

        when (val state = uiState) {
            is CalifFinalUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CalifFinalUiState.Error -> {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
            is CalifFinalUiState.Success -> {
                LastSyncLabel(state.lastSync)
                Spacer(modifier = Modifier.height(8.dp))
                if (state.items.isEmpty()) {
                    Text("No hay calificaciones finales disponibles.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.items) { item ->
                            CalifFinalCard(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalifFinalCard(item: Map<String, String>) {
    val calStr = item["calificacion"] ?: item["calFinal"] ?: ""
    val calNum = calStr.toDoubleOrNull() ?: 0.0
    val calColor = when {
        calNum >= 90 -> Color(0xFF2E7D32)
        calNum >= 70 -> Color(0xFF1565C0)
        calNum > 0   -> Color(0xFFC62828)
        else         -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item["nombreMateria"] ?: item["materia"] ?: "Materia",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                item["semestre"]?.let {
                    Text("Semestre $it", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline)
                }
            }
            if (calStr.isNotBlank()) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = calColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        calStr,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = calColor
                    )
                }
            }
        }
    }
}