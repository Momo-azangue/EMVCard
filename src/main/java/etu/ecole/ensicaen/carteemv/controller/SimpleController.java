package etu.ecole.ensicaen.carteemv.controller;

import etu.ecole.ensicaen.carteemv.Utils.Utils;
import etu.ecole.ensicaen.carteemv.apdu.ApduCommand;
import etu.ecole.ensicaen.carteemv.apdu.Command;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javax.smartcardio.*;
import java.util.List;
import static etu.ecole.ensicaen.carteemv.apdu.ApduCommand.*;


public class SimpleController {

    private TerminalFactory tf;
    private Card card;
    private CardChannel channel;
    private ResponseAPDU response;
    private boolean isPinVerify;
    private List<CardTerminal> cardTerminals;


    @FXML
    private  ComboBox<CardTerminal> cardTerminalComboBox;
    @FXML
    private Button Readerbutton;
    @FXML
    private Button Atrbutton;
    @FXML
    private TextField AtrField;
    @FXML
    private TextField consulterTextfield;
    @FXML
    private TextField debitTextfield;
    @FXML
    private TextField creditTextfield;
    @FXML
    private Pane pinPane;
    @FXML
    private AnchorPane actionPane;
    @FXML
    private  PasswordField pinCodeField;
    @FXML
    private  Button validatePin;
    @FXML
    private Button creditSoldButton;
    @FXML
    private Button debitSoldButton;
    @FXML
    private Button refreshReaderButton;


    public void initialize() {
        componentsfirstState();
        loadCardTerminals();
        setupEventHandlers();

    }

    /**
     * fonction to set up all UI components
     * for the first time, no parameter
     */
    private void componentsfirstState(){
        AtrField.setDisable(true);
        cardTerminalComboBox.setDisable(true);
        Readerbutton.setDisable(true);
        Atrbutton.setDisable(true);
        pinPane.setVisible(false);
        actionPane.setVisible(false);
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

    /**
     * fonction to set up the button on the application
     * and observation of value on the interface
     */
    private void setupEventHandlers(){
        cardTerminalComboBox.getSelectionModel().selectedItemProperty().addListener((observable,odlValue,newValue) ->{
            Readerbutton.setDisable(newValue == null);
        });

        Readerbutton.setOnAction(event -> handleReaderButtonAction());
        Atrbutton.setOnAction(event -> handleAtrButtonAction());
        validatePin.setOnAction(event -> handleValidatePinButtonAction());
        creditSoldButton.setOnAction(event -> handleCreditSoldButtonAction());
        debitSoldButton.setOnAction(event -> handleDebitSoldButtonAction());
        refreshReaderButton.setOnAction(event -> setRefreshReaderButton());

        pinCodeField.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if(newText.matches("\\d*")){
                return change;
            }
            return null;
        }));
    }

    /**
     * handle of reader button to do action
     * on the reader
     */
    private void handleReaderButtonAction(){
        try {
            CardTerminal selectedTerminal = cardTerminalComboBox.getValue();
            if (selectedTerminal != null && selectedTerminal.isCardPresent()){
                card = selectedTerminal.connect("*");
                String ATR = Utils.hexify(card.getATR().getBytes());
                AtrField.setText(ATR);
                AtrField.setDisable(false);
                Atrbutton.setDisable(false);
            }else{
                showAlert("No card present", "veuillez inserer une carte s'iil vous plait");
            }
        } catch (CardException e) {
            showAlert("Erreur", "Erreur lors de la connexion à la carte : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * handle of the button who show Atr
     * creation of channel
     * transmission of the select command
     * to start dialog  with the ICC
     */
    private void handleAtrButtonAction(){
        try {
            pinPane.setVisible(true);
            card = cardTerminalComboBox.getValue().connect("*");
            channel = card.getBasicChannel();
            response = channel.transmit(new CommandAPDU(Command.selectCommand));
        } catch (CardException e) {
            showAlert("Erreur", "Erreur lors de la transmission de la commande APDU : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * fonction to validate and control the pin
     *
     */
    private void handleValidatePinButtonAction(){
            actionPane.setVisible(true);
            String pin = pinCodeField.getText();
            if(pin.length() == 4){
                try{
                    isPinVerify = veryPinWithAPDU(pin);
                    showAlert(isPinVerify ? "Succès" : "Erreur", isPinVerify ? "PIN correct !" : "PIN incorrect !");
                    if(isPinVerify){
                        consulterTextfield.setText(ApduCommand.Solde());
                    }
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de la vérification du PIN : " + e.getMessage());
                    e.printStackTrace();
                }
            }else{
                showAlert("Erreur", "Le PIN doit contenir 4 chiffres. ");
            }
        }

    /**
     * fonction to credit the sold wallet
     */
    private void handleCreditSoldButtonAction(){
            if(isPinVerify){
                String montantCredit = creditTextfield.getText();
                try {
                    byte[] apduCredit = crediterLeSoldeAPDU(montantCredit);
                    response = channel.transmit(new CommandAPDU(apduCredit));
                    consulterTextfield.setText(ApduCommand.Solde());
                } catch (CardException e) {
                    showAlert("Erreur", "Erreur lors du crédit du solde : " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    /**
     * fonction to debit the sold wallet
     */
    private void handleDebitSoldButtonAction(){
        if(isPinVerify){
            String montantDebit = debitTextfield.getText();
            try {
                byte[] apduDebit = debiterLeSoldeAPDU(montantDebit);
                response = channel.transmit(new CommandAPDU(apduDebit));
                consulterTextfield.setText(ApduCommand.Solde());
            } catch (CardException e) {
                showAlert("Erreur", "Erreur lors du débit du solde : " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * fonction to refresh reader in application
     */
    private void setRefreshReaderButton(){
            AtrField.clear();
            cardTerminalComboBox.getItems().clear();
            Atrbutton.setDisable(true);
            pinPane.setVisible(false);
            actionPane.setVisible(false);
            loadCardTerminals();
        }

    /**
     * fonction for alert user
     * @param title
     * @param message
     */
    private  void showAlert(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}



