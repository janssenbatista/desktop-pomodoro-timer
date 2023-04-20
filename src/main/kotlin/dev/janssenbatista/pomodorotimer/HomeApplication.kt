package dev.janssenbatista.pomodorotimer

import dev.janssenbatista.pomodorotimer.controllers.HomeController
import dev.janssenbatista.pomodorotimer.utils.PreferenceUtils
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.VBox
import javafx.stage.Stage

class HomeApplication : Application() {

    private lateinit var controller: HomeController

    override fun start(stage: Stage) {

        val fxmlLoader = FXMLLoader(javaClass.getResource("fxml/home.fxml"))
        val preferences = PreferenceUtils.loadPreferences()
        val root = fxmlLoader.load<VBox>()
        controller = fxmlLoader.getController()
        val scene = Scene(root)
        if (preferences.theme == "dark") {
            scene.stylesheets.remove(javaClass.getResource("css/light-theme.css")?.toExternalForm())
            scene.stylesheets.add(javaClass.getResource("css/dark-theme.css")?.toExternalForm())
        } else {
            scene.stylesheets.remove(javaClass.getResource("css/dark-theme.css")?.toExternalForm())
            scene.stylesheets.add(javaClass.getResource("css/light-theme.css")?.toExternalForm())
        }
        val image = Image(javaClass.getResource("images/app_icon.png")?.toExternalForm())
        stage.apply {
            icons.add(image)
            title = "Pomodoro Timer"
            this.scene = scene
            isResizable = false
            show()
        }
    }

    override fun stop() {
        controller.stopRunning()
        super.stop()
    }
}

fun main() {
    Application.launch(HomeApplication::class.java)
}