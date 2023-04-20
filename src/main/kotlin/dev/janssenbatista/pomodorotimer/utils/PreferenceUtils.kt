package dev.janssenbatista.pomodorotimer.utils

import dev.janssenbatista.pomodorotimer.models.Preferences
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

object PreferenceUtils {

    private val userHomePath: String = System.getProperty("user.home")
    private val preferencesFilePath = Paths.get("$userHomePath/.config/pomodoro-timer/app.properties")
    private val properties = Properties()

    fun loadPreferences(): Preferences {
        var preferences: Preferences
        val configPath: Path?
        val appConfigPath: Path?
        val fileInputStream = FileInputStream(File(preferencesFilePath.toUri()))
        if (!Files.exists(preferencesFilePath)) {
            configPath = if (!Files.exists(Paths.get("$userHomePath/.config"))) {
                Files.createDirectory(Paths.get("$userHomePath/.config"))
            } else {
                Paths.get("$userHomePath/.config")
            }
            appConfigPath = if (!Files.exists(Paths.get("$configPath/pomodoro-timer"))) {
                Files.createDirectory(Paths.get("$configPath/pomodoro-timer"))
            } else {
                Paths.get("$configPath/pomodoro-timer")
            }
            Files.createFile(Paths.get("$appConfigPath/app.properties"))
            properties.load(fileInputStream)
            preferences = Preferences(
                workTime = 25 * 60 * 1000,
                shortBreakTime = 5 * 60 * 1000,
                longBreakTime = 15 * 60 * 1000,
                theme = "light",
                alertSound = "sound_one"
            )
            savePreferences(preferences)
        } else {
            properties.apply {
                load(fileInputStream)
                preferences = Preferences(
                    workTime = this.getProperty("WORK_TIME").toInt(),
                    shortBreakTime = this.getProperty("SHORT_BREAK_TIME").toInt(),
                    longBreakTime = this.getProperty("LONG_BREAK_TIME").toInt(),
                    theme = this.getProperty("THEME"),
                    alertSound = this.getProperty("ALERT_SOUND")
                )
            }
        }
        fileInputStream.close()
        return preferences
    }

    fun savePreferences(preferences: Preferences) {
        val fileOutputStream = FileOutputStream(File(preferencesFilePath.toUri()))
        properties.apply {
            setProperty("WORK_TIME", preferences.workTime.toString())
            setProperty("SHORT_BREAK_TIME", preferences.shortBreakTime.toString())
            setProperty("LONG_BREAK_TIME", preferences.longBreakTime.toString())
            setProperty("THEME", preferences.theme)
            setProperty("ALERT_SOUND", preferences.alertSound)
            store(fileOutputStream, "Preferences")
        }
        fileOutputStream.close()
    }
}