package etu.ecole.ensicaen.carteemv;



import javax.smartcardio.*;

public class SmartCardManager {

    private static CardChannel cardChannel;

    public SmartCardManager()throws Exception{
        TerminalFactory tf = TerminalFactory.getDefault();
        CardTerminal terminal = tf.terminals().list().get(0);
        Card card = terminal.connect("T=1");
        cardChannel = card.getBasicChannel();
        System.out.println("Carte connection established");
        System.out.println(cardChannel);
    }

    public static void  selectApplet() throws Exception{

        byte[] aid = {(byte)0xF2, (byte)0x34, (byte)0x12, (byte)0x34,(byte)0x56, (byte)0x10,(byte)0x00, (byte)0x01};
        CommandAPDU selectCommand = new CommandAPDU(0x00,0xA4,0x04,0x00, aid);
        ResponseAPDU response = cardChannel.transmit(selectCommand);
        System.out.println(" la valeur" + cardChannel);
            checkResponse(response);
    }

    public boolean verify(String pin) throws Exception{

        byte[] pinBytes = pin.getBytes();
        CommandAPDU verifyPinCommand = new CommandAPDU(0x80,0x20,0x00,0x00,pinBytes);
        ResponseAPDU response = cardChannel.transmit(verifyPinCommand);
        return response.getSW() == 0x9000;

    }


    private static void checkResponse(ResponseAPDU response) throws Exception{

        if(response.getSW() != 0x9000){
            throw new Exception("Select application impossible" + "(SW =" + Integer.toHexString(response.getSW()) + ")");
        }
    }
}
