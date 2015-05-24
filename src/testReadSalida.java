import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class testReadSalida {

	public static void main(String[] args) {
		
		try {
			FileReader f = new FileReader("salida.txt");
			Path path = Paths.get("salida.txt");
			byte[] data = Files.readAllBytes(path);
			
			for(byte b : data){
				System.out.println(toHex(b));
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	public static String toHex(byte b){
		return String.format("0x%02X", b & 0xFF);
	}

}
