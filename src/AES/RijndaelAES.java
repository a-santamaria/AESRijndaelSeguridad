package AES;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class RijndaelAES {

    private String text;
    private byte[][] block;
    private int index;
    private SecretKey secretKey;
    private byte[][] secretKeyBytes;
    private KeySchedule keySchedule;
    byte[] textbytes;

    public RijndaelAES(byte[] textbytes, byte[][] secretKeybytes) {
        super();

        this.textbytes = textbytes;
        this.secretKeyBytes = secretKeybytes;
        index = 0;
        block = new byte[4][4];
        keySchedule = new KeySchedule(secretKeyBytes);
    }

    private void generateKey() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        keyGen.init(128);
        secretKey = keyGen.generateKey();
    }

    public byte[] encrypt() {
        index = 0;
        if (textbytes.length == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        int num = (textbytes.length / 16);
        if (textbytes.length % 16 != 0) {
            num += 1;
        }
        byte[] salida = new byte[16 * (num)];

        //thread ??
        int index = 0;
        while (hasNextBlock()) {
            newBlock();
            encryptBlock();

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    sb.append((char) block[i][j]);
                    salida[index++] = block[i][j];
                }
            }

        }
        /*System.out.println("------------------salida");
         for(byte b : salida){
         System.out.println(toHex(b));
         }*/
        return salida;
    }

    private void encryptBlock() {
        System.out.println("---------encriptBlock");
        printBlock();
        //addRoundKey with initial key
        addRoundKey(keySchedule.getKey());

        for (int round = 0; round < 9; round++) {
            subBytes();
            shiftRows();
            mixColumns();
            addRoundKey(keySchedule.getNextKey());
        }

        subBytes();
        shiftRows();
        addRoundKey(keySchedule.getNextKey());

        printBlock();
        System.out.println("-------------end EncriptBlock");

    }
    
    public byte[] decrypt(byte[] encriptedBytes, byte[][] key) {
        KeySchedule keySchedule = new KeySchedule(key);
        byte[] salida = new byte[encriptedBytes.length];
        textbytes = encriptedBytes;
        //thread ??
        int index = 0;
        while (hasNextBlock()) {
            newBlock();
            decryptBlock();

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    salida[index++] = block[i][j];
                }
            }

        }
        return salida;
    }
    
    
    private void decryptBlock() {
        System.out.println("---------decryptBlock");
        printBlock();
        //addRoundKey with initial key
        addRoundKey(keySchedule.getKey());

        for (int round = 0; round < 9; round++) {
            inverseSubBytes();
            inverseShiftRows();
            mixColumns();
            addRoundKey(keySchedule.getNextKey());
        }

        subBytes();
        shiftRows();
        addRoundKey(keySchedule.getNextKey());

        printBlock();
        System.out.println("-------------end EncriptBlock");

    }   
    
    

    /**
     * newBlock fills block with next bytes in the byte array
     */
    private void newBlock() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (index < textbytes.length) {
                    block[i][j] = (textbytes[index++]);
                } else {
                    block[i][j] = 0;
                }
            }
        }
    }

    /**
     * hasNextBlock returns if byte array has bytes left for a new block
     */
    private boolean hasNextBlock() {
        if (index >= textbytes.length) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * subBytes 1st transformation of Rijndael
     */
    private void subBytes() {

        byte curr;
        for (int i = 0; i < 4; i++) {
            subBytes(block[i]);
        }
    }

    private void subBytes(byte[] arr) {
        int x, y;
        byte curr;
        for (int j = 0; j < 4; j++) {
            curr = arr[j];

            x = (curr & 0xF0) >> 4; //mask: 11110000
            y = curr & 0x0F; 		//mask: 00001111

            arr[j] = Rijndael_Sbox.get(x, y);
        }
    }

    /**
     * inversesubBytes 1st transformation of Rijndael
     * Decrypt
     */
    private void inverseSubBytes() {

        byte curr;
        for (int i = 0; i < 4; i++) {
            subBytes(block[i]);
        }
    }

    private void inverseSubBytes(byte[] arr) {
        int x, y;
        byte curr;
        for (int j = 0; j < 4; j++) {
            curr = arr[j];

            x = (curr & 0xF0) >> 4; //mask: 11110000
            y = curr & 0x0F; 		//mask: 00001111

            arr[j] = Rijndael_Sbox.getInverse(x, y);
        }
    }
    
    
    /**
     * shiftRows 2nd transformation of Rijndael
     */
    private void shiftRows() {
        byte aux;

        aux = block[1][0];
        block[1][0] = block[1][1];
        block[1][1] = block[1][2];
        block[1][2] = block[1][3];
        block[1][3] = aux;

        aux = block[2][0];
        block[2][0] = block[2][2];
        block[2][2] = aux;
        aux = block[2][1];
        block[2][1] = block[2][3];
        block[2][3] = aux;

        aux = block[3][3];
        block[3][3] = block[3][2];
        block[3][2] = block[3][1];
        block[3][1] = block[3][0];
        block[3][0] = aux;

    }
    
    
     /**
     * InverseShiftRows 2nd transformation of Rijndael
     * Decrypt
     */
    private void inverseShiftRows() {
        byte aux;

        aux = block[1][3];
        block[1][3] = block[1][2];
        block[1][2] = block[1][1];
        block[1][1] = block[1][0];
        block[1][0] = aux;

        aux = block[2][0];
        block[2][0] = block[2][2];
        block[2][2] = aux;
        aux = block[2][1];
        block[2][1] = block[2][3];
        block[2][3] = aux;

        aux = block[3][0];
        block[3][0] = block[3][1];
        block[3][1] = block[3][2];
        block[3][2] = block[3][3];
        block[3][3] = aux;
    }

    /**
     * MixColumns 3rd transformation of Rijndael
     *
     * The array 'a' is simply a copy of the input array 'r' The array 'b' is
     * each element of the array 'a' multiplied by 2 in Rijndael's Galois field
     * a[n] ^ b[n] is element n multiplied by 3 in Rijndael's Galois field 
     *
     */
    private void mixColumns() {
        byte[] r = new byte[4];
        byte[] a = new byte[4];
        byte[] b = new byte[4];
        byte c;
        byte h;
        for (int i = 0; i < 4; i++) {

            //get col i of block
            for (int j = 0; j < 4; j++) {
                a[j] = block[j][i];
            }
            r[0] = (byte) (GMult.getLookUp2(a[0]) ^ GMult.getLookUp3(a[1]) ^ a[2] ^ a[3]);
            r[1] = (byte) (a[0] ^ GMult.getLookUp2(a[1]) ^ GMult.getLookUp3(a[2]) ^ a[3]);
            r[2] = (byte) (a[0] ^ a[1] ^ GMult.getLookUp2(a[2]) ^ GMult.getLookUp3(a[3]));
            r[3] = (byte) (GMult.getLookUp3(a[0]) ^ a[1] ^ a[2] ^ GMult.getLookUp2(a[3]));

            //fill col i of block with transform
            for (int j = 0; j < 4; j++) {
                block[j][i] = r[j];
            }
        }
    }

    private void addRoundKey(byte[][] RoundKey) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                block[i][j] ^= RoundKey[i][j];
            }
        }
    }

    /**
     * printBlock pirnt block in hex format
     */
    private void printBlock() {
        System.out.println("-----------------------");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(toHex(block[i][j]) + " ");
            }
            System.out.println();
        }
        System.out.println("-----------------------");
    }

    private String toHex(char c) {
        return String.format("0x%02X", (int) c);
    }

    private String toHex(int x) {
        return String.format("0x%02X", x);
    }

    private String toHex(byte b) {
        return String.format("0x%02X", (b & 0xFF));
    }
}
