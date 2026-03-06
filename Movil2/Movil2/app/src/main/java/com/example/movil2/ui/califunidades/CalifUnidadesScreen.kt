package com.example.movil2.ui.califunidades

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.movil2.ui.carga.SyncHeader

@Composable
fun CalifUnidadesScreen(viewModel: CalifUnidadesViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is CalifUnidadesUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CalifUnidadesUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is CalifUnidadesUiState.Success -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        SyncHeader("Calificaciones por Unidad", state.lastSync)
                    }
                    if (state.items.isEmpty()) {
                        item {
                            Text("No hay calificaciones disponibles.", modifier = Modifier.padding(20.dp))
                        }
                    } else {
                        items(state.items) { item ->
                            UnidadListItem(item)
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnidadListItem(item: Map<String, String>) {
    // Mapeo exhaustivo de nombres de materia para asegurar que aparezca
    val nombreMateria = listOf("materia", "nombreMateria", "asignatura", "Materia", "NombreMateria", "Asignatura")
        .firstNotNullOfOrNull { key -> item[key].takeIf { !it.isNullOrBlank() && it != "null" } }
        ?: "Materia Desconocida"

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = nombreMateria,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(10.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Filtro estricto para las unidades: debe ser una llave que empiece con U o C seguida de un número
            val unidades = item.filterKeys { 
                (it.lowercase().startsWith("u") || it.lowercase().startsWith("c")) && 
                it.any { char -> char.isDigit() }
            }.toList().sortedBy { entry ->
                entry.first.filter { it.isDigit() }.toIntOrNull() ?: 0
            }

            unidades.forEach { (key, value) ->
                if (!value.isNullOrBlank() && value != "null") {
                    val calif = value.toIntOrNull() ?: 0
                    val header = key.filter { it.isDigit() }.let { "U$it" }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            header,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = if (calif >= 70) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ) {
                            Text(
                                value,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (calif >= 70) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                    }
                }
            }
        }
    }
}