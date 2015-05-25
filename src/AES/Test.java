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

            /*
            char[] c = {
                0x19, 0xa0, 0x9a, 0xe9,
                0x3d, 0xf4, 0xc6, 0xf8,
                0xe3, 0xe2, 0x8d, 0x48,
                0xbe, 0x2b, 0x2a, 0x08
		};
            
            /*char[] c = {
                0x32, 0x88, 0x31, 0xe0,
                0x43, 0x5a, 0x31, 0x37,
                0xf6, 0x30, 0x98, 0x07,
                0xa8, 0x8d, 0xa2, 0x34
		};
            */
            
            /*char[] c = {
                '_', '?', 'N', 'R',
                'a', '%', '3', '2',
                'g', '~', '{', '.',
                'f', 'e', '\''
		};
            
            String s = new String(c);
            System.out.println("original");
            for(int i = 0; i < c.length; i++)
                System.out.print(c[i]);
            System.out.println("");
            System.out.println(s);
            System.out.println("");
            byte[] data = new byte[c.length];
                    //data = s.getBytes();
            
            for(int i = 0 ; i < c.length; i++){
                data[i] = (byte) c[i];
            }
            */
            
            int index = 0;
            for(int i = 0 ; i < 4; i++){
                for(int j = 0 ;j < 4; j++){
                    if(index >= data.length) break;
                    System.out.print(toHex(data[index++])+" ");
                }
                System.out.println("");
            }
            System.out.println("");
            
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
            //RijndaelAES aes = new RijndaelAES(data, privateKey);
            byte[] salida = RijndaelAES.encrypt(data, privateKey);

            if (salida != null) {
                //saving encrypted bytes to file
                FileOutputStream fos = new FileOutputStream("salida.txt");
                fos.write(salida);
                fos.close();
                
                
                
                Path path2 = Paths.get("salida.txt");
                byte[] data2 = Files.readAllBytes(path2);
                
                System.out.println("++++++++++++++++++++Antes de decrypt");
                pirntKey(privateKey);
                System.out.println("+++++++++++++++++++++++++++++++++");
                //RijndaelAES aes2 = new RijndaelAES(data2, privateKey);
                byte[] bonito = RijndaelAES.decrypt(data2, privateKey);
                index = 0;
                
                System.out.println("********888after decrypt");
                for(int i = 0 ; i < 4; i++){
                    for(int j = 0 ;j < 4; j++){
                        System.out.print(toHex(bonito[index++])+" ");
                    }
                System.out.println("****************8");
                }
                System.out.println("");
                
                String fin = new String(bonito, "UTF-8");
                System.out.println(fin);
                /*ArrayList<Character> arr = new ArrayList<>();
                for(byte b : bonito){
                    arr.add((char)b);
                }
                System.out.println("------------>Decrypted");
                for(int i = 0; i < arr.size(); i++){
                    System.out.print(arr.get(i));
                }
                System.out.println("------------------->");*/
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
