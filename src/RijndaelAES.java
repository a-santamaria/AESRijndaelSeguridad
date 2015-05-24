import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


public class RijndaelAES {
	
	private String text;
	private byte[] byteText;
	private byte[][] block;
	private int index;
	private SecretKey secretKey;
	private byte[][] secretKeyBytes;
	private KeySchedule keySchedule;
	
	public RijndaelAES(String text) {
		super();
		this.text = text;
		
		generateKey();
		secretKeyBytes = new byte[4][4];
		int x = 0;
		int ii = -1, jj = 0;
		for(byte b :secretKey.getEncoded()){
			if((x++) % 4 == 0) {
				ii++;
				jj = 0;
			}
			secretKeyBytes[ii][jj] = b;
			jj++;
		}
		
		System.out.println("-----------key------");
		for(int i = 0 ; i < secretKeyBytes.length; i++){
			for(int j = 0; j < secretKeyBytes[i].length; j++)
				System.out.print(toHex(secretKeyBytes[i][j])+" ");
			System.out.println();
		}
		System.out.println("-------------------------");
		/**this doesn't work (for 0x9A and 0x8D)... don't know why**/
		//byteText = text.getBytes(); 
		
		//convert string to byte array
		byteText = new byte[text.length()];
		for(int i = 0 ; i < text.length(); i++){
			byteText[i] = (byte) text.charAt(i);
		}
		index = 0;
		block = new byte[4][4];
		keySchedule = new KeySchedule(secretKeyBytes);
	}
	
	private void generateKey(){
		KeyGenerator keyGen = null;
		try {
			keyGen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		keyGen.init(128);
		secretKey = keyGen.generateKey();
	}
	
	public String encript(){
		index = 0;
		if(text.isEmpty()) return null;
		
		while(hasNextBlock()){
			newBlock();
			printBlock();
			System.out.println("subBytes");
			subBytes();
			printBlock();
			System.out.println("shiftRows");
			shiftRows();
			printBlock();
			System.out.println("mixcolumns");
			mixColumns();
			printBlock();
			System.out.println("addRoundKey");
			addRoundKey();
			printBlock();
		}
		return null;
	}
	
	/**
	 * newBlock 
	 * fills block with next bytes in the byte array
	 */
	private void newBlock(){
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				if(index < byteText.length)
					block[i][j] = (byteText[index++]);
				else
					block[i][j] = 0;
			}
		}
	}
	
	/**
	 * hasNextBlock 
	 * returns if byte array has bytes left 
	 * for a new block
	 */
	private boolean hasNextBlock(){
		if(index >= byteText.length)
			return false;
		else 
			return true;
	}

	/**
	 * subBytes 
	 * 1st transformation of Rijndael
	 */
	private void subBytes(){
		
		byte curr;  
		for(int i = 0; i < 4; i++){
			subBytes(block[i]);
		}
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
	
	/**
	 * shiftRows 
	 * 2nd transformation of Rijndael
	 */
	private void shiftRows(){
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
	 * MixColumns 
	 * 3rd transformation of Rijndael
	 * 
	 * The array 'a' is simply a copy of the input array 'r'
     * The array 'b' is each element of the array 'a' multiplied by 2
     * in Rijndael's Galois field
     * a[n] ^ b[n] is element n multiplied by 3 in Rijndael's Galois field 
     * */ 
	private void mixColumns(){
		byte[] r = new byte[4];
		byte[] a = new byte[4];
		byte[] b = new byte[4];
	    byte c;
	    byte h;
		for(int i = 0 ; i < 4; i++){
			
			//get col i of block
			for(int j = 0; j < 4; j++){
				r[j] = block[j][i];
			}			
			
		    
		    for(c=0;c<4;c++) {
		            a[c] = r[c];
		            
		            /* h is 0xff if the high bit of r[c] is set, 0 otherwise */
		            h = (byte) (r[c] >> 7); 
		            b[c] = (byte) (r[c] << 1); 
		            b[c] ^= 0x1B & h; 
		    }
		    r[0] = (byte) (b[0] ^ a[3] ^ a[2] ^ b[1] ^ a[1]); // 2 * a0 + a3 + a2 + 3 * a1 
		    r[1] = (byte) (b[1] ^ a[0] ^ a[3] ^ b[2] ^ a[2]); // 2 * a1 + a0 + a3 + 3 * a2 
		    r[2] = (byte) (b[2] ^ a[1] ^ a[0] ^ b[3] ^ a[3]); // 2 * a2 + a1 + a0 + 3 * a3 
		    r[3] = (byte) (b[3] ^ a[2] ^ a[1] ^ b[0] ^ a[0]); // 2 * a3 + a2 + a1 + 3 * a0 
		    
		  //fill col i of block with transform
			for(int j = 0; j < 4; j++){
				block[j][i] = r[j];
			}
		}
	}
	
	private void addRoundKey(){
		byte[][] RoundKey = keySchedule.getNextKey();
		
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				block[i][j] ^= RoundKey[i][j];
			}
		}
	}
	
	/**
	 * printBlock 
	 * pirnt block in hex format
	 */
	private void printBlock(){
		System.out.println("-----------------------");
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				System.out.print(toHex(block[i][j]) + " ");
			}
			System.out.println();
		}
		System.out.println("-----------------------");
	}
	
	private String toHex(char c){
		return String.format("0x%02X", (int)c);
	}
	private String toHex(int x){
		return String.format("0x%02X", x);
	}
	private String toHex(byte b){
		return String.format("0x%02X", (b & 0xFF));
	}
}
