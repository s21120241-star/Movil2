package com.example.movil2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kardex")
data class KardexDB(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val matricula: String,
    val jsonData: String,
    val lastSync: Long = System.currentTimeMillis()
)