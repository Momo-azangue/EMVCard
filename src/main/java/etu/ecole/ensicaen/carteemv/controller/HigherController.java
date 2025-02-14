package etu.ecole.ensicaen.carteemv.controller;

import etu.ecole.ensicaen.carteemv.Utils.Utils;
import etu.ecole.ensicaen.carteemv.apdu.ApduCommand;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

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


    String command = null;


    public void initialize() {
        setupFormatter();
        setupButton();
    }


    private  void setupButton(){
        sendAPDU.setOnAction(event -> { handleSendApdu();
        });
    }


    private void handleSendApdu() {
        command = Cla.getText()+ Ins.getText()+ P1.getText()+ P2.getText()+ Lc.getText()+ Data.getText()+ Le.getText();
        System.out.println(command);
        byte [] Adpu = Utils.convertHexStringToByteArray(command);

        if(Adpu.length > 0){
            try {
                ApduCommand.SendAPDU(Adpu);
                System.out.println("vous avez envoyé une APDU :" + " "+ Utils.hexify(Adpu));
                System.out.println("La réponse est :"+ " " + ApduCommand.SendAPDU(Adpu));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            System.out.println("vous ne pouvez pas envoyer de commande null");
        }
    }


    private void setupFormatter(){
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
    }

    private static TextFormatter<String> getTextFormatter() {
        return new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (!newText.matches("(\\d*[A-F]*)*") || newText.length() > 2) {
                return null;
            }
            return change;
        });
    }





}
