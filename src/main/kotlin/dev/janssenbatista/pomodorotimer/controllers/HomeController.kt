package dev.janssenbatista.pomodorotimer.controllers

import dev.janssenbatista.pomodorotimer.HomeApplication
import dev.janssenbatista.pomodorotimer.utils.PreferenceUtils
import javafx.animation.Animation
import javafx.animation.FadeTransition
import javafx.animation.ScaleTransition
import javafx.animation.TranslateTransition
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.util.Duration
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates
import kotlin.system.exitProcess

class HomeController {

    @FXML
    private lateinit var labelCurrentTimer: Label

    @FXML
    private lateinit var labelTimer: Label

    @FXML
    private lateinit var buttonStart: Button

    @FXML
    private lateinit var buttonPause: Button

    @FXML
    private lateinit var buttonReset: Button

    @FXML
    private lateinit var buttonSkip: Button

    @FXML
    private lateinit var progressIndicator: ProgressIndicator

    @FXML
    private lateinit var menuItemAbout: MenuItem

    @FXML
    private lateinit var menuItemClose: MenuItem

    @FXML
    private lateinit var menuItemSettings: MenuItem


    private var workTime by Delegates.notNull<Int>()
    private var shortBreakTime by Delegates.notNull<Int>()
    private var longBreakTime by Delegates.notNull<Int>()
    private var sound: String? = null
    private var theme: String? = null

    private var counter: Int = 1
    private var currentTimer by Delegates.notNull<Int>()
    private var isRunning: AtomicBoolean = AtomicBoolean(false)
    private var mediaPlayer: MediaPlayer? = null

    private val labelCurrentTimerFadeTransition = FadeTransition()
    private val labelTimerFadeTransition = FadeTransition()
    private val scaleTransition = ScaleTransition()
    private var animationIsRunning = false


    @FXML
    private fun initialize() {
        loadPreferences()
        currentTimer = getCurrentTimer(counter)
        labelTimer.text = formatTime(currentTimer)
        buttonPause.isDisable = true
        buttonReset.isDisable = true
        labelCurrentTimer.translateX = 20.0

        buttonStart.setOnAction {
            onStart()
        }
        buttonPause.setOnAction {
            onPause()
        }
        buttonReset.setOnAction {
            onReset()
        }
        buttonSkip.setOnAction {
            onSkip()
        }

        menuItemClose.setOnAction {
            exitProcess(0)
        }

        menuItemSettings.setOnAction {
            openSettingsScreen()
        }

        menuItemAbout.setOnAction {
            openAboutScreen()
        }
    }



    fun stopRunning() {
        isRunning.set(false)
    }

    @FXML
    private fun onStart() {
        if (animationIsRunning) {
            stopAnimation()
        }
        buttonStart.isVisible = false
        buttonPause.isDisable = false
        buttonReset.isDisable = true
        showProgressIndicator()
        isRunning.set(true)
        Thread {
            while (currentTimer >= 0 && isRunning.get()) {
                Platform.runLater { labelTimer.text = formatTime(currentTimer) }
                Thread.sleep(1_000)
                currentTimer -= 1_000
            }
            Platform.runLater {
                buttonStart.apply {
                    isVisible = true
                }
                if (currentTimer <= 0) {
                    startAlertAnimation()
                    buttonStart.text = "Start"
                    buttonPause.isDisable = true
                    counter++
                    currentTimer = getCurrentTimer(counter)
                    labelTimer.text = formatTime(currentTimer)
                }
            }
        }.start()
    }

    private fun onPause() {
        startPauseAnimation()
        buttonPause.isDisable = true
        buttonReset.isDisable = false
        if (isRunning.get()) {
            isRunning.set(false)
            buttonStart.text = "Resume"
        }
    }

    private fun onReset() {
        counter = 1
        currentTimer = getCurrentTimer(counter)
        labelTimer.text = formatTime(currentTimer)
        buttonPause.isDisable = true
        buttonReset.isDisable = true
        stopAnimation()
    }

    private fun onSkip() {
        counter++
        currentTimer = getCurrentTimer(counter)
        startSkipAnimation()
    }


    private fun formatTime(time: Int): String {
        val minutes = time / 60 / 1_000
        val seconds = (time / 1_000) % 60
        val strMinutes = if (minutes < 10) "0$minutes" else "$minutes"
        val strSeconds = if (seconds < 10) "0$seconds" else "$seconds"
        return "$strMinutes:$strSeconds"
    }

    private fun getCurrentTimer(counter: Int) = when {
        counter % 2 == 1 -> {
            labelCurrentTimer.text = "Work"
            workTime
        }

        counter % 2 == 0 && counter % 8 != 0 -> {
            labelCurrentTimer.text = "Short Break"
            shortBreakTime
        }

        else -> {
            labelCurrentTimer.text = "Long Break"
            longBreakTime
        }
    }

    private fun playSound() {
        val mediaPath = HomeApplication::class.java
            .getResource("sounds/$sound.wav")?.toExternalForm()
        val media = Media(mediaPath)
        mediaPlayer = MediaPlayer(media)
        mediaPlayer?.play()
    }

    private fun stopSound() {
        mediaPlayer?.stop()
    }

    private fun startPauseAnimation() {
        animationIsRunning = true
        buttonPause.isDisable = true
        buttonReset.isDisable = true
        hideProgressIndicator()
        labelCurrentTimerFadeTransition.apply {
            node = labelCurrentTimer
            duration = Duration.millis(700.0)
            fromValue = 1.0
            toValue = 0.0
            cycleCount = Animation.INDEFINITE
            play()
        }

        labelTimerFadeTransition.apply {
            node = labelTimer
            duration = Duration.millis(700.0)
            fromValue = 1.0
            toValue = 0.0
            cycleCount = Animation.INDEFINITE
            play()
        }

        scaleTransition.apply {
            node = labelTimer
            duration = Duration.millis(700.0)
            fromX = labelTimer.scaleX
            toX = labelTimer.scaleX - 0.1
            fromY = labelTimer.scaleY
            toY = labelTimer.scaleY - 0.1
            cycleCount = Animation.INDEFINITE
            setOnFinished { animationIsRunning = false }
            play()
        }
    }

    private fun startAlertAnimation() {
        playSound()
        hideProgressIndicator()
    }

    private fun startSkipAnimation() {
        ScaleTransition(Duration(300.0), progressIndicator).apply {
            fromX = 0.0
            toX = 1.0
            fromY = 0.0
            toY = 1.0
            play()
        }
        ScaleTransition(Duration(300.0), labelCurrentTimer).apply {
            fromX = 0.0
            toX = 1.0
            fromY = 0.0
            toY = 1.0
            play()
        }
        ScaleTransition(Duration(300.0), labelTimer).apply {
            fromX = 0.0
            toX = 1.0
            fromY = 0.0
            toY = 1.0
            play()
        }
    }

    private fun stopAnimation() {
        labelCurrentTimerFadeTransition.stop()
        labelTimerFadeTransition.stop()
        scaleTransition.stop()
        onStopAnimation()
        stopSound()
    }

    private fun onStopAnimation() {
        buttonStart.text = "Start"
        animationIsRunning = false
        labelTimer.apply {
            opacity = 1.0
            scaleX = 1.0
            scaleY = 1.0
        }
        labelCurrentTimer.apply {
            opacity = 1.0
            scaleX = 1.0
            scaleY = 1.0
        }
    }

    private fun showProgressIndicator() {
        progressIndicator.isVisible = true
        ScaleTransition(Duration(300.0), progressIndicator).apply {
            fromX = 0.0
            toX = 1.0
            fromY = 0.0
            toY = 1.0
            play()
        }
        TranslateTransition(Duration(300.0), labelCurrentTimer).apply {
            toX = 0.0
            play()
        }

    }

    private fun hideProgressIndicator() {
        ScaleTransition(Duration(300.0), progressIndicator).apply {
            fromX = 1.0
            toX = 0.0
            fromY = 1.0
            toY = 0.0
            play()
        }
        TranslateTransition(Duration(300.0), labelCurrentTimer).apply {
            toX = 20.0
            play()
        }
    }

    private fun loadPreferences() {
        PreferenceUtils.loadPreferences().also { filePreferences ->
            workTime = filePreferences.workTime
            shortBreakTime = filePreferences.shortBreakTime
            longBreakTime = filePreferences.longBreakTime
            sound = filePreferences.alertSound
            theme = filePreferences.theme
        }
    }

    private fun openSettingsScreen() {
        val url: URL? = HomeApplication::class.java.getResource("fxml/settings.fxml")
        url?.let {
            val root: Parent = FXMLLoader(it).load()
            val scene = Scene(root)
            if (theme == "light") {
                scene.stylesheets.add(
                    HomeApplication::class.java.getResource("css/light-theme.css")?.toExternalForm()
                )
            } else {
                scene.stylesheets.add(
                    HomeApplication::class.java.getResource("css/dark-theme.css")?.toExternalForm()
                )
            }
            Stage().apply {
                title = "Settings"
                initModality(Modality.APPLICATION_MODAL)
                this.scene = scene
                show()
            }

        } ?: Alert(Alert.AlertType.ERROR, "File not found").show()
    }

    private fun openAboutScreen() {
        val url = HomeApplication::class.java.getResource("fxml/about.fxml")
        val loader = FXMLLoader(url)
        val root = loader.load<VBox>()
        val scene = Scene(root)
        if (PreferenceUtils.loadPreferences().theme == "light") {
            scene.stylesheets.add(
                HomeApplication::class.java.getResource("css/light-theme.css")?.toExternalForm()
            )
        } else {
            scene.stylesheets.add(
                HomeApplication::class.java.getResource("css/dark-theme.css")?.toExternalForm()
            )
        }
        Stage().apply {
            this.scene = scene
            this.initModality(Modality.APPLICATION_MODAL)
            showAndWait()
        }
    }
}


