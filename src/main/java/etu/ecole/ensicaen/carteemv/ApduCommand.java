package etu.ecole.ensicaen.carteemv;

import etu.ecole.ensicaen.carteemv.Utils.Utils;

import javax.smartcardio.*;
import java.util.List;

public class ApduCommand {


    public static boolean veryPinWithAPDU(String pin) throws  Exception{

        TerminalFactory terminalFactory = TerminalFactory.getDefault();
        List<CardTerminal> cardTerminals = terminalFactory.terminals().list();

        if(cardTerminals.isEmpty()){
            throw  new Exception("Aucun lecteur de carte détecté");
        }

        CardTerminal terminal = cardTerminals.get(0);
       Card card = terminal.connect("*"); // pour tout les protocoles de transmissions
        CardChannel channel = card.getBasicChannel();
        //commande APDU pour vérifier le PIN
        byte[] apduCommand = createVerifyPinAPDU(pin);
        ResponseAPDU response = channel.transmit(new CommandAPDU(apduCommand));

        int sw1 = response.getSW1();
        int sw2 = response.getSW2();

        System.out.println("la commande APDU envoyé est : " + Utils.hexify(apduCommand));
        System.out.println("la réponse du lecteur : "+ response);

        return (sw1 == 0x90 && sw2 == 0x00);

    }



    public  static byte[] createVerifyPinAPDU(String pin){
        // longueur des données
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


    public static byte[] crediterLeSoldeAPDU(String montantEntier){

        // Convertir le montant entier en décimal

        byte[] montantEntierBytes = Utils.convertirMontantEnBytes(montantEntier);
        byte Lc = (byte) montantEntierBytes.length;
        byte[] apdu = new  byte[5 + montantEntierBytes.length];
        apdu[0] = (byte) 0x90;;
        apdu[1] = (byte) 0x30;
        apdu[2] = (byte) 0x00;
        apdu[3] = (byte) 0x00;
        apdu[4] =  Lc;
        System.arraycopy(montantEntierBytes, 0,apdu, 5, montantEntierBytes.length);

        return  apdu;

    }

    public  static byte[] debiterLeSoldeAPDU(String montantEntier){

        byte[] montantEntierBytes = Utils.convertirMontantEnBytes(montantEntier);
        byte Lc = (byte) montantEntierBytes.length;
        byte[] apdu = new  byte[5 + montantEntierBytes.length];
        apdu[0] = (byte) 0x90;;
        apdu[1] = (byte) 0x40;
        apdu[2] = (byte) 0x00;
        apdu[3] = (byte) 0x00;
        apdu[4] = Lc;
        System.arraycopy(montantEntierBytes, 0,apdu, 5, montantEntierBytes.length);

        return  apdu;

    }

}
