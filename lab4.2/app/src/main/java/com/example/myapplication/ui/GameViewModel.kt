package com.example.myapplication.ui

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

class GameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _state

    private val _toast = MutableSharedFlow<String>()
    val toast: SharedFlow<String> = _toast

    init {
        initializeBuildings()
        startCookieProcess()
    }

    private fun startCookieProcess() {
        viewModelScope.launch {
            while (true) {
                delay(1000)

                _state.update {
                    it.copy(count = it.count + it.cookiesPerSecond)
                }

            }
        }
    }

    fun onCookieClicked() {
        _state.update {
            it.copy(count = it.count + 1.0)
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
                    it.copy(
                        count = it.count + 1,
                        cost = (it.cost * 1.15).toInt()
                    )
                } else it
            }
            _state.value = _state.value.copy(
                count = _state.value.count - building.cost,
                buildings = updatedBuildings,
                cookiesPerSecond = _state.value.cookiesPerSecond + building.income
            )

            viewModelScope.launch {
                _toast.emit("Вы купили ${building.name}!")
            }
        } else {
            viewModelScope.launch {
                _toast.emit("Недостаточно печенек для ${building.name}.")
            }
        }
    }

    fun updateBuildingsAvailability() {
        val currentCookies = _state.value.count
        val updatedBuildings = _state.value.buildings.map {
            it.copy(isAvailable = currentCookies >= it.cost)
        }
        _state.value = _state.value.copy(buildings = updatedBuildings)
    }
}
