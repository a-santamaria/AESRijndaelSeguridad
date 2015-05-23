
public class RijndaelAES {
	
	private String text;
	private byte[] byteText;
	private byte[][] block;
	private int index;
	
	public RijndaelAES(String text) {
		super();
		this.text = text;
		
		/**this doesn't work (for 0x9A and 0x8D)... don't know why**/
		//byteText = text.getBytes(); 
		
		//convert string to byte array
		byteText = new byte[text.length()];
		for(int i = 0 ; i < text.length(); i++){
			byteText[i] = (byte) text.charAt(i);
		}
		index = 0;
		block = new byte[4][4];
	}
	
	public String encript(){
		index = 0;
		if(text.isEmpty()) return null;
		
		while(hasNextBlock()){
			newBlock();
			printBlock();
			subBytes();
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
	 * subByte, 1st transform of Rijndael
	 */
	private void subBytes(){
		int x, y;
		byte curr;  
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				curr = block[i][j];
				
				x = (curr & 0xF0) >> 4; //mask: 11110000
				y = curr & 0x0F; 		//mask: 00001111
				
				block[i][j] = Rijndael_Sbox.get(x, y);
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
