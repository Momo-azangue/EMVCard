package etu.ecole.ensicaen.carteemv.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class Utils {
    public static String hexify(byte[] bytes) {
        var bytesStrings = new ArrayList<String>(bytes.length);
        for (var b : bytes) {
            bytesStrings.add(String.format("%02X", b));
        }
        return String.join(" ", bytesStrings);
    }

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


    public static byte[] convertPinToBytes(String pin) {
        byte[] pinBytes = new byte[pin.length()];
        for (int i = 0; i < pin.length(); i++) {
            pinBytes[i] = (byte) Integer.parseInt(pin.substring(i, i + 1), 16);
        }
        return pinBytes;
    }




    public static byte[] convertirMontantEnBytes(String montant) {
        try {
            // Convertir la valeur du montant en entier
            int valeur = Integer.parseInt(montant);

            // Convertir l'entier en chaîne hexadécimale
            String hexString = Integer.toHexString(valeur).toUpperCase();

            // Ajouter un zéro non significatif si la longueur est impaire
            if (hexString.length() % 2 != 0) {
                hexString = "0" + hexString;
            }

            // Créer un tableau de bytes à partir de la chaîne hexadécimale
            byte[] bytes = new byte[hexString.length() / 2];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
            }

            // Ajouter 0x00 au début des bytes pour respecter le format
            byte[] result = new byte[bytes.length + 1];
            result[0] = 0x00; // Ajout du zéro non significatif
            System.arraycopy(bytes, 0, result, 1, bytes.length);

            return result;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Le montant doit être un nombre valide !");
        }
    }





}