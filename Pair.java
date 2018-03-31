/*
 * Pair.java
 * 
 * This class defines a Pair object. A pair object
 * is made up of 2 letters of plaintext and 2 
 * resulting letters of ciphertext. 
 * 
 * Author: Nicholas Hawkins (JMU e-ID: hawkinng)
 * 
 */

public class Pair {
	
	String plain;
	String cipher;
	
	public Pair(String plain, String cipher)
	{
		this.plain = plain;
		this.cipher = cipher;
	}
	
	public String getPlain()
	{
		return plain;
	}
	
	public String getCipher()
	{
		return cipher;
	}

}
