module etu.ecole.ensicaen.carteemv {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.smartcardio;
    requires java.sql;

    requires org.controlsfx.controls;
    requires jnasmartcardio;

    opens etu.ecole.ensicaen.carteemv to javafx.fxml;
    exports etu.ecole.ensicaen.carteemv;
    exports etu.ecole.ensicaen.carteemv.apdu;
    opens etu.ecole.ensicaen.carteemv.apdu to javafx.fxml;
    exports etu.ecole.ensicaen.carteemv.controller;
    opens etu.ecole.ensicaen.carteemv.controller to javafx.fxml;
}