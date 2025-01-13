module etu.ecole.ensicaen.carteemv {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.smartcardio;

    requires org.controlsfx.controls;
    requires jnasmartcardio;

    opens etu.ecole.ensicaen.carteemv to javafx.fxml;
    exports etu.ecole.ensicaen.carteemv;
}