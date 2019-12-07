package com.yang.util;

/**
 * @author this class is searching specific byte from given byte array range
 *         from start to end
 * 
 * 
 */
public class FindByte {

	/**
	 * commets : this method requires the length of dest byte array declared
	 * first and then as a parameter in this method as follows : byte[] dest =
	 * new byte[length] ; searchArraySeq(dest , source , 4) ;
	 * 
	 * @param dest
	 *            the destination byte array to store the bytes founded from
	 *            source
	 * @param source
	 *            the source byte array to find
	 * @param start
	 *            the start position to find byte in source byte
	 * @param end
	 *            the end position to find byte in source byte
	 */
	public static void searchByteSeq(byte[] dest, byte[] source, int start) {

		/**
		 * no need to ensure the length of the dest equals (end - start + 1)
		 * because the length of dest equals (end - start + 1), the parameter
		 * end no need
		 */
		for (int i = 0; i < dest.length; i++) {
			dest[i] = source[i + start];
		}

	}
   
	
	/**
	 * returns an array byte that stores the bytes founded in source array
	 * @param source the source byte array byte array to find 
	 * @param start  the start position to find byte in source byte
	 * @param end    the end position to find byte in source byte
	 * @return byte[] a byte array that stores the bytes founded in source array range from start to end 
	 */
	public static byte[] searchByteSeq(byte[] source, int start, int end) {
        // a temp byte array that stores the byte founded in source array      
		byte[] tempByte = new byte[end - start + 1] ;
		
		// assigned temp byte
		for(int i = 0 ; i < tempByte.length ; i ++){
			tempByte[i] = source[i + start] ;
		}
		
		return tempByte ;
              
	}

}
