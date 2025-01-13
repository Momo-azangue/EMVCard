package etu.ecole.ensicaen.carteemv;

import etu.ecole.ensicaen.carteemv.Utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javax.smartcardio.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import static etu.ecole.ensicaen.carteemv.ApduCommand.*;


public class HelloController {

   @FXML
   private  ComboBox itemcombo;
    @FXML
    private Button Readerbutton;
    @FXML
    private Button Atrbutton;
    @FXML
    private TextField AtrField;
    @FXML
    private TextField consulter;
    @FXML
    private TextField debit;
    @FXML
    private TextField credit;

    @FXML
    private Pane pinPane;
    @FXML
    private AnchorPane actionPane;
    @FXML
    private  PasswordField pinCodeField;
    @FXML
    private  Button validatePin;

    @FXML
    private Button consulterLeSolde;
    @FXML
    private Button crediterLeSolde;
    @FXML
    private Button debiterLeSolde;

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

    public TerminalFactory tf;
    public Card card;
    public CardChannel channel;
    public ResponseAPDU response;
    public  boolean isPinVerify;

    public void initialize() {
        //Désactivation des éléments
        AtrField.setDisable(true);
        itemcombo.setDisable(true);
        Readerbutton.setDisable(true);
        Atrbutton.setDisable(true);
         try{
             tf = TerminalFactory.getDefault();
             var ref = new Object() {
                 List<CardTerminal> cardTerminal = null;
             };
             ref.cardTerminal = tf.terminals().list();

             LoadReader(ref.cardTerminal);
             itemcombo.getSelectionModel().selectedItemProperty().addListener((observable,oldvalue, newvalue) -> {
                    Readerbutton.setDisable(false);
                    //je récupère la position et je passe ça en parametre à mon bouton
             });

             /*button de selection du reader*/
             Readerbutton.setOnAction(event ->{

                 String code = "15";
                 byte[] data = Utils.convertirMontantEnBytes(code);
                 for (byte b : data) {
                     System.out.print(String.format("%02x ", b));
                 }
                 System.out.println(Arrays.toString(data));

                 /*Champ de l'ATR*/
                 AtrField.setDisable(false);

                  try {
                             card = ref.cardTerminal.get(0).connect("*");
                             card.getATR();
                             channel = card.getBasicChannel();
                             String ATR = Utils.hexify(card.getATR().getBytes());
                             AtrField.setText(ATR);
                             Atrbutton.setDisable(false);

                         } catch (CardException e) {throw new RuntimeException(e);}

                     }
             );

            /* button de selection de ATR */
            Atrbutton.setOnAction(event ->{
                    pinPane.setVisible(true);
                try {
                    card = ref.cardTerminal.get(0).connect("*");
                    channel = card.getBasicChannel();
                    response = channel.transmit(new CommandAPDU(Command.selectCommand));
                    System.out.println(response.getSW());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            });



            /* restriction sur le format du mot de passe */
            pinCodeField.setTextFormatter(new TextFormatter<String>(change -> {

                String newText = change.getControlNewText();

                if(!newText.matches("\\d*") || newText.length() > 4){
                    return null;
                }
                return change;
            }));

            /*Valider le mot de passe ***/
            validatePin.setOnAction(event -> {
                actionPane.setVisible(true);
                String pin = pinCodeField.getText();

                if(pin.length() == 4){

                    try {
                        isPinVerify = veryPinWithAPDU(pin);
                        showAlert(isPinVerify ? "Succès" : "Erreur", isPinVerify ? "PIN correct !" : "PIN incorrect !");

                       // System.out.println("la valeur du pin est " + pin);
                        System.out.println( "la valeur de la fonction est " + veryPinWithAPDU(pin));

                    }catch (Exception e){
                        e.printStackTrace();
                        showAlert("Erreur","Une erreur s'est produite lors de la vérification.");
                    }
                }else {
                    showAlert("Erreur", "Le PIN doit contenir 4 chiffres.");
                }


            });

            consulterLeSolde.setOnAction(event -> {

                try {
                   if(isPinVerify){
                      card = ref.cardTerminal.get(0).connect("*");
                      channel = card.getBasicChannel();
                      response = channel.transmit(new CommandAPDU(Command.soldeCommand));

                      int sw1 = response.getSW1();
                      int sw2 = response.getSW2();
                      if (sw1 == 0x90 && sw2 == 0x00) {
                          byte[] responseData = response.getData();

                          StringBuilder hexString = new StringBuilder();
                          for (byte b : responseData) {
                              hexString.append(String.format("%02X ", b)); // %02X pour 2 chiffres en hexadécimal
                          }
                          System.out.println("Données en hexadécimal : " + hexString);
                          int decimalPart = 0;
                          for (byte b : responseData) {
                              String hexPart = String.format("%02X", b); // Convertir chaque byte en hexadécimal
                               decimalPart = Integer.parseInt(hexPart, 16); // Convertir chaque byte hexadécimal en décimal
                              System.out.println("Hex : " + hexPart + ", Décimal : " + decimalPart);
                          }

                          String value = String.valueOf(decimalPart);
                          consulter.setText(value);

                      }else {
                          System.out.println("Une erreur " + response.getSW());
                      }
                   }
                } catch (CardException e) {
                    throw new RuntimeException(e);
                }
            });
            crediterLeSolde.setOnAction(event -> {
                String montantCredit = credit.getText();

                try {
                   if(isPinVerify) {
                        card = ref.cardTerminal.get(0).connect("*");
                        channel = card.getBasicChannel();
                        byte[] apduCredit = crediterLeSoldeAPDU(montantCredit);
                        response = channel.transmit(new CommandAPDU(apduCredit));
                        //response = channel.transmit(new CommandAPDU(Command.CreditCommand15));
                        System.out.println("la commande APDU envoyé est : " + Utils.hexify(apduCredit));
                        System.out.println("le solde a été crédité : " + response);
                    }
                } catch (CardException e) {
                    throw new RuntimeException(e);
                }
            });
            debiterLeSolde.setOnAction(event -> {
                String montantDebit = credit.getText();
                try {
                   if(isPinVerify) {
                        card = ref.cardTerminal.get(0).connect("*");
                        channel = card.getBasicChannel();
                        //byte[] apduDebit = debiterLeSoldeAPDU(montantDebit);
                        //response = channel.transmit(new CommandAPDU(apduDebit));
                        response = channel.transmit(new CommandAPDU(Command.DebitCommand));
                        //System.out.println("la commande APDU envoyé est : " + Utils.hexify(apduDebit));
                        System.out.println("la commande APDU envoyé est : " + Utils.hexify(Command.DebitCommand));
                        System.out.println("le solde a été débité : " + response);
                    }
                } catch (CardException e) {
                    throw new RuntimeException(e);
                }
            });


         }catch (javax.smartcardio.CardException e){

             System.out.println("the exception is : "+ e.getMessage());
             e.printStackTrace();
         }


    }







   private void LoadReader(List<CardTerminal> cardTerminal){

        for( int i=0; i < cardTerminal.size(); i++){
            itemcombo.getItems().add(cardTerminal.get(i));
            itemcombo.setDisable(false);
        }

   }

   private  void showAlert(String title, String message){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
   }



}