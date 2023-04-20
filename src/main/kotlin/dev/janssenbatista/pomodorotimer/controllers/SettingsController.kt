package dev.janssenbatista.pomodorotimer.controllers

import dev.janssenbatista.pomodorotimer.HomeApplication
import dev.janssenbatista.pomodorotimer.models.Preferences
import dev.janssenbatista.pomodorotimer.utils.PreferenceUtils
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.net.URL

class SettingsController {

    @FXML
    private lateinit var buttonCancel: Button

    @FXML
    private lateinit var buttonSaveSettings: Button

    @FXML
    private lateinit var comboBoxSound: ComboBox<String>

    @FXML
    private lateinit var comboBoxTheme: ComboBox<String>

    @FXML
    private lateinit var imageViewPlay: ImageView

    @FXML
    private lateinit var labelLongBreakTime: Label

    @FXML
    private lateinit var labelShortBreakTime: Label

    @FXML
    private lateinit var labelWorkTime: Label

    @FXML
    private lateinit var sliderLongBreakTime: Slider

    @FXML
    private lateinit var sliderShortBreakTime: Slider

    @FXML
    private lateinit var sliderWorkTime: Slider

    private lateinit var preferences: Preferences

    private var mediaPlayer: MediaPlayer? = null

    @FXML
    private fun initialize() {

        loadSettings()

        sliderWorkTime.valueProperty().addListener { _, _, newValue ->
            labelWorkTime.text = newValue.toInt().toString()
        }

        sliderShortBreakTime.valueProperty().addListener { _, _, newValue ->
            labelShortBreakTime.text = newValue.toInt().toString()
        }

        sliderLongBreakTime.valueProperty().addListener { _, _, newValue ->
            labelLongBreakTime.text = newValue.toInt().toString()
        }

        buttonCancel.setOnAction {
            buttonCancel.scene.window.hide()
        }

        buttonSaveSettings.setOnAction {
            saveSettings()
            Alert(
                Alert.AlertType.INFORMATION,
                "The settings will be applied on the next launch of the application"
            ).showAndWait()
            buttonSaveSettings.scene.window.hide()
        }

        comboBoxTheme.promptText = "Light"
        comboBoxTheme.items = FXCollections.observableArrayList("Light", "Dark")

        comboBoxSound.promptText = "Sound 1"
        comboBoxSound.items =
            FXCollections.observableArrayList("Sound 1", "Sound 2", "Sound 3", "Sound 4", "Sound 5")

        if (preferences.theme == "light") {
            imageViewPlay.image =
                Image(HomeApplication::class.java.getResource("images/play_light_icon.png")?.toExternalForm())
        } else {
            imageViewPlay.image =
                Image(HomeApplication::class.java.getResource("images/play_dark_icon.png")?.toExternalForm())
        }

        imageViewPlay.setOnMouseClicked {
            playSound(setSound(comboBoxSound.selectionModelProperty().get().selectedItem))
        }
    }


    private fun loadSettings() {
        preferences = PreferenceUtils.loadPreferences()
        labelWorkTime.text =
            convertMillisecondsToMinutes(preferences.workTime)
        sliderWorkTime.value = convertMillisecondsToMinutes(preferences.workTime).toDouble()
        labelShortBreakTime.text = convertMillisecondsToMinutes(
            preferences.shortBreakTime
        )
        sliderShortBreakTime.value = convertMillisecondsToMinutes(preferences.shortBreakTime).toDouble()
        labelLongBreakTime.text = convertMillisecondsToMinutes(
            preferences.longBreakTime
        )
        sliderLongBreakTime.value = convertMillisecondsToMinutes(preferences.longBreakTime).toDouble()
        comboBoxTheme.selectionModelProperty().value.select(getTheme(preferences.theme))
        comboBoxSound.selectionModelProperty().value.select(getSound(preferences.alertSound))
    }

    private fun saveSettings() {
        val preferences = Preferences(
            workTime = convertMinutesToMilliseconds(labelWorkTime.text.toInt()),
            shortBreakTime = convertMinutesToMilliseconds(labelShortBreakTime.text.toInt()),
            longBreakTime = convertMinutesToMilliseconds(labelLongBreakTime.text.toInt()),
            theme = setTheme(comboBoxTheme.selectionModelProperty().value.selectedItem),
            alertSound = setSound(comboBoxSound.selectionModelProperty().value.selectedItem)
        )
        PreferenceUtils.savePreferences(preferences)
    }

    private fun convertMillisecondsToMinutes(value: Int): String {
        val valueInMinutes = value / 60 / 1000
        return valueInMinutes.toString()
    }

    private fun convertMinutesToMilliseconds(value: Int): Int {
        return value * 60 * 1000
    }

    private fun getTheme(theme: String) = when (theme) {
        "light" -> "Light"
        "dark" -> "Dark"
        else -> ""
    }

    private fun setTheme(theme: String) = when (theme) {
        "Light" -> "light"
        "Dark" -> "dark"
        else -> ""
    }

    private fun getSound(sound: String) = when (sound) {
        "sound-one" -> "Sound 1"
        "sound-two" -> "Sound 2"
        "sound-three" -> "Sound 3"
        "sound-four" -> "Sound 4"
        "sound-five" -> "Sound 5"
        else -> ""
    }

    private fun setSound(sound: String) = when (sound) {
        "Sound 1" -> "sound-one"
        "Sound 2" -> "sound-two"
        "Sound 3" -> "sound-three"
        "Sound 4" -> "sound-four"
        "Sound 5" -> "sound-five"
        else -> ""
    }

    private fun playSound(sound: String) {
        mediaPlayer?.stop()
        val url: URL? = HomeApplication::class.java.getResource("sounds/$sound.wav")
        val media = url?.let {
            Media(it.toExternalForm())
        }
        mediaPlayer = MediaPlayer(media)
        mediaPlayer?.play()
    }


}