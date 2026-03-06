package com.example.movil2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carga_academica")
data class CargaAcademicaDB(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val matricula: String,
    val jsonData: String, // JSON completo de la respuesta
    val lastSync: Long = System.currentTimeMillis()
)