
/*
 * Driver.java
 * 
 * This class is the Driver of a Playfair Known Plaintext
 * Attack (KPA) that takes in one or more pairs of plaintext
 * and resulting ciphertext from the user. Using this information,
 * the program finds the resulting 5x5 Playfair Matrix and decrypts
 * any additional ciphertext the user may input. 
 * 
 * This program runs with the help of Pair.java, PlayfairMatrix.java,
 * and Decrypt.java. 
 * 
 * Author: Nicholas Hawkins (JMU e-ID: hawkinng)
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Driver {
	// Sequence of letters occur in the matrix
	static ArrayList<String> hint1 = new ArrayList<String>();
	// Letters are in the same row but not necessarily in a sequence
	static ArrayList<String> hint2Row = new ArrayList<String>();
	// Letters are in the same column but not necessarily in a sequence
	static ArrayList<String> hint2Column = new ArrayList<String>();
	// Sequence of letters occur in the matrix
	static ArrayList<String> hint3 = new ArrayList<String>();

	static ArrayList<Pair> pairs = new ArrayList<Pair>();

	public static void main(String args[]) {
		File file = new File("input.txt");
		Scanner s = null;
		try {
			s = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> plainList = new ArrayList<String>();
		ArrayList<String> cipherList = new ArrayList<String>();
		int count = 0;

		// Get plaintext/ciphertext pairs from user input
		while (s.hasNextLine()) {
			// System.out.print("Do you have a plaintext/ciphertext pair? (y/n):
			// ");
			count++;
			// s.nextLine();

			String current = s.nextLine();

			if (current.length() == 0)
				break;

			if (count % 2 == 1) {
				String plaintext = current;
				plaintext = plaintext.replaceAll("j", "i");
				plaintext = plaintext.toUpperCase();
				plaintext = plaintext.replaceAll("\\s+", "");
				plaintext = plaintext.replaceAll("[^A-Za-z]+", "");
				plainList.add(plaintext);

			} else if (count % 2 == 0) {
				// s.nextLine();
				String ciphertext = current;
				ciphertext = ciphertext.toUpperCase();
				ciphertext = ciphertext.replaceAll("\\s+", "");
				ciphertext = ciphertext.replaceAll("[^A-Za-z]+", "");
				cipherList.add(ciphertext);
			}
		}

		for (int ii = 0; ii < plainList.size(); ii++) {
			String sub = plainList.get(ii);
			String ci = cipherList.get(ii);

			// Find duplicate consecutive letters in plaintext, add an "X" in
			// between them
			for (int jj = 0; jj < sub.length() - 1; jj += 2) {
				if (sub.charAt(jj) == sub.charAt(jj + 1)) {
					sub = sub.substring(0, jj + 1) + "X" + sub.substring(jj + 1, sub.length());
				}

			}

			// If the plain text has an odd length, append a "Z" at the end
			if (sub.length() % 2 == 1)
				sub = sub + "Z";

			// If the plain/cipher pair is valid, split into pairs of 2 letters
			// each
			if (sub.length() == ci.length()) {
				while (sub.length() > 0) {
					pairs.add(new Pair(sub.substring(0, 2), ci.substring(0, 2)));
					sub = sub.substring(2);
					ci = ci.substring(2);
				}
				// If the pair is not valid, skip
			} else
				System.out.println("Invalid plain/ciphertext combination");

		}

		// Find sequences of trigraphs (3 letters) in the matrix
		findHint1(pairs);
		System.out.println("\nSequences in matrix: " + hint1.toString());

		// Find letters that belong in the same row/column together
		findHint2(pairs);
		System.out.println("Letters that belong in same row: " + hint2Row.toString());
		System.out.println("Letters that belong in same column: " + hint2Column.toString());

		// Find sequences of pentagraphs (5 letters) in the matrix
		findHint3(pairs);
		System.out.println("Sequences in matrix: " + hint3.toString());

		// Create a PlayfairMatrix object and use information ("hints) to find
		// matrix
		PlayfairMatrix matrix = new PlayfairMatrix(pairs, hint1, hint2Row, hint2Column, hint3);
		matrix.findMatrix();

		// Ask user if they want to decrypt additional ciphertext
		if (matrix.success()) {

			Decrypt d = new Decrypt(matrix.getMatrix());
			File file2 = new File("ciphertext.txt");
			Scanner s2 = null;
			try {
				s2 = new Scanner(file2);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			while (s2.hasNextLine()) {
				String next = s2.nextLine();

				if (next.length() > 0)
					d.findPlain(next);
			}
		}
	}

	// Find cipher/plain pairs that share a letter; the three letters
	// form a trigraph located in the 5x5 matrix
	public static void findHint1(ArrayList<Pair> list) {
		for (int ii = 0; ii < list.size(); ii++) {
			if (list.get(ii).getPlain().charAt(0) == list.get(ii).getCipher().charAt(1)) {
				String add = list.get(ii).getPlain().substring(1, 2) + list.get(ii).getPlain().substring(0, 1)
						+ list.get(ii).getCipher().substring(0, 1);
				if (!hint1.contains(add)) {
					hint1.add(add);
				}

			} else if (list.get(ii).getPlain().charAt(1) == list.get(ii).getCipher().charAt(0)) {
				String add = list.get(ii).getPlain().substring(0, 2) + list.get(ii).getCipher().substring(1, 2);
				if (!hint1.contains(add)) {
					hint1.add(add);
				}
			}
		}
	}

	// Find plain/cipher pairs that encrypt each other. These pairs result in
	// rules as to
	// certain letters located in the same row and same column in the 5x5
	// matrix.
	public static void findHint2(ArrayList<Pair> list) {
		for (int ii = 0; ii < pairs.size(); ii++) {
			String currentPlain = pairs.get(ii).getPlain();
			for (int jj = 0; jj < pairs.size(); jj++) {
				if (pairs.get(jj).getCipher().equals(currentPlain)) {

					if (pairs.get(jj).getPlain().equals(pairs.get(ii).getCipher())) {
						String rowAdd1 = currentPlain.substring(0, 1) + pairs.get(jj).getPlain().substring(0, 1);
						String rowAdd2 = currentPlain.substring(1, 2) + pairs.get(jj).getPlain().substring(1, 2);
						String columnAdd1 = currentPlain.substring(0, 1) + pairs.get(jj).getPlain().substring(1, 2);
						String columnAdd2 = currentPlain.substring(1, 2) + pairs.get(jj).getPlain().substring(0, 1);

						if (!(hint2Row.contains(rowAdd1) || hint2Row.contains(reverse(rowAdd1))))
							hint2Row.add(rowAdd1);
						if (!(hint2Row.contains(rowAdd2) || hint2Row.contains(reverse(rowAdd2))))
							hint2Row.add(rowAdd2);
						if (!(hint2Column.contains(columnAdd1) || hint2Column.contains(reverse(columnAdd1))))
							hint2Column.add(columnAdd1);
						if (!(hint2Column.contains(columnAdd2) || hint2Column.contains(reverse(columnAdd2))))
							hint2Column.add(columnAdd2);
					}
				}
			}
		}
	}

	// Find plain/cipher pairs that share a pair of letters but do not encrypt
	// other.
	// These pairs result in a pentagraph (5 letters) sequence that occurs in
	// the 5x5 matrix.
	public static void findHint3(ArrayList<Pair> list) {
		for (int ii = 0; ii < pairs.size(); ii++) {
			String currentCipher = pairs.get(ii).getCipher();
			String currentPlain = pairs.get(ii).getPlain();
			boolean add = false;

			for (int jj = 0; jj < pairs.size(); jj++) {
				if (pairs.get(jj).getPlain().equals(currentCipher)) {

					if (!(pairs.get(jj).getCipher().equals(pairs.get(ii).getPlain()))) {
						add = true;
						String let1 = currentPlain.substring(0, 1);
						String let2 = currentCipher.substring(0, 1);
						String let3 = currentPlain.substring(1, 2);
						String let4 = currentCipher.substring(1, 2);
						String let5 = pairs.get(jj).getCipher().substring(1, 2);
						String let6 = pairs.get(jj).getCipher().substring(0, 1);
						String seq = "";

						if (currentPlain.substring(1, 2).equals(pairs.get(jj).getCipher().substring(0, 1))) {
							seq = let1 + let2 + let3 + let4 + let5;
						} else if (currentPlain.substring(0, 1).equals(pairs.get(jj).getCipher().substring(1, 2))) {
							seq = let3 + let4 + let1 + let2 + let6;
						}

						if (seq.length() > 0 && !(hint3.contains(seq))) {
							for (int kk = 0; kk < hint3.size(); kk++) {
								if (hint3.get(kk).contains(seq.substring(0, 2))
										|| hint3.get(kk).contains(seq.substring(1, 3)))
									add = false;
							}

							if (add)
								hint3.add(seq);
						}
					}
				}
			}
		}
	}

	// Helper method to reverse the string parameter
	private static String reverse(String str) {
		String reverse = "";
		int length = str.length();
		for (int i = length - 1; i >= 0; i--) {
			reverse = reverse + str.charAt(i);
		}
		return reverse;
	}

	// Helper method to print the list of pairs
	/*
	 * public static void printList(ArrayList<Pair> list) { for (int ii = 0; ii
	 * < list.size(); ii++) { System.out.println(list.get(ii).getPlain() + " " +
	 * list.get(ii).getCipher()); } }
	 */

	// Helper method to print pairs
	/*
	 * public static void printPairs() { for (int ii = 0; ii < pairs.size();
	 * ii++) { System.out.print(pairs.get(ii).getPlain() + " ");
	 * System.out.println(pairs.get(ii).getCipher()); } }
	 */
}
