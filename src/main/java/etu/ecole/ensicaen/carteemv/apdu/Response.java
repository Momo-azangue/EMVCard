package etu.ecole.ensicaen.carteemv.apdu;

public class Response {

    public static final byte SW_ECHEC_PIN = (byte) 9110;
    public static final byte SW_VERIF_PIN = (byte)  9120;
    public static final byte SW_MONTANT_TRANSAC_INVAL =(byte)  9130;
    public static final byte SW_MONTANT_MAX_SOLDE_DEPASSE = (byte) 9140;
    public static final byte SW_SOLDE_NEGATIF = (byte) 9150;
    public static final byte SW_ERROR_CHANGEPIN = (byte) 9160;
    public static final byte SW_CARTE_BLOQUE = (byte) 9170;
    public static final byte SW_WRONG_LE_LENGTH = (byte) 6402;
}
