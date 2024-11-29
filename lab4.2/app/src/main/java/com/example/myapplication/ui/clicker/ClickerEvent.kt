package com.example.myapplication.ui.clicker

sealed class ClickerEvent {
    object OnClick : ClickerEvent()
    data class OnLongClick(val data: String) : ClickerEvent()
}