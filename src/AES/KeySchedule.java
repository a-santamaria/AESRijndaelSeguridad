package AES;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeySchedule {

    private byte[][] secretKey;
    private byte[][] currentKey;
    private int round;
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
        char[][] testKey = {
            {0x2b, 0x28, 0xab, 0x09},
            {0x7e, 0xae, 0xf7, 0xcf},
            {0x15, 0xd2, 0x15, 0x4f},
            {0x16, 0xa6, 0x88, 0x3c}
        };
		//this.secretKey = this.currentKey = statSecretKey;

        this.currentKey = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                currentKey[i][j] = (byte) testKey[i][j];
            }
        }
        secretKey = currentKey;
        round = 0;
    }

    public byte[][] getKey() {
        return secretKey;
    }

    public byte[][] getNextKey() {
        byte[] rotWord = new byte[4];
        for (int i = 0; i < 4; i++) {
            rotWord[i] = currentKey[i][3];
        }

        shift(rotWord);

        subBytes(rotWord);
        
        reconXor(rotWord);

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
        for (int j = 0; j < 4; j++) {
            System.out.print(toHex(arr[j]) + " ");
        }
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
