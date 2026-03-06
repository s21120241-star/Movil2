package com.example.movil2.ui.kardex

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
import androidx.compose.ui.unit.sp
import com.example.movil2.ui.carga.SyncHeader

@Composable
fun KardexScreen(viewModel: KardexViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is KardexUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is KardexUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is KardexUiState.Success -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { SyncHeader("Cardex Académico", state.lastSync) }
                    item { KardexStatsCard(state) }

                    items(state.items) { item ->
                        KardexTableRow(item)
                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                    }
                    
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
fun KardexStatsCard(state: KardexUiState.Success) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
            .padding(16.dp)
    ) {
        StatItem("Promedio General", state.promedio, true)
        StatItem("Materias Aprobadas", "${state.aprobadas} de ${state.totales}", false)
        StatItem("Créditos Acumulados", state.creditos, false)
    }
}

@Composable
fun StatItem(label: String, value: String, isHighlight: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Text(
            value, 
            style = if(isHighlight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = if(isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun KardexTableRow(item: Map<String, String>) {
    val materia = item["materia"] ?: item["Materia"] ?: item["nombremateria"] ?: "Sin Nombre"
    
    // Mapeo exhaustivo de la clave oficial (CVE) incluyendo la variante con punto
    val clvOficial = item["Cve."] ?: item["cve."] ?: item["clvoficial"] ?: item["ClvOficial"] ?: 
                     item["oficial"] ?: item["Oficial"] ?: item["Clave"] ?: item["clave"] ?: "-"
    
    val cdts = item["cdts"] ?: item["Cdts"] ?: item["creditos"] ?: "-"
    val calif = item["calif"] ?: item["Calif"] ?: item["calificacion"] ?: item["prom"] ?: "-"

    val califNum = calif.toIntOrNull() ?: 0
    val isAprobada = califNum >= 70
    
    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = materia,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = calif,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if(isAprobada) Color(0xFF2E7D32) else if(califNum > 0) Color(0xFFC62828) else Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Text("CVE: $clvOficial", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.width(150.dp))
            Text("Créditos: $cdts", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ORDINARIO
            OpportunityBlock(
                "ORDINARIO",
                item["sem"] ?: item["Sem"] ?: item["s1"] ?: "-",
                item["per"] ?: item["Per"] ?: item["p1"] ?: "-",
                item["anio"] ?: item["Anio"] ?: item["a1"] ?: "-"
            )
            
            // REPETICIÓN (Variantes: SemR, PerR, AnioR o S2, P2, A2 o Repeticion)
            val semR = item["semr"] ?: item["SemR"] ?: item["s2"] ?: ""
            val perR = item["perr"] ?: item["PerR"] ?: item["p2"] ?: ""
            val anioR = item["anior"] ?: item["AnioR"] ?: item["a2"] ?: ""
            
            if (anioR.isNotBlank() && anioR != "null") {
                OpportunityBlock("REPETICIÓN", semR, perR, anioR)
            }

            // ESPECIAL (Variantes: SemE, PerE, AnioE o S3, P3, A3)
            val semE = item["seme"] ?: item["SemE"] ?: item["s3"] ?: ""
            val perE = item["pere"] ?: item["PerE"] ?: item["p3"] ?: ""
            val anioE = item["anioe"] ?: item["AnioE"] ?: item["a3"] ?: ""
            
            if (anioE.isNotBlank() && anioE != "null") {
                OpportunityBlock("ESPECIAL", semE, perE, anioE)
            }
        }
    }
}

@Composable
fun OpportunityBlock(title: String, sem: String, per: String, anio: String) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), MaterialTheme.shapes.extraSmall)
            .padding(8.dp)
            .width(115.dp)
    ) {
        Text(title, style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(2.dp))
        Text("Sem: $sem", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
        Text(per, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(anio, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
    }
}
