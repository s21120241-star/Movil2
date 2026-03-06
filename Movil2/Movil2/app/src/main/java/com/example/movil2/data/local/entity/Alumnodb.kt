package com.example.movil2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alumno")
data class AlumnoDB(
    @PrimaryKey val matricula: String,
    val nombre: String,
    val carrera: String,
    val especialidad: String,
    val semActual: Int,
    val cdtosAcumulados: Int,
    val cdtosActuales: Int,
    val estatus: String,
    val fechaReins: String,
    val urlFoto: String,
    val lastSync: Long = System.currentTimeMillis()
)