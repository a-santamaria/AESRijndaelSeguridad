package AES;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * RijndaelAES
 * Algoritmos de cifrado y descifrado Advanced Encryption Standard 
 * Rijndael
 * @author Alfredo Santamaria
 * @author Laura Chacon
 * @author Carlos Manrique
 */
public class RijndaelAES {

    /** texto **/
    private static String text;
    /** bloque de 16 bytes de datos **/
    private static byte[][] block;
    /** indice **/
    private static int index;
    /** planeador de llaves **/
    private static KeySchedule keySchedule;
    /** datos en formato de bytes **/
    private static byte[] textBytes;

    /**
     * encrypt
     * cifra los datos con la llave privada dada por parametro
     * con el algorithmo AES Rijndael
     * 
     * @param textbytes datos a ser cifrados
     * @param secretKeybytes llave privada de 128 bits
     * @return arreglo de bytes de los datos encriptados
     */
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
        return salida;
    }

    /**
     * encryptBlock
     * cifra un bloque de 16 bytes
     */
    private static void encryptBlock() {
        //System.out.println("---------encriptBlock");
        //printBlock();
        //addRoundKey with initial key
        addRoundKey(keySchedule.getFirstKey());
        int round = 1;
        for (; round < 10; round++) {
            //System.out.println("round "+ round);
            subBytes();
            shiftRows();
            mixColumns();
            addRoundKey(keySchedule.getNextKey(round));
        }
        subBytes();
        shiftRows();
        addRoundKey(keySchedule.getNextKey(round));

    }
    
    
    /**
     * decrypt
     * descifra los datos recividos con la llave privada que llega por
     * parametro 
     * 
     * @param encriptedBytes datos a ser descifrados
     * @param secretKeyBytes llave privada
     * @return arreglo de bytes con la informacion desifrada
     */
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
    
    /**
     * decryptBlock
     * descifra un bloque de datos de 16 bytes
     */
    private static void decryptBlock() {
        //System.out.println("---------decryptBlock");
        //printBlock();
        //addRoundKey with initial key
        addRoundKey(keySchedule.getFirstKeyInverese());
        int round = 1;
        for (; round < 10; round++) {
            //System.out.println("round "+round);
            inverseShiftRows();
            inverseSubBytes();
            addRoundKey(keySchedule.getNextKeyInverse(round)); 
            inverseMixColumns();   
        }

        inverseShiftRows();
        inverseSubBytes();
        addRoundKey(keySchedule.getNextKeyInverse(round));

    }   
    
    

    /**
     * newBlock 
     * llena el bloque de 16 bytes con el siguietne bloque
     * de datos
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
     * hasNextBlock 
     * verifica si queda un bloque de datos de 16 bits en los datos
     * @return verdadero si hay un bloque siguiete de lo contrario falso
     */
    private static boolean hasNextBlock() {
        if (index >= textBytes.length) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * subBytes 
     * 1ra transformacion de Rijndael
     */
    private static void subBytes() {

        for (int i = 0; i < 4; i++) {
            subBytes(block[i]);
        }
    }
    
    /**
     * subBytes
     * sobrecarga de la 1ra transformacion de Rijndael
     * especifico de una fila del bloque
     * @param arr fila del bloque
     */
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
     * inversesubBytes 
     * 1ra transformacion de Rijndael
     * Decrypt
     */
    private static void inverseSubBytes() {

        for (int i = 0; i < 4; i++) {
            inverseSubBytes(block[i]);
        }
    }

    /**
     * inverseSubBytes
     * Sobrecarga de la 1ra transformacion de Rijndael
     * especifico de una fila del bloque
     * Decrypt
     * @param arr fila del bloque de datos
     */
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
     * shiftRows 
     * 2da transformacion de Rijndael
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
     * InverseShiftRows
     * 2da transformacion de Rijndael Rijndael
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
     * MixColumns 
     * 3ra transformacion de Rijndael
     *
     * usa tabal lookup de Multiplicacion de  Galois
     * para las multiplicaciones
     * y xor (^) para las sumas 
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
     * InverseMixColumns 
     * 3ra transformacion de Rijndael
     * Decrypt
     *
     * usa tabal lookup de Multiplicacion de  Galois
     * para las multiplicaciones
     * y xor (^) para las sumas  
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

    /**
     * 4ta transformacion de Rijndael
     * 
     * @param RoundKey llave de ronda
     */
    private static void addRoundKey(byte[][] RoundKey) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                block[i][j] ^= RoundKey[i][j];
            }                
        }
    }

    /**
     * printBlock 
     * imprime bloque en formato hexadecimal
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
