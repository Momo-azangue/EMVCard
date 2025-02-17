package etu.ecole.ensicaen.carteemv.controller;

import etu.ecole.ensicaen.carteemv.Utils.Utils;
import etu.ecole.ensicaen.carteemv.apdu.ApduCommand;
import etu.ecole.ensicaen.carteemv.apdu.Command;
import etu.ecole.ensicaen.carteemv.model.DatabaseModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import javax.smartcardio.*;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static etu.ecole.ensicaen.carteemv.controller.SimpleController.showAlert;

public class HigherController implements Initializable {

    private TerminalFactory tf;
    private Card card;
    private CardChannel channel;
    private ResponseAPDU response;
    private boolean isPinVerify;
    private List<CardTerminal> cardTerminals;

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
    private Button stream;
    @FXML
    private Button Readers;
    @FXML
    private Button Connect;
    @FXML
    private Button sendAPDU;
    @FXML
    private Label data_Label;
    @FXML
    private TextArea Log;
    @FXML
    private  ComboBox<CardTerminal> cardTerminalComboBox;
    @FXML
    private HBox sectionTextfield;

    private final DatabaseModel databaseModel = new DatabaseModel();
    private ResourceBundle bundle;

    public String command = null;
    private int countActionOnLog = 0;
    private int countResponseLog = 0;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        Locale defaultLocale = Locale.getDefault();
        ressourceBundleComponents(defaultLocale);

        setupTextFieldProperty();
        setupButton();
        loadCardTerminals();
        setuoConfiguration();

    }

    /**
     *
     */
    private  void setuoConfiguration(){
    sectionTextfield.setDisable(true);
    Connect.setDisable(true);
    sendAPDU.setDisable(true);
    Data.setDisable(true);
    }
    private  void setupButton(){
        sendAPDU.setOnAction(event -> { handleSendApdu();
        });
        Readers.setOnAction(event -> { setRefreshReaderButton();
        });
        stream.setOnAction(event -> {
            handleReaderButtonAction();
        });
        Connect.setOnAction(event -> {
            handleAtrButtonAction();
        });


    }


    public void ressourceBundleComponents(Locale locale){

        bundle = ResourceBundle.getBundle("messages", locale);
        stream.setText(bundle.getString("Stream-button"));
        Readers.setText(bundle.getString("List_reader_button"));
        Connect.setText(bundle.getString("Connect_button"));
        data_Label.setText(bundle.getString("Label_6"));
        sendAPDU.setText(bundle.getString("Apdu_button"));
    }

    private void loadCardTerminals(){
        try{
            tf = TerminalFactory.getDefault();
            cardTerminals = tf.terminals().list();
            if(cardTerminals.isEmpty()){
                showAlert("Pas de lecteur présent", "Veuillez insérer un lecteur.");
            }else {
                cardTerminalComboBox.getItems().addAll(cardTerminals);
                cardTerminalComboBox.setDisable(false);
            }
        }catch (CardException e){
            showAlert("Erreur", "Erreur lors du chargement des terminaux : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleReaderButtonAction(){
        try {

            CardTerminal selectedTerminal = cardTerminalComboBox.getValue();
            if (selectedTerminal != null && selectedTerminal.isCardPresent()){
                card = selectedTerminal.connect("*");
                card.getATR();
                showAlert("Connexion", "La connexion a été établi");
                Connect.setDisable(false);

            }else{
                showAlert("No card present", "veuillez inserer une carte s'iil vous plait");
            }
        } catch (CardException e) {
            showAlert("Erreur", "Erreur lors de la connexion à la carte : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleAtrButtonAction(){
        try {

            card = cardTerminalComboBox.getValue().connect("*");
            channel = card.getBasicChannel();
            response = channel.transmit(new CommandAPDU(Command.selectCommand));
            sectionTextfield.setDisable(false);
            sendAPDU.setDisable(false);
            Data.setDisable(false);
        } catch (CardException e) {
            showAlert("Erreur", "Erreur lors de la transmission de la commande APDU : " + e.getMessage());
            e.printStackTrace();
        }
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
        if(Adpu.length >= 4){
            try {
                ApduCommand.SendAPDU(Adpu);
                System.out.println("vous avez envoyé une APDU :" + " "+ Utils.hexify(Adpu));
                System.out.println("La réponse est :"+ " " + Utils.hexify(ApduCommand.SendAPDU(Adpu).getBytes()));
                Log.appendText( countActionOnLog + "  " + Utils.hexify(Adpu) + "\n");
                Log.appendText(countResponseLog + "  " + Utils.hexify(ApduCommand.SendAPDU(Adpu).getBytes()) + "\n");

                databaseModel.logToDatabase(Utils.hexify(Adpu), Utils.hexify(ApduCommand.SendAPDU(Adpu).getBytes()));
            } catch (Exception e) {
                Log.appendText( countActionOnLog + "  " + ": Erreur - " + e.getMessage() + "\n");
                throw new RuntimeException(e);

            }
        }else {
            System.out.println("vous ne pouvez pas envoyer de commande une commade courte");
            showAlert("Erreur", "longueur de l'apdu incorrecte");
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

    private void setRefreshReaderButton(){
        cardTerminalComboBox.getItems().clear();
        loadCardTerminals();
    }


}
