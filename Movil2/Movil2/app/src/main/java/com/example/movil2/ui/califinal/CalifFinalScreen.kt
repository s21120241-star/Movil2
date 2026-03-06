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
import com.example.movil2.ui.carga.SyncHeader

@Composable
fun CalifFinalScreen(viewModel: CalifFinalViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is CalifFinalUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CalifFinalUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is CalifFinalUiState.Success -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        SyncHeader("Calificaciones Finales", state.lastSync)
                    }
                    if (state.items.isEmpty()) {
                        item {
                            Text("No hay calificaciones finales disponibles.", modifier = Modifier.padding(20.dp))
                        }
                    } else {
                        items(state.items) { item ->
                            FinalListItem(item)
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FinalListItem(item: Map<String, String>) {
    val calStr = item["calificacion"] ?: item["calFinal"] ?: "-"
    val calNum = calStr.toDoubleOrNull() ?: 0.0
    val calColor = when {
        calNum >= 70 -> Color(0xFF2E7D32)
        calNum > 0   -> Color(0xFFC62828)
        else         -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item["nombreMateria"] ?: item["materia"] ?: "Materia",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Semestre: ${item["semestre"] ?: "-"}", 
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        Text(
            calStr,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = calColor
        )
    }
}