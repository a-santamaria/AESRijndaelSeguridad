package AES;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.SpringLayout;

public class Test {

    public static void main(String[] args) {

        try {
            /**
             * cambiar por entreda por gui en vez de archivo*
             */
            //reading data from file as bytes
            Path path = Paths.get("archivo.txt");
            byte[] data = Files.readAllBytes(path);
            
            System.out.println("original");
            System.out.println(new String(data));
            System.out.println("");
            
            //print string read from file
            System.out.println("bytes read from file");
            printData(data);
            
            /**
             * deberia ser guardada para cada usuario y asi se puede usar para
             * desencriptar
			 *
             */
            //generate private key ----unica por usuario--------
            byte[][] privateKey = KeySchedule.generateKey();

            //printkey in hex format
            System.out.println("-------128 private key generated-------");
            pirntKey(privateKey);

            //aes encription algorithm
            byte[] salida = RijndaelAES.encrypt(data, privateKey);

            if (salida != null) {
                
                //saving encrypted bytes to file
                FileOutputStream fos = new FileOutputStream("salida.txt");
                fos.write(salida);
                fos.close();
                
                
                //read from encripted file
                Path path2 = Paths.get("salida.txt");
                byte[] data2 = Files.readAllBytes(path2);
                
                System.out.println("-------Data encrypted-------");
                printData(data2);
                
                
                //RijndaelAES aes2 = new RijndaelAES(data2, privateKey);
                byte[] bonito = RijndaelAES.decrypt(data2, privateKey);
                int index = 0;
                
                System.out.println("-------Data decrypted-------");
                printData(bonito);
                
                String fin = new String(bonito, "UTF-8");
                System.out.println(fin);
            } else {
                System.out.println("empty file");
            }

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void pirntKey(byte[][] privateKey) {
        System.out.println("-----------privatekey------");
        for (int i = 0; i < privateKey.length; i++) {
            for (int j = 0; j < privateKey[i].length; j++) {
                System.out.print(toHex(privateKey[i][j]) + " ");
            }
            System.out.println();
        }
        System.out.println("---------------------------");

    }

    public static void printData(byte[] data){
        System.out.println("=====================================");
        int index = 0;
        while(index < data.length){
            System.out.println("--------------------");
            for(int i = 0 ; i < 4; i++){
                for(int j = 0 ;j < 4; j++){
                    if(index >= data.length) break;
                    System.out.print(toHex(data[index++])+" ");
                }
                System.out.println("");
            }
            System.out.println("-----------------------");
        }
        System.out.println("=====================================");
    }
    public static String toHex(char c) {
        return String.format("0x%02X", (int) c);
    }

    public static String toHex(int x) {
        return String.format("0x%02X", (int) x);
    }

    public static String toHex(byte b) {
        return String.format("0x%02X", b & 0xFF);
    }
}
