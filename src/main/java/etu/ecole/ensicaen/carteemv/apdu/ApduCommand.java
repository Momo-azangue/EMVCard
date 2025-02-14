package etu.ecole.ensicaen.carteemv.apdu;

import etu.ecole.ensicaen.carteemv.Utils.Utils;
import javax.smartcardio.*;
import java.util.List;

public class ApduCommand {

    /**
     * create the pin apdu
     * @param pin
     * @return
     */
    public  static byte[] createVerifyPinAPDU(String pin){
        byte[] pinBytes = Utils.convertPinToBytes(pin);
        byte[] apdu = new  byte[5 + pinBytes.length];
        apdu[0] = (byte) 0x90;;
        apdu[1] = (byte) 0x20;
        apdu[2] = (byte) 0x00;
        apdu[3] = (byte) 0x00;
        apdu[4] = (byte) pinBytes.length;
        System.arraycopy(pinBytes, 0,apdu, 5, pinBytes.length);
        return  apdu;
    }

    /**
     * credit Apdu Sold
     * @param montantEntier
     * @return
     */
    public static byte[] crediterLeSoldeAPDU(String montantEntier){
        byte[] montantEntierBytes = Utils.convertirMontantEnBytes(montantEntier);
        return  createAmountApdu((byte) 0x30, montantEntierBytes);
    }

    /**
     *debit Apdu sold
     * @param montantEntier
     * @return
     */
    public static byte[] debiterLeSoldeAPDU(String montantEntier){
        byte[] montantEntierBytes = Utils.convertirMontantEnBytes(montantEntier);
        return  createAmountApdu((byte) 0x40, montantEntierBytes);
    }


    /**
     * creation of a common structure for our two Apdu credit and debit
     * @param instruction
     * @param amountBytes
     * @return
     */
    public static byte[] createAmountApdu(byte instruction, byte[] amountBytes){
        byte[] apdu = new byte[5 + amountBytes.length];
        apdu[0] = (byte) 0x90;
        apdu[1] = instruction;
        apdu[2] = (byte) 0x00;
        apdu[3] = (byte) 0x00;
        apdu[4] = (byte) amountBytes.length;
        System.arraycopy(amountBytes, 0, apdu, 5, amountBytes.length);
        return apdu;
    };


    /**
     * show solde of wallet
     * @return
     * @throws Exception
     */
    public static String Solde() throws Exception {
        CardChannel channel = getCardChannel();
        ResponseAPDU response = channel.transmit(new CommandAPDU(Command.soldeCommand));
       return processSoldeResponse(response);
    }

    /**
     * function to send apdu in higher interface
     * @param apdu
     * @return
     * @throws Exception
     */
    public  static ResponseAPDU SendAPDU(byte[] apdu)throws Exception{
        CardChannel channel = getCardChannel();
        return channel.transmit(new CommandAPDU(apdu));
    };

    /**
     * Verification on our apdu Status
     * @param pin
     * @return
     * @throws Exception
     */
    public static boolean veryPinWithAPDU(String pin) throws  Exception{
        CardChannel channel = getCardChannel();
        byte[] apduCommand = createVerifyPinAPDU(pin);
        ResponseAPDU response = channel.transmit(new CommandAPDU(apduCommand));
        return isSuccessfulResponse(response);
    }

    /**
     * commun channel for all operation
     * @return
     * @throws Exception
     */
    private static CardChannel getCardChannel() throws Exception{
        TerminalFactory terminalFactory = TerminalFactory.getDefault();
        List<CardTerminal> cardTerminals = terminalFactory.terminals().list();
        if (cardTerminals.isEmpty()) {
            throw new Exception("Aucun lecteur de carte détecté");
        }
        CardTerminal terminal = cardTerminals.get(0);
        Card card = terminal.connect("*");
        return card.getBasicChannel();
    }

    /**
     * get the value of the solde
     * @param response
     * @return
     */
    private static String processSoldeResponse(ResponseAPDU response) {
        String value = null;
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
            value = String.valueOf(decimalPart);

        }else {
            System.out.println("Une erreur " + response.getSW());
        }
        return value;
    }

    /**
     * in case of successful response
     * @param response
     * @return
     */
    private static boolean isSuccessfulResponse(ResponseAPDU response) {
        int sw1 = response.getSW1();
        int sw2 = response.getSW2();
        return (sw1 == 0x90 && sw2 == 0x00);
    }

}
