package AES;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeySchedule {

    private byte[][] secretKey;
    private byte[][] currentKey;
    private int round;
    private int indexRound;
    private int indexInverseRound;
    private ArrayList<byte[][]> roundKeys;
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
    

    public KeySchedule(byte[][] statSecretKey) {
        super();
        /*
        char[][] testKey = {
            {0x2b, 0x28, 0xab, 0x09},
            {0x7e, 0xae, 0xf7, 0xcf},
            {0x15, 0xd2, 0x15, 0x4f},
            {0x16, 0xa6, 0x88, 0x3c}
        };
        

        this.currentKey = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                currentKey[i][j] = (byte) testKey[i][j];
            }
        }
        secretKey = currentKey;
        
        */
        
        this.secretKey = new byte[statSecretKey.length][];
        this.currentKey = new byte[statSecretKey.length][];
        for (int i = 0; i < statSecretKey.length; i++) {
            this.secretKey[i] = Arrays.copyOf(statSecretKey[i], 
                                            statSecretKey[i].length);
            this.currentKey[i] = Arrays.copyOf(statSecretKey[i], 
                                            statSecretKey[i].length);
        }
        //this.secretKey = this.currentKey = statSecretKey;
        System.out.println("______________Entre con key______________");
        printKey();
        System.out.println("____________________________________");
        round = 0;
        indexRound = 0;
        indexInverseRound = 9;
    }

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
    
    public byte[][] getNextKey() {
        if(roundKeys == null || indexRound >= roundKeys.size())
            return null;
        return roundKeys.get(++indexRound);
    }
    
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
    
    public byte[][] getNextKeyInverse() {
        if(roundKeys == null || indexInverseRound < 0)
            return null;
        return roundKeys.get(indexInverseRound--);
    }

    public byte[][] calculateNextKey() {
        byte[] rotWord = new byte[4];
        for (int i = 0; i < 4; i++) {
            rotWord[i] = currentKey[i][3];
        }
        
        
        shift(rotWord);
        
        //System.out.println("-----shift");
        //printRow(rotWord);
        
        subBytes(rotWord);
        
        //System.out.println("-----subBytes");
        //printRow(rotWord);
        
        reconXor(rotWord);
        //System.out.println("-----Xor");
        //printKey();
        //System.out.println("round"+ round);
        //System.out.println("Round key");
        //printKey();
        round++;
        return currentKey;
    }

    private void shift(byte[] arr) {
        byte aux = arr[0];
        arr[0] = arr[1];
        arr[1] = arr[2];
        arr[2] = arr[3];
        arr[3] = aux;
    }

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

    private void printRow(byte[] arr) {
        System.out.println("------------");
        for (int j = 0; j < 4; j++) {
            System.out.print(toHex(arr[j]) + " ");
        }
        System.out.println("/n---------------");
    }

    private void printKey() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(toHex(currentKey[i][j]) + " ");
            }
            System.out.println();
        }
    }

    private String toHex(byte b) {
        return String.format("0x%02X", (b & 0xFF));
    }
}
