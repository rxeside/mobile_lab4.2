package com.example.myapplication.ui

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.ui.buildings.Building
import com.example.myapplication.ui.buildings.BuildingType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

class GameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _state

    private val _toast = MutableStateFlow<String?>(null)
    val toast: StateFlow<String?> = _toast

    private var lastToastPowerOfTen = 0

    init {
        initializeBuildings()
        startCookieProcess()
    }

    @SuppressLint("DefaultLocale")
    private fun formatElapsedTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }

    private fun startCookieProcess() {
        var elapsedTimeInSeconds = 0 // Время с начала игры в секундах

        viewModelScope.launch {
            while (true) {
                delay(1000)
                elapsedTimeInSeconds++

                _state.update { state ->
                    val updatedState = state.copy(
                        count = state.count + state.cookiesPerSecond,
                        elapsedTime = formatElapsedTime(elapsedTimeInSeconds)
                    )
                    checkAndShowToastForPowerOfTen(updatedState.count)
                    updatedState.copy(
                        buildings = updateBuildingsAvailability(updatedState)
                    )
                }
            }
        }
    }


    fun onCookieClicked() {
        _state.update { state ->
            val updatedState = state.copy(count = state.count + 1.0)
            checkAndShowToastForPowerOfTen(updatedState.count)
            updatedState.copy(
                buildings = updateBuildingsAvailability(updatedState)
            )
        }
    }

    private fun initializeBuildings() {
        _state.value = _state.value.copy(
            buildings = listOf(
                Building("Клик", BuildingType.CLICKER, 0, 15, 0.1, false),
                Building("Бабуля", BuildingType.GRANDMA, 0, 100, 1.0, false),
                Building("Ферма", BuildingType.FARM, 0, 1100, 8.0, false),
                Building("Шахта", BuildingType.MINE, 0, 12000, 47.0, false),
                Building("Фабрика", BuildingType.FABRIC, 0, 130000, 260.0, false),
                Building("Банк", BuildingType.BANK, 0, 1400000, 1400.0, false),
                Building("Храм", BuildingType.TEMPLE, 0, 20000000, 7800.0, false),
                Building("Башня волшебника", BuildingType.WIZARD_TOWER, 0, 330000000, 44000.0, false)
            )
        )
    }

    fun buyBuilding(building: Building) {
        if (_state.value.count >= building.cost) {
            val updatedBuildings = _state.value.buildings.map {
                if (it.name == building.name) {
                    val newIncome = it.income * 1.10 // Увеличиваем доход на 10% за каждое строение
                    it.copy(
                        count = it.count + 1,
                        cost = (it.cost * 1.15).toInt(),
                        income = newIncome
                    )
                } else it
            }

            _state.update { state ->
                val totalIncome = updatedBuildings.sumOf { it.count * it.income }
                val updatedState = state.copy(
                    count = state.count - building.cost,
                    buildings = updatedBuildings,
                    cookiesPerSecond = totalIncome
                )
                checkAndShowToastForPowerOfTen(updatedState.count)
                updatedState.copy(
                    buildings = updateBuildingsAvailability(updatedState)
                )
            }

            viewModelScope.launch {
                _toast.emit("Вы купили ${building.name}! Теперь оно приносит больше!")
            }
        } else {
            viewModelScope.launch {
                _toast.emit("Недостаточно печенек для ${building.name}.")
            }
        }
    }

    private fun updateBuildingsAvailability(state: GameState): List<Building> {
        val currentCookies = state.count
        return state.buildings.map {
            it.copy(
                isAvailable = currentCookies >= it.cost,
                nextIncome = it.income * 1.10
            )
        }
    }


    private fun checkAndShowToastForPowerOfTen(currentCount: Double) {
        val currentPowerOfTen = floor(log10(currentCount.coerceAtLeast(1.0))).toInt()
        if (currentPowerOfTen > lastToastPowerOfTen) {
            lastToastPowerOfTen = currentPowerOfTen
            val milestone = 10.0.pow(currentPowerOfTen).toInt()
            viewModelScope.launch {
                _toast.value = "Поздравляем! Вы достигли $milestone печенек!"
            }
        }
    }

    fun clearToast() {
        _toast.value = null
    }
}
