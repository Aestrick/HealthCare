package com.example.aplikasihealthcare.model

import androidx.room.Entity
import androidx.room.PrimaryKey
//yes

@Entity(tableName = "tensi")
data class Tensi(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tanggal: String,
    val sistolik: String,
    val diastolik: String,
    val nadi: String
)