package etu.ecole.ensicaen.carteemv;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class Test {

    @FXML
    private TextField Cla;
    @FXML
    private TextField Ins;
    @FXML
    private TextField P1;
    @FXML
    private TextField P2;
    @FXML
    private TextField Lc;
    @FXML
    private TextField Data;
    @FXML
    private TextField Le;
    @FXML
    private Button sendAPDU;


    String command = null;


    public void initialize() {

        Cla.setTextFormatter(getTextFormatter());
        Ins.setTextFormatter(getTextFormatter());
        P1.setTextFormatter(getTextFormatter());
        P2.setTextFormatter(getTextFormatter());
        Lc.setTextFormatter(getTextFormatter());
        Le.setTextFormatter(getTextFormatter());

        Data.setTextFormatter(new TextFormatter<String>(change -> {

            String newText = change.getControlNewText();

            if( !newText.matches("(\\d*[A-F]*)*") ){
                return null;
            }
            return change;
        }));


        sendAPDU.setOnAction(event -> {

            command = Cla.getText()+ Ins.getText()+ P1.getText()+ P2.getText()+ Lc.getText()+ Data.getText()+ Le.getText();
            System.out.println(command);
        });

    }

    private static TextFormatter<String> getTextFormatter() {
        return new TextFormatter<String>(change -> {

            String newText = change.getControlNewText();


            if (!newText.matches("\\d*") || newText.length() > 2) {
                return null;
            }
            return change;
        });
    }


}
