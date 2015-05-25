package AES;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * KeySchedule
 * planeador de llaves de ronda para algorimto de cirfrado y descifrado
 * AES Rijndael
 * @author Alfredo Santamaria
 * @author Laura Chacon
 * @author Carlos Manrique
 */
public class KeySchedule {

    /** llave privada **/
    private byte[][] secretKey;
    /** llave privada actual transformada **/
    private byte[][] currentKey;
    /** numero de ronda **/
    private int round;
    /** indice de ronda **/
    private int indexRound;
    /** indice de ronda inversa **/
    private int indexInverseRound;
    /** lista de las llaves con transformaciones de cada ronda **/
    private ArrayList<byte[][]> roundKeys;
    /** Rcon lookup **/
    private static char[][] Rcon = {
        {0x01, 0x00, 0x00, 0x00},
        {0x02, 0x00, 0x00, 0x00},
        {0x04, 0x00, 0x00, 0x00},
        {0x08, 0x00, 0x00, 0x00},
        {0x10, 0x00, 0x00, 0x00},
        {0x20, 0x00, 0x00, 0x00},
        {0x40, 0x00, 0x00, 0x00},
        {0x80, 0x00, 0x00, 0x00},
        {0x1b, 0x00, 0x00, 0x00},
        {0x36, 0x00, 0x00, 0x00}
    };
    
    /**
     * KeySchedule
     * constructor del planeador de llaves
     * @param statSecretKey llave privada
     */
    public KeySchedule(byte[][] statSecretKey) {
        super();
        
        this.secretKey = new byte[statSecretKey.length][];
        this.currentKey = new byte[statSecretKey.length][];
        for (int i = 0; i < statSecretKey.length; i++) {
            this.secretKey[i] = Arrays.copyOf(statSecretKey[i], 
                                            statSecretKey[i].length);
            this.currentKey[i] = Arrays.copyOf(statSecretKey[i], 
                                            statSecretKey[i].length);
        }
        round = 0;
        indexRound = 0;
        indexInverseRound = 9;
    }
    
    /**
     * getFirstKey
     * retorna la primera llave del planeador
     * @return llave inicial
     */
    public byte[][] getFirstKey() {
        if(roundKeys == null){
            roundKeys = new ArrayList<>();
        
            byte [][] aux = new byte[secretKey.length][];
            for (int i = 0; i < secretKey.length; i++) {
                aux[i] = Arrays.copyOf(secretKey[i], secretKey[i].length);
            }
            roundKeys.add(aux);
            for(int i = 0; i < 10; i++){
                byte [][] next = calculateNextKey();
                byte [][] aux2 = new byte[next.length][];
                for (int j = 0; j < next.length; j++) {
                    aux2[j] = Arrays.copyOf(next[j], next[j].length);
                }
                roundKeys.add(aux2);
            }
        }
        return roundKeys.get(0);
    }
    
    /**
     * getNextKey
     * retorna la siguiente llave con respecto al numero de ronda
     * @param round numero de ronda
     * @return siguiente llave de la ronda ronda
     */
    public byte[][] getNextKey(int round) {
        if(roundKeys == null || round > 10 || round < 0)
            return null;
        return roundKeys.get(round);
    }
    
    /**
     * getFirstKeyInverese
     * retorna la primaera llave inversa
     * @return primera llave inversa
     */
    public byte[][] getFirstKeyInverese() {
         if(roundKeys == null){
            roundKeys = new ArrayList<>();
        
            byte [][] aux = new byte[secretKey.length][];
            for (int i = 0; i < secretKey.length; i++) {
                aux[i] = Arrays.copyOf(secretKey[i], secretKey[i].length);
            }
            roundKeys.add(aux);
            for(int i = 0; i < 10; i++){
                byte [][] next = calculateNextKey();
                byte [][] aux2 = new byte[next.length][];
                for (int j = 0; j < next.length; j++) {
                    aux2[j] = Arrays.copyOf(next[j], next[j].length);
                }
                roundKeys.add(aux2);
            }
        }
        
        return roundKeys.get(roundKeys.size()-1);
    }
    
    /**
     * getNextKeyInverse
     * retorna la siguiente llave inversa dada la ronda
     * @param round numero de ronda
     * @return siguiente llave de la ronda ronda
     */
    public byte[][] getNextKeyInverse(int round) {
        if(roundKeys == null || round > 10 || round < 0)
            return null;
        return roundKeys.get(10 - round);
    }

    /**
     * calculateNextKey
     * hace el calculo de la siguiente llave
     * @return siguiete llave calculada
     */
    public byte[][] calculateNextKey() {
        byte[] rotWord = new byte[4];
        for (int i = 0; i < 4; i++) {
            rotWord[i] = currentKey[i][3];
        }
        
        
        shift(rotWord);
        
        
        subBytes(rotWord);
        
        
        reconXor(rotWord);
        
        round++;
        return currentKey;
    }

    /**
     * shift
     * transformacion shift
     * @param arr columna transformada con shift
     */
    private void shift(byte[] arr) {
        byte aux = arr[0];
        arr[0] = arr[1];
        arr[1] = arr[2];
        arr[2] = arr[3];
        arr[3] = aux;
    }

    /**
     * subBytes
     * transformacion subBytes
     * @param arr coluimna transformada con subBytes
     */
    private void subBytes(byte[] arr) {
        int x, y;
        byte curr;
        for (int j = 0; j < 4; j++) {
            curr = arr[j];

            x = (curr & 0xF0) >> 4; //mask: 11110000
            y = curr & 0x0F;        //mask: 00001111

            arr[j] = Rijndael_Sbox.get(x, y);
        }
    }

    /**
     * reconXor
     * transformacion reconXor
     * transforma la llave actual dada la primera
     * @param arr columna que se usa para la transformacion
     */
    private void reconXor(byte[] arr) {
       
        for (int j = 0; j < 4; j++) {
            currentKey[j][0] = (byte) (currentKey[j][0] ^ arr[j] ^ Rcon[round][j]);
        }
        
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                currentKey[j][i] = (byte) (currentKey[j][i] ^ currentKey[j][i - 1]);
            }
        }
    }

    /**
     * generateKey
     * genera una llave priada con KeyGenerator de la libreria
     * javax.crypto
     * @return llave privada
     */
    public static byte[][] generateKey() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        keyGen.init(128);
        SecretKey secretK = keyGen.generateKey();
        byte[][] secretKeyBytes = new byte[4][4];
        int x = 0;
        int ii = -1, jj = 0;
        for (byte b : secretK.getEncoded()) {
            if ((x++) % 4 == 0) {
                ii++;
                jj = 0;
            }
            secretKeyBytes[ii][jj] = b;
            jj++;
        }

        return secretKeyBytes;
    }

    /**
     * printRow
     * imprime una fila
     * @param arr fila a imprimir
     */
    private void printRow(byte[] arr) {
        System.out.println("------------");
        for (int j = 0; j < 4; j++) {
            System.out.print(toHex(arr[j]) + " ");
        }
        System.out.println("/n---------------");
    }

    /**
     * printKey
     * imprime la llava actual en formato hexadecimal
     */
    private void printKey() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(toHex(currentKey[i][j]) + " ");
            }
            System.out.println();
        }
    }
    
    /**
     * toHex
     * conversion de byte a formato hexadecimal
     * @param b byte a convertir
     * @return string con formato hexadecimal de b
     */
    private String toHex(byte b) {
        return String.format("0x%02X", (b & 0xFF));
    }
}
