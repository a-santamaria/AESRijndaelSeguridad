package AES;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class RijndaelAES {

    private static String text;
    private static byte[][] block;
    private static int index;
    private static KeySchedule keySchedule;
    private static byte[] textBytes;

    /*public RijndaelAES(byte[] textbytes, byte[][] secretKeybytes) {
        super();

        this.textbytes = textbytes;
        this.secretKeyBytes = secretKeybytes;
        index = 0;
        block = new byte[4][4];
        keySchedule = new KeySchedule(secretKeyBytes);
    }*/

    public static byte[] encrypt(byte[] textbytes, byte[][] secretKeybytes) {
        textBytes = textbytes;
        index = 0;
        block = new byte[4][4];
        keySchedule = new KeySchedule(secretKeybytes);
        
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

    private static void encryptBlock() {
        System.out.println("---------encriptBlock");
        //printBlock();
        //addRoundKey with initial key
        addRoundKey(keySchedule.getFirstKey());
        //System.out.println("addroundkey");
        //printBlock();
        int round = 1;
        for (; round < 10; round++) {
            //System.out.println("round "+ round);
            subBytes();
            //System.out.println("subBytes");
            //printBlock();
            shiftRows();
            //System.out.println("shiftRows");
            //printBlock();
            mixColumns();
            //System.out.println("mixcolumns");
            //printBlock();
            addRoundKey(keySchedule.getNextKey(round));
            //System.out.println("addRoundKey");
            //printBlock();
        }
        subBytes();
        //System.out.println("subBytes");
        //printBlock();
        shiftRows();
        //System.out.println("shiftRows");
        //printBlock();
        addRoundKey(keySchedule.getNextKey(round));
        //System.out.println("addRoundKey");
        //printBlock();

        printBlock();
        System.out.println("-------------end EncriptBlock");

    }
    
    public static byte[] decrypt(byte[] encriptedBytes, byte[][] secretKeyBytes) {
        index = 0;
        block = new byte[4][4];
        keySchedule = new KeySchedule(secretKeyBytes);
        
        byte[] salida = new byte[encriptedBytes.length];
        textBytes = encriptedBytes;
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
    
    
    private static void decryptBlock() {
        System.out.println("---------decryptBlock");
        //printBlock();
        //addRoundKey with initial key
        addRoundKey(keySchedule.getFirstKeyInverese());
        //System.out.println("addroundkey");
        //printBlock();
        int round = 1;
        for (; round < 10; round++) {
            //System.out.println("round "+round);
            inverseShiftRows();
            //System.out.println("inverse shift");
            //printBlock();
            inverseSubBytes();
            //System.out.println("inverese sub bytes");
            //printBlock();
            addRoundKey(keySchedule.getNextKeyInverse(round)); 
            //System.out.println("inverse add round key");
            //printBlock();
            inverseMixColumns();    
            //System.out.println("inverse mix cols");
            //printBlock();
        }

        inverseShiftRows();
        //System.out.println("inverse shift");
        //printBlock();
        inverseSubBytes();
        //System.out.println("inverse sub bytes");
        //printBlock();
        addRoundKey(keySchedule.getNextKeyInverse(round));
        //System.out.println("inverse add round key");
        //printBlock();

        printBlock();
        System.out.println("-------------end EncriptBlock");

    }   
    
    

    /**
     * newBlock fills block with next bytes in the byte array
     */
    private static void newBlock() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (index < textBytes.length) {
                    block[i][j] = (textBytes[index++]);
                } else {
                    block[i][j] = 0;
                }
            }
        }
    }

    /**
     * hasNextBlock returns if byte array has bytes left for a new block
     */
    private static boolean hasNextBlock() {
        if (index >= textBytes.length) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * subBytes 1st transformation of Rijndael
     */
    private static void subBytes() {

        for (int i = 0; i < 4; i++) {
            subBytes(block[i]);
        }
    }

    private static void subBytes(byte[] arr) {
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
    private static void inverseSubBytes() {

        for (int i = 0; i < 4; i++) {
            inverseSubBytes(block[i]);
        }
    }

    private static void inverseSubBytes(byte[] arr) {
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
    private static void shiftRows() {
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
    private static void inverseShiftRows() {
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
     * uses Galois Multiplication lookup tables
     * for de multiplications
     * and xor (^) for addition 
     * 
     * b_0 = 2a_0 + 3a_1 + 1a_2 + 1a_3
     * b_1 = 1a_0 + 2a_1 + 3a_2 + 1a_3
     * b_2 = 1a_0 + 1a_1 + 2a_2 + 3a_3
     * b_3 = 3a_0 + 1a_1 + 1a_2 + 2a_3
     * 
     *
     */
    private static void mixColumns() {
        
        for (int i = 0; i < 4; i++) {
            byte[] r = new byte[4];
            byte[] a = new byte[4];
            byte[] b = new byte[4];
            byte c;
            byte h;
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
    
    
    /**
     * InverseMixColumns 3rd transformation of Rijndael
     * Decrypt
     *
     * uses Galois Multiplication lookup tables
     * for de multiplications
     * and xor (^) for addition 
     * 
     * r_0 = 14a_0 + 11a_1 + 13a_2 +  9a_3
     * r_1 =  9a_0 + 14a_1 + 11a_2 + 13a_3
     * r_2 = 13a_0 +  9a_1 + 14a_2 + 11a_3
     * r_3 = 11a_0 + 13a_1 +  9a_2 + 14a_3
     * 
     *
     */
    private static void inverseMixColumns() {
        
        for (int i = 0; i < 4; i++) {
            byte[] r = new byte[4];
            byte[] a = new byte[4];
            byte[] b = new byte[4];
            byte c;
            byte h;
            //get col i of block
            for (int j = 0; j < 4; j++) {
                a[j] = block[j][i];
            }
            r[0] = (byte) (GMult.getLookUp14(a[0]) ^ GMult.getLookUp11(a[1]) ^ 
                           GMult.getLookUp13(a[2]) ^ GMult.getLookUp9(a[3]));
            
            r[1] = (byte) (GMult.getLookUp9(a[0]) ^ GMult.getLookUp14(a[1]) ^ 
                           GMult.getLookUp11(a[2]) ^ GMult.getLookUp13(a[3]));
            
            r[2] = (byte) (GMult.getLookUp13(a[0]) ^ GMult.getLookUp9(a[1]) ^ 
                           GMult.getLookUp14(a[2]) ^ GMult.getLookUp11(a[3]));
            
            r[3] = (byte) (GMult.getLookUp11(a[0]) ^ GMult.getLookUp13(a[1]) ^ 
                           GMult.getLookUp9(a[2]) ^ GMult.getLookUp14(a[3]));

            //fill col i of block with transform
            for (int j = 0; j < 4; j++) {
                block[j][i] = r[j];
            }
        }
    }

    private static void addRoundKey(byte[][] RoundKey) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                block[i][j] ^= RoundKey[i][j];
            }                
        }
    }

    /**
     * printBlock pirnt block in hex format
     */
    private static void printBlock() {
        System.out.println("-----------------------");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(toHex(block[i][j]) + " ");
            }
            System.out.println();
        }
        System.out.println("-----------------------");
    }

    private static String toHex(char c) {
        return String.format("0x%02X", (int) c);
    }

    private static String toHex(int x) {
        return String.format("0x%02X", x);
    }

    private static String toHex(byte b) {
        return String.format("0x%02X", (b & 0xFF));
    }
}
