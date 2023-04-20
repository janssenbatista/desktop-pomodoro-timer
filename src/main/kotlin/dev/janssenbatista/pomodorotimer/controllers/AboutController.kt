package dev.janssenbatista.pomodorotimer.controllers

import javafx.fxml.FXML
import javafx.scene.control.Hyperlink
import java.awt.Desktop
import java.net.URI

class AboutController {

    @FXML
    private lateinit var hyperLinkGithub: Hyperlink

    @FXML
    private lateinit var hyperLinkOwner: Hyperlink

    @FXML
    private lateinit var hyperLinkAppIcons: Hyperlink

    @FXML
    private fun initialize() {

        val desktop: Desktop = Desktop.getDesktop()

        hyperLinkOwner.setOnAction {
            if (desktop.isSupported(Desktop.Action.APP_OPEN_URI)) {
                desktop.browse(URI("https://github.com/janssenbatista"))
            }
        }

        hyperLinkGithub.setOnAction {
            if (desktop.isSupported(Desktop.Action.APP_OPEN_URI)) {
                desktop.browse(URI("https://github.com/janssenbatista/pomodoro-timer"))
            }
        }

        hyperLinkAppIcons.setOnAction {
            if (desktop.isSupported(Desktop.Action.APP_OPEN_URI)) {
                desktop.browse(URI("https://www.svgrepo.com/"))
            }
        }
    }

}