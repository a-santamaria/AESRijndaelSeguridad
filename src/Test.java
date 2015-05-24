
public class Test {

	public static void main(String[] args) {
		
		char[] c = {
				0x19, 0xa0, 0x9a, 0xe9,
				0x3d, 0xf4, 0xc6, 0xf8,
				0xe3, 0xe2, 0x8d, 0x48,
				0xbe, 0x2b, 0x2a, 0x08
		};
		
		
		char[] testKey = {
				0x2b, 0x28, 0xab, 0x09,
				0x7e, 0xae, 0xf7, 0xcf,
				0x15, 0xd2, 0x15, 0x4f,
				0x16, 0xa6, 0x88, 0x3c
		};
		
		/*byte b = (byte) (1 << 7);
		System.out.println(b & 0xFF);
		byte h =  (byte) (b >> 7); 
		System.out.println(h & 0xFF);
		*/
		
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
