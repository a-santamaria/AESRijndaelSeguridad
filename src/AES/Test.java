package AES;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Test {

    public static void main(String[] args) {

        try {
            /**
             * cambiar por entreda por gui en vez de archivo*
             */
            //reading data from file as bytes
            Path path = Paths.get("archivo.txt");
            byte[] data = Files.readAllBytes(path);

            /**
             * deberia ser guardada para cada usuario y asi se puede usar para
             * desencriptar
			 *
             */
            //generate private key
            byte[][] privateKey = KeySchedule.generateKey();

            //printkey in hex format
            pirntKey(privateKey);

            //aes encription algorithm
            RijndaelAES aes = new RijndaelAES(data, privateKey);
            byte[] salida = aes.encrypt();

            if (salida != null) {
                //saving encrypted bytes to file
                FileOutputStream fos = new FileOutputStream("salida.txt");
                fos.write(salida);
                fos.close();
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
