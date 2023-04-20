package dev.janssenbatista.pomodorotimer.models

data class Preferences(
    val workTime: Int,
    val shortBreakTime: Int,
    val longBreakTime: Int,
    val theme: String,
    val alertSound: String
)
