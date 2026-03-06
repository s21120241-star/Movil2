package com.example.movil2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calif_unidades")
data class CalifUnidadesDB(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val matricula: String,
    val jsonData: String,
    val lastSync: Long = System.currentTimeMillis()
)