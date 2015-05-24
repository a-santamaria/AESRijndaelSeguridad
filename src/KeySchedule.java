
public class KeySchedule {
	private byte[][] secretKeyBytes;
	private int round;
	private static char[][] Rcon = {
			{0x01, 0x00, 0x00, 0x00},
			{0x02, 0x00, 0x00, 0x00},
			{0x04, 0x00, 0x00, 0x00},
			{0x08, 0x00, 0x00, 0x00},
			{0x10, 0x00, 0x00, 0x00},
			{0x20, 0x00, 0x00, 0x00},
			{0x40, 0x00, 0x00, 0x00},
			{0x80, 0x00, 0x00, 0x00},
			{0x1b, 0x00, 0x00, 0x00},
			{0x36, 0x00, 0x00, 0x00}
	};
	
	public KeySchedule(byte[][] statSecretKey) {
		super();
		char[][] testKey = {
				{0x2b, 0x28, 0xab, 0x09},
				{0x7e, 0xae, 0xf7, 0xcf},
				{0x15, 0xd2, 0x15, 0x4f},
				{0x16, 0xa6, 0x88, 0x3c}
		};
		//this.secretKeyBytes = statSecretKey;
		this.secretKeyBytes = new byte[4][4];
		for(int i = 0; i < 4; i++){
			for(int j = 0 ; j < 4; j++){
				secretKeyBytes[i][j] = (byte) testKey[i][j];
			}
		}
		round = 0;
	}

	public byte[][] getNextKey(){
		System.out.println("---------key");
		printKey();
		System.out.println("---------------");
		byte[] rotWord = new byte[4];
		System.out.println("rotWord---->");
		for(int i = 0; i < 4; i++){
			rotWord[i] = secretKeyBytes[i][3];
			System.out.print(toHex(rotWord[i])+" ");
		}
		System.out.println("--------------------");
		
		
		shift(rotWord);
		System.out.println("shift rotWord---->");
		printRow(rotWord);
		System.out.println("--------------------");
		
		subBytes(rotWord);
		System.out.println("subBytes---->");
		printRow(rotWord);
		System.out.println("--------------------");
		
		reconXor(rotWord);
		System.out.println("reconXor--------->");
		printKey();
		System.out.println("-----------------");
		
		
		return secretKeyBytes;
	}	
		
	private void shift(byte[] arr){
		byte aux = arr[0];
		arr[0] = arr[1];
		arr[1] = arr[2];
		arr[2] = arr[3];
		arr[3] = aux;
	}
	
	private void subBytes(byte[] arr){
		int x, y;
		byte curr;
		for(int j = 0; j < 4; j++){
			curr = arr[j];
			
			x = (curr & 0xF0) >> 4; //mask: 11110000
			y = curr & 0x0F; 		//mask: 00001111
			
			arr[j] = Rijndael_Sbox.get(x, y);
		}
	}
	
	private void reconXor(byte[] arr){
		
		for(int j = 0; j < 4; j++){
			secretKeyBytes[j][0] = (byte) (secretKeyBytes[j][0] ^ 
								   arr[j] ^ Rcon[round][j]);
		}
		
		for(int i = 1; i < 4; i++){
			for(int j = 0; j < 4; j++){
				secretKeyBytes[j][i] = (byte) (secretKeyBytes[j][i] ^ secretKeyBytes[j][i-1]);
			}
		}
	}
	
	private void printRow(byte[] arr){
		for(int j = 0; j < 4; j++){
			System.out.print(toHex(arr[j])+" ");
		}
	}
	private void printKey(){
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				System.out.print(toHex(secretKeyBytes[i][j])+" ");
			}
			System.out.println();
		}
	}
	
	private String toHex(byte b){
		return String.format("0x%02X", (b & 0xFF));
	}
}
