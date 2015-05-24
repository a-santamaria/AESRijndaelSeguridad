import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Test {

	public static void main(String[] args) {
		
		char[] c = {
				0x19, 0xa0, 0x9a, 0xe9,
				0x3d, 0xf4, 0xc6, 0xf8,
				0xe3, 0xe2, 0x8d, 0x48,
				0xbe, 0x2b, 0x2a, 0x08
		};
		
		
		/*byte b = (byte) (1 << 7);
		System.out.println(b & 0xFF);
		byte h =  (byte) (b >> 7); 
		System.out.println(h & 0xFF);
		*/
		/*
		StringBuilder sb = new StringBuilder();
		try {
			
			
			/*BufferedReader br = new BufferedReader(new FileReader("archivo.txt"));
			String line;
			while((line = br.readLine()) != null){
				sb.append(line);
				sb.append('\n');
				System.out.println(line);
			}
			br.close();
			System.out.println("acabe");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*String s = "hola quiero se encriptado";//new String(c);
		
		System.out.println(sb.toString());
		System.out.println("first");
		for(int i = 0; i < sb.length(); i++){
			if(i%4 == 0) System.out.println();
			System.out.print(toHex(sb.charAt(i))+" ");
			
		}*/
		System.out.println("\n");
		System.out.println("---------AES-------------");
		//RijndaelAES aes = new RijndaelAES(sb.toString());
		
		
		try {
			FileReader f = new FileReader("archivo.txt");
			Path path = Paths.get("archivo.txt");
			byte[] data = Files.readAllBytes(path);
			
			RijndaelAES aes = new RijndaelAES(data);
			String salida = aes.encript();
			
			BufferedWriter bw = new BufferedWriter(new FileWriter("salida.txt"));
			bw.write(salida);
			bw.close();
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static String toHex(char c){
		return String.format("0x%02X", (int)c);
	}
	public static String toHex(int c){
		return String.format("0x%02X", (int)c);
	}
	public static String toHex(byte c){
		return String.format("0x%02X", (int)c);
	}
}
