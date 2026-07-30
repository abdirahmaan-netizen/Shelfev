package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logs")
data class LogEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "in" or "out"
    val productId: Long,
    val productName: String,
    val amount: Int,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
