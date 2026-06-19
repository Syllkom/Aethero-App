package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plugins")
data class PluginEntity(
    @PrimaryKey val id: String, // filename e.g. eval.js
    val name: String,
    val isCommand: Boolean,
    val usePrefix: Boolean,
    val cases: String, // comma separated
    val description: String,
    val category: String,
    val usages: String, // json or separated
    val code: String,
    val isActive: Boolean = true,
    val license: String = "MIT",
    val author: String = "Aethero",
    val version: String = "v1.0.0",
    val url: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "activity")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,
    val actionType: String,
    val description: String
)
