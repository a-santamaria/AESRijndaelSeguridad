/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package AES;

/**
 *
 * @author alfredo
 */
public class testKeySchedule {
    
    public static void main(String[] args) {
        char[][] testKey = {
            {0x2b, 0x28, 0xab, 0x09},
            {0x7e, 0xae, 0xf7, 0xcf},
            {0x15, 0xd2, 0x15, 0x4f},
            {0x16, 0xa6, 0x88, 0x3c}
        };
        

        byte[][] currentKey = new byte[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                currentKey[i][j] = (byte) testKey[i][j];
            }
        }
        KeySchedule keySchedule = new KeySchedule(currentKey);
        
        printBlock(keySchedule.getFirstKey());
        
        keySchedule.calculateNextKey();
        keySchedule.calculateNextKey();
        keySchedule.calculateNextKey();
        keySchedule.calculateNextKey();
        keySchedule.calculateNextKey();
        keySchedule.calculateNextKey();
        keySchedule.calculateNextKey();
        keySchedule.calculateNextKey();
        keySchedule.calculateNextKey();
        keySchedule.calculateNextKey();
    }
    
    private static void printBlock(byte[][] block) {
        System.out.println("-----------------------");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(toHex(block[i][j]) + " ");
            }
            System.out.println();
        }
        System.out.println("-----------------------");
    }
    
     public static String toHex(byte b) {
        return String.format("0x%02X", b & 0xFF);
    }
}
