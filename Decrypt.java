/*
 * Decrypt.java
 * 
 * This class uses a Playfair matrix (2D string array) to decrypt
 * ciphertext into plaintext.
 * 
 * Author: Nicholas Hawkins (JMU e-ID: hawkinng)
 * 
 */

import java.util.ArrayList;

public class Decrypt {

	String[][] matrix;

	public Decrypt(String[][] matrix) {
		this.matrix = matrix;
	}

	// Find the plaintext when given ciphertext. The matrix is already known. 
	public String findPlain(String cipher) {
		String plain = "";
		cipher = cipher.replaceAll(" ", "");
		ArrayList<String> cipherPairs = new ArrayList<String>();
		ArrayList<String> plainPairs = new ArrayList<String>();

		// Break the ciphertext into pairs of 2 letters
		if (cipher.length() % 2 == 0)
			{
			while (cipher.length() > 0) {
				cipherPairs.add(cipher.substring(0, 2).toUpperCase());
				cipher = cipher.substring(2);
			}
	
			System.out.print("\nCiphertext: ");
	
			for (int ii = 0; ii < cipherPairs.size(); ii++) {
				String let1 = cipherPairs.get(ii).substring(0, 1);
				String let2 = cipherPairs.get(ii).substring(1, 2);
				System.out.print(let1+let2);
				int row1 = getRowIndex(let1);
				int col1 = getColIndex(let1);
				int row2 = getRowIndex(let2);
				int col2 = getColIndex(let2);
	
				// Playfair rule: row and column of each letter is different
				if (row1 != row2 && col1 != col2) {
					String let3 = matrix[row1][col2];
					String let4 = matrix[row2][col1];
					plainPairs.add(let3 + let4);
				}
				// Playfair rule: same column
				else if (col1==col2)
				{
					String let3;
					String let4;
					
					if (row1 > 0)
						let3 = matrix[row1-1][col1];
					else
						let3 = matrix[4][col1];
					
					if (row2 > 0)
						let4 = matrix[row2-1][col2];
					else
						let4 = matrix[4][col2];
					
					plainPairs.add(let3+let4);
				}
				// Playfair rule: same row
				else if (row1==row2)
				{
					String let3;
					String let4;
					
					if (col1 > 0)
						let3 = matrix[row1][col1-1];
					else
						let3 = matrix[row1][4];
					
					if (col2 > 0)
						let4 = matrix[row2][col2-1];
					else
						let4 = matrix[row2][4];
					
					plainPairs.add(let3+let4);
				}
			}
	
			// Print the resulting plaintext
			System.out.print("\nPlaintext:  ");
			
			for (int jj = 0; jj < plainPairs.size(); jj++)
				System.out.print(plainPairs.get(jj).substring(0,1)+plainPairs.get(jj).substring(1, 2));
			System.out.println("\n");
		}
		else
			System.out.println("Invalid ciphertext.");

		return plain;
	}

	// Get index of the row for a specific letter
	public int getRowIndex(String let) {
		for (int ii = 0; ii < 5; ii++) {
			for (int jj = 0; jj < 5; jj++) {
				if (matrix[ii][jj].equals(let))
					return ii;
			}
		}
		System.out.println(let);
		return -1;
	}

	// Get index of the column for a specific letter
	public int getColIndex(String let) {
		for (int ii = 0; ii < 5; ii++) {
			for (int jj = 0; jj < 5; jj++) {
				if (matrix[ii][jj].equals(let))
					return jj;
			}
		}

		return -1;
	}

	/*public void print() {
		for (int ii = 0; ii < 5; ii++) {
			for (int jj = 0; jj < 5; jj++)
				System.out.print(matrix[ii][jj]);
			System.out.println("");
		}
	}*/
}
