module dev.janssenbatista.pomodorotimer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.web;
    requires java.desktop;
    requires kotlin.stdlib;
    opens dev.janssenbatista.pomodorotimer.controllers to javafx.fxml;
    opens dev.janssenbatista.pomodorotimer to javafx.fxml;
    exports dev.janssenbatista.pomodorotimer;
}