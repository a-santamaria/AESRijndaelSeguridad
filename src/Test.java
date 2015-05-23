
public class Test {

	public static void main(String[] args) {
		
		char[] c = {
				0x19, 0xa0, 0x9a, 0xe9,
				0x3d, 0xf4, 0xc6, 0xf8,
				0xe3, 0xe2, 0x8d, 0x48,
				0xb3, 0x2b, 0x2a, 0x08
		};
		
		String s = new String(c);
		
		System.out.println(s);
		System.out.println("first");
		for(int i = 0; i < s.length(); i++){
			if(i%4 == 0) System.out.println();
			System.out.print(toHex(s.charAt(i))+" ");
			
		}
		System.out.println("\n");
		System.out.println("---------AES-------------");
		RijndaelAES aes = new RijndaelAES(s);
		aes.encript();
		
		
		
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
