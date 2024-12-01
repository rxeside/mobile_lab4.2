package com.example.myapplication.ui

import com.example.myapplication.ui.buildings.Building

data class GameState(
    val count: Double = 0.0,
    val cookiesPerSecond: Double = 0.0,
    val buildings: List<Building> = emptyList(),
    val averageSpeed: Double = 0.0,
    val elapsedTime: String = "0:00"
)
