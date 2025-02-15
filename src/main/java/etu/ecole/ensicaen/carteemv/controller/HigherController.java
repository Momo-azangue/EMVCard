package etu.ecole.ensicaen.carteemv.controller;

import etu.ecole.ensicaen.carteemv.Utils.Utils;
import etu.ecole.ensicaen.carteemv.apdu.ApduCommand;
import etu.ecole.ensicaen.carteemv.model.DatabaseModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;

import java.util.Arrays;

public class HigherController {

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
    @FXML
    private TextArea Log;

    private DatabaseModel databaseModel = new DatabaseModel();


    public String command = null;
    private int countActionOnLog = 0;
    private int countResponseLog = 0;

    public void initialize() {
        setupTextFieldProperty();
        setupButton();
    }

    /**
     *
     */
    private  void setupButton(){
        sendAPDU.setOnAction(event -> { handleSendApdu();
        });
    }

    /**
     *
     */
    private void handleSendApdu() {
        countActionOnLog++;
        countResponseLog =  countActionOnLog +1;
        command = Cla.getText()+ Ins.getText()+ P1.getText()+ P2.getText()+ Lc.getText()+ Data.getText()+ Le.getText();
        System.out.println(command);
        byte [] Adpu = Utils.convertHexStringToByteArray(command);
        System.out.println(Adpu.length);
        if(Adpu.length >= 4){
            try {
                ApduCommand.SendAPDU(Adpu);
                System.out.println("vous avez envoyé une APDU :" + " "+ Utils.hexify(Adpu));
                System.out.println("La réponse est :"+ " " + ApduCommand.SendAPDU(Adpu));
                Log.appendText( countActionOnLog + "  " + Utils.hexify(Adpu) + "\n");
                Log.appendText(countResponseLog + "  " + Utils.hexify(ApduCommand.SendAPDU(Adpu).getBytes()) + "\n");

                databaseModel.logToDatabase(Utils.hexify(Adpu), Utils.hexify(ApduCommand.SendAPDU(Adpu).getBytes()));
            } catch (Exception e) {
                Log.appendText( countActionOnLog + "  " + ": Erreur - " + e.getMessage() + "\n");
                throw new RuntimeException(e);

            }
        }else {
            System.out.println("vous ne pouvez pas envoyer de commande une commade courte");
            SimpleController.showAlert("Erreur", "longueur de l'apdu incorrecte");
            Log.appendText(countActionOnLog + "  " + ": Erreur - Longueur de l'APDU incorrecte\n");

        }
    }

    /**
     *
     *
     */
    private void setupTextFieldProperty(){
        Cla.setTextFormatter(getTextFormatter());
        Ins.setTextFormatter(getTextFormatter());
        P1.setTextFormatter(getTextFormatter());
        P2.setTextFormatter(getTextFormatter());
        Lc.setTextFormatter(getTextFormatter());
        Le.setTextFormatter(getTextFormatter());

        Data.setTextFormatter(new TextFormatter<String>(change -> {

            String newText = change.getControlNewText();

            if( !newText.matches("(\\d*[A-Fa-f]*)*") ){
                return null;
            }
            return change;
        }));

        addFocusListener(Cla, Ins);
        addFocusListener(Ins, P1);
        addFocusListener(P1, P2);
        addFocusListener(P2, Lc);
        addFocusListener(Lc, Data);
        addFocusListener(Data, Le);
        addFocusListener(Data, Cla);


        Lc.textProperty().addListener((observable, oldValue, newValue) -> {
            updateDataFieldBasedOnLc(newValue);
        });

        Data.textProperty().addListener((observable, oldValue, newValue) -> {
            checkDataLengthAgainstLc();
        });

    }


    private void addFocusListener(TextField currentField, TextField nextField){
        currentField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER){
                nextField.requestFocus();
                event.consume();
            }
        });
    }
    private static TextFormatter<String> getTextFormatter() {
        return new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (!newText.matches("[0-9A-Fa-f]*") || newText.length() > 2) {
                return null;
            }
            return change;
        });
    }

    private void updateDataFieldBasedOnLc(String newLcValue) {
        if (!newLcValue.isEmpty()) {
            try {
                int length = Integer.parseInt(newLcValue, 16); // Convertir de l'hexadécimal
                generateHexStringOfLength(length);
            } catch (NumberFormatException e) {
                Data.setText("");
            }
        } else {
            Data.setText("");
        }
        checkDataLengthAgainstLc();
    }

    private void checkDataLengthAgainstLc() {
        String lcValue = Lc.getText();
        String dataValue = Data.getText();

        if (!lcValue.isEmpty() && !dataValue.isEmpty()) {
            try {
                int length = Integer.parseInt(lcValue, 16); // Convertir de l'hexadécimal
                if (dataValue.length() / 2 > length) {
                    Data.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
                } else {
                    Data.setStyle(""); // Réinitialiser le style
                }
            } catch (NumberFormatException e) {
                Data.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
            }
        } else {

            if(!dataValue.isEmpty()){
                Lc.setStyle("-fx-background-color: red");
            }else {
                Lc.setStyle("");
            }
            Data.setStyle("");

        }
    }

    private String generateHexStringOfLength(int length) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            hexString.append("00");
        }
        return hexString.toString();
    }



}
