package etu.ecole.ensicaen.carteemv.Utils;
import java.util.*;


public class Utils {

    /**
     * show the hexadecimal value
     * @param bytes
     * @return
     */
    public static String hexify(byte[] bytes) {
        var bytesStrings = new ArrayList<String>(bytes.length);
        for (var b : bytes) {
            bytesStrings.add(String.format("%02X", b));
        }
        return String.join(" ", bytesStrings);
    }

    /**
     *
     * @param s
     * @return
     */
    public static byte[] convertHexStringToByteArray(String s) {
        final int len = s.length();
        final int rem = len % 2;

        byte[] ret = new byte[len / 2 + rem];

        if (rem != 0) {
            try {
                ret[0] = (byte) (Integer.parseInt(s.substring(0, 1), 16) & 0x00F);
            } catch (Exception e) {
                ret[0] = 0;
            }
        }

        for (int i = rem; i < len; i += 2) {
            try {
                ret[i / 2 + rem] = (byte) (Integer.parseInt(s.substring(i, i + 2), 16) & 0x0FF);
            } catch (Exception e) {
                ret[i / 2 + rem] = 0;
            }
        }

        return ret;
    }

    /**
     * Convert pin string to pinbytes
     * @param pin
     * @return
     */
    public static byte[] convertPinToBytes(String pin) {
        byte[] pinBytes = new byte[pin.length()];
        for (int i = 0; i < pin.length(); i++) {
            pinBytes[i] = (byte) Integer.parseInt(pin.substring(i, i + 1), 16);
        }
        return pinBytes;
    }


    /**
     * conversion of amount in hexadecimal value
     * @param montant
     * @return
     */
    public static byte[] convertirMontantEnBytes(String montant) {
        try {
            int valeur = Integer.parseInt(montant);
            String hexString = Integer.toHexString(valeur).toUpperCase();
            if (hexString.length() % 2 != 0) {
                hexString = "0" + hexString;
            }
            byte[] bytes = new byte[hexString.length() / 2];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
            }
            byte[] result = new byte[bytes.length + 1];
            result[0] = 0x00; // Ajout du zéro non significatif
            System.arraycopy(bytes, 0, result, 1, bytes.length);
            return result;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Le montant doit être un nombre valide !");
        }
    }

}