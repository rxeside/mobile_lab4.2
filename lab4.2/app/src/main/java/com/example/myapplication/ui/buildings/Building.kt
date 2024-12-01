package com.example.myapplication.ui.buildings

data class Building(
    val id: Int,
    val name: String,
    val type: BuildingType,
    val count: Int,
    val cost: Int,
    val income: Double,
    val isAvailable: Boolean,
    val nextIncome: Double = income * 1.10
)

enum class BuildingType {
    CLICKER, GRANDMA, FARM, MINE, FABRIC, BANK, TEMPLE, WIZARD_TOWER
}
