/*
 * PlayfairMatrix.java
 * 
 * This class constructs a 5x5 playfair matrix using rules
 * and sequences found in the plaintext/ciphertext pairs. 
 * These rules and sequences are passed as parameters when
 * a PlayfairMatrix object is instantiated.
 * 
 * Author: Nicholas Hawkins (JMU e-ID: hawkinng)
 * 
 */

import java.util.ArrayList;

public class PlayfairMatrix {

	String[][] matrix;
	ArrayList<Pair> pairs = new ArrayList<Pair>();
	// Sequence of letters occur in the matrix
	ArrayList<String> hint1 = new ArrayList<String>();
	static // Letters are in the same row but not necessarily in a sequence
	ArrayList<String> hint2Row = new ArrayList<String>();
	static // Letters are in the same column but not necessarily in a sequence
	ArrayList<String> hint2Column = new ArrayList<String>();
	// Sequence of letters occur in the matrix
	ArrayList<String> hint3 = new ArrayList<String>();

	ArrayList<String> addRow = new ArrayList<String>();
	ArrayList<String> addCol = new ArrayList<String>();
	ArrayList<String> addRow3 = new ArrayList<String>();
	ArrayList<String> addCol3 = new ArrayList<String>();

	public PlayfairMatrix(ArrayList<Pair> pairs, ArrayList<String> hint1, ArrayList<String> hint2Row,
			ArrayList<String> hint2Column, ArrayList<String> hint3) {
		matrix = new String[5][5];
		this.hint1 = hint1;
		PlayfairMatrix.hint2Column = hint2Column;
		PlayfairMatrix.hint2Row = hint2Row;
		this.hint3 = hint3;
		this.pairs = pairs;
	}

	public void findMatrix() {
		clearMatrix();

		// Determine if sequences belong in a row or column
		for (int ii = 0; ii < hint3.size(); ii++)
			rowOrColumn(hint3.get(ii));
		for (int jj = 0; jj < hint1.size(); jj++)
			rowOrColumn(hint1.get(jj));

		// Set the top row and subsequent column to fit
		firstRowColumn();

		// Remove sequences from list that have already been placed in the matrix 
		removeIfInMatrix();

		// Add all additional pentagraphs to the matrix
		for (int ii = 0; ii < addRow.size() * 2; ii++)
			plusRow5();

		for (int ii = 0; ii < addCol.size() * 2; ii++)
			plusCol5();
		
		for (int i = 0; i < 5; i++)
		{
			add3Row();
			add3Col();
			fill1();
			fill2();
			fillUp2();
			//fill3();
			fillPairs();
			removeIfInMatrix();
		}
		
		missingOne();

		// Print the resulting matrix
		print();
	}

	// Return the String[][] array that represents the 5x5 matrix
	public String[][] getMatrix() {
		return matrix;
	}

	// Add a row pentagraph to the 5x5 matrix
	public void plusRow5() {
		for (int ii = 0; ii < addRow.size(); ii++) {
			ArrayList<String> row = new ArrayList<String>();

			for (int jj = 0; jj < addRow.get(ii).length(); jj++) {
				row.add(addRow.get(ii).substring(jj, jj + 1));
			}

			for (int kk = 1; kk < 5; kk++) // rows
			{
				for (int ll = 0; ll < 5; ll++) // columns
				{
					if (row.contains(matrix[kk][ll])) {

						int count = 0;
						int index = row.indexOf(matrix[kk][ll]);

						for (int mm = ll + 1; mm < row.size(); mm++) {

							index++;

							if (index > row.size() - 1)
								index = 0;

							matrix[kk][mm] = row.get(index);
							count++;

						}

						for (int nn = 0; nn < row.size() - count - 1; nn++) {
							index++;

							if (index > row.size() - 1)
								index = 0;

							matrix[kk][nn] = row.get(index);

						}

						addRow.remove(ii);
						return;
					}
				}
			}

		}
	}

	// Add a column pentagraph to the 5x5 matrix
	public void plusCol5() {
		for (int ii = 0; ii < addCol.size(); ii++) {
			ArrayList<String> row = new ArrayList<String>();

			for (int jj = 0; jj < addCol.get(ii).length(); jj++) {
				row.add(addCol.get(ii).substring(jj, jj + 1));
			}

			for (int kk = 0; kk < 5; kk++) // columns
			{
				for (int ll = 0; ll < 5; ll++) // rows
				{
					if (row.contains(matrix[kk][ll])) {

						int count = 1;
						int index = row.indexOf(matrix[kk][ll]);

						for (int mm = kk + 1; mm < 5; mm++) {

							index++;

							if (index > row.size() - 1)
								index = 0;

							matrix[mm][ll] = row.get(index);
							count++;

						}

						for (int nn = 0; nn < row.size() - count; nn++) {
							index++;

							if (index > row.size() - 1)
								index = 0;

							matrix[nn][ll] = row.get(index);

						}

						addCol.remove(ii);

						return;
					}
				}
			}

		}
	}

	// Add additional row trigraphs that occur in the matrix 
	public void add3Row() {
		// For every sequence of 3
		for (int kk = 0; kk < hint1.size(); kk++) {
			String seq = hint1.get(kk);
			String seq1 = seq.substring(0, 1);
			String seq2 = seq.substring(1, 2);
			String seq3 = seq.substring(2, 3);

			// Go through rows (vertically)
			for (int ii = 0; ii < 5; ii++) {
				// Go through columns (horizontally)
				for (int jj = 0; jj < 5; jj++) {
					int index1 = jj;
					int index2 = jj + 1;
					int index3 = jj + 2;

					if (jj == 4) {
						index2 = 0;
						index3 = 1;
					} else if (jj == 3)
						index3 = 0;

					String s1 = matrix[ii][index1];
					String s2 = matrix[ii][index2];
					String s3 = matrix[ii][index3];

					if (s1.equals(seq1) && s2.equals(seq2))
						matrix[ii][index3] = seq3;
					if (s2.equals(seq2) && s3.equals(seq3))
						matrix[ii][index1] = seq1;
					if (s1.equals(seq1) && s3.equals(seq3))
						matrix[ii][index2] = seq2;

				}
			}
		}
	}

	// Add additional column trigraphs that occur in the matrix 
	public void add3Col() {
		// For every sequence of 3
		for (int kk = 0; kk < hint1.size(); kk++) {
			String seq = hint1.get(kk);
			String seq1 = seq.substring(0, 1);
			String seq2 = seq.substring(1, 2);
			String seq3 = seq.substring(2, 3);

			// Go through columns (horizontally)
			for (int ii = 0; ii < 5; ii++) {
				// Go through rows (vertically)
				for (int jj = 0; jj < 5; jj++) {
					int index1 = jj;
					int index2 = jj + 1;
					int index3 = jj + 2;

					if (jj == 4) {
						index2 = 0;
						index3 = 1;
					} else if (jj == 3)
						index3 = 0;

					String s1 = matrix[index1][ii];
					String s2 = matrix[index2][ii];
					String s3 = matrix[index3][ii];

					if (s1.equals(seq1) && s2.equals(seq2))
						matrix[index3][ii] = seq3;
					if (s2.equals(seq2) && s3.equals(seq3))
						matrix[index1][ii] = seq1;
					if (s1.equals(seq1) && s3.equals(seq3))
						matrix[index2][ii] = seq2;

				}
			}
		}
	}

	// Add the first row and subsequent fitting column to the matrix
	public void firstRowColumn() {
		int index = 0;
		
		if (addCol.size() > 0 && addRow.size() > 0)
		{
			for (int ii = 0; ii < addCol.size(); ii++) {
				String l1 = addCol.get(index).substring(0, 1);
				String l2 = addCol.get(index).substring(1, 2);
				String l3 = addCol.get(index).substring(2, 3);
				String l4 = addCol.get(index).substring(3, 4);
				String l5 = addCol.get(index).substring(4, 5);
				String[] col = { l1, l2, l3, l4, l5 };
	
				for (int jj = 0; jj < addRow.size(); ii++) {
					String let1 = addRow.get(index).substring(0, 1);
					String let2 = addRow.get(index).substring(1, 2);
					String let3 = addRow.get(index).substring(2, 3);
					String let4 = addRow.get(index).substring(3, 4);
					String let5 = addRow.get(index).substring(4, 5);
					String[] row = { let1, let2, let3, let4, let5 };
	
					for (int kk = 0; kk < 5; kk++) {
						for (int ll = 0; ll < 5; ll++) {
							if (row[ll].equals(col[kk])) {
								matrix[0] = row;
								addRow.remove(jj);
								int count = 1;
	
								for (int mm = kk + 1; mm < 5; mm++) {
									matrix[count][ll] = col[mm];
									count++;
								}
	
								for (int nn = 0; nn < 5 - count; nn++) {
									matrix[count][ll] = col[nn];
									count++;
								}
	
								// print();
								return;
							}
						}
					}
				}
			}
		}
		else if (hint3.size() > 0)
		{
			String let1 = hint3.get(0).substring(0, 1);
			String let2 = hint3.get(0).substring(1, 2);
			String let3 = hint3.get(0).substring(2, 3);
			String let4 = hint3.get(0).substring(3, 4);
			String let5 = hint3.get(0).substring(4, 5);
			String[] row = { let1, let2, let3, let4, let5 };

			matrix[0] = row;
		}
	}

	// Fill remaining spots based on letters already occurring in the matrix
	public void fill1() {
		for (int kk = 0; kk < 4; kk++) {
			for (int ii = 0; ii < 4; ii++) {
				if (matrix[ii + 1][kk].equals("_")) {

					String let1 = matrix[ii][kk];
					String let2 = matrix[ii][kk + 1];
					String let3 = matrix[ii + 1][kk + 1];
					String let4 = "";
					String let5 = "";
					String let6 = "";
					String let7 = "";
					String let8 = "";
					String let9 = "";

					if (kk < 3) {
						let4 = matrix[ii][kk + 2];
						let5 = matrix[ii + 1][kk + 2];
					}

					if (kk < 2) {
						let6 = matrix[ii][kk + 3];
						let7 = matrix[ii + 1][kk + 3];
					}

					if (kk < 1) {
						let8 = matrix[ii][kk + 4];
						;
						let9 = matrix[ii + 1][kk + 4];
						;
					}

					for (int jj = 0; jj < pairs.size(); jj++) {
						String cipher = pairs.get(jj).getCipher();
						String plain = pairs.get(jj).getPlain();

						if (cipher.equals(let1 + let3) || cipher.equals(let3 + let1)) {
							if (plain.substring(0, 1).equals(let2))
								matrix[ii + 1][kk] = plain.substring(1, 2);
							if (plain.substring(1, 2).equals(let2))
								matrix[ii + 1][kk] = plain.substring(0, 1);
						}

						else if (cipher.equals(let1 + let5) || cipher.equals(let5 + let1)) {
							if (plain.substring(0, 1).equals(let4))
								matrix[ii + 1][kk] = plain.substring(1, 2);
							if (plain.substring(1, 2).equals(let4))
								matrix[ii + 1][kk] = plain.substring(0, 1);
						}

						else if (cipher.equals(let1 + let7) || cipher.equals(let7 + let1)) {
							if (plain.substring(0, 1).equals(let6))
								matrix[ii + 1][kk] = plain.substring(1, 2);
							if (plain.substring(1, 2).equals(let6))
								matrix[ii + 1][kk] = plain.substring(0, 1);
						}

						else if (cipher.equals(let1 + let9) || cipher.equals(let9 + let1)) {
							if (plain.substring(0, 1).equals(let8))
								matrix[ii + 1][kk] = plain.substring(1, 2);
							if (plain.substring(1, 2).equals(let8))
								matrix[ii + 1][kk] = plain.substring(0, 1);
						}

						else if (plain.equals(let1 + let3) || plain.equals(let3 + let1)) {

							if (cipher.substring(0, 1).equals(let2))
								matrix[ii + 1][kk] = cipher.substring(1, 2);
							if (cipher.substring(1, 2).equals(let2))
								matrix[ii + 1][kk] = cipher.substring(0, 1);
						}

						else if (plain.equals(let1 + let5) || plain.equals(let5 + let1)) {

							if (cipher.substring(0, 1).equals(let4))
								matrix[ii + 1][kk] = cipher.substring(1, 2);
							if (cipher.substring(1, 2).equals(let4))
								matrix[ii + 1][kk] = cipher.substring(0, 1);
						}

						else if (plain.equals(let1 + let7) || plain.equals(let7 + let1)) {

							if (cipher.substring(0, 1).equals(let6))
								matrix[ii + 1][kk] = cipher.substring(1, 2);
							if (cipher.substring(1, 2).equals(let6))
								matrix[ii + 1][kk] = cipher.substring(0, 1);
						}

						else if (plain.equals(let1 + let9) || plain.equals(let9 + let1)) {

							if (cipher.substring(0, 1).equals(let8))
								matrix[ii + 1][kk] = cipher.substring(1, 2);
							if (cipher.substring(1, 2).equals(let8))
								matrix[ii + 1][kk] = cipher.substring(0, 1);
						}
					}
				}

			}
		}
	}
	
	public void fillPairs()
	{
		for (int ii = 0; ii < pairs.size(); ii++)
		{
			// Instantiate row/col index variables to -1
			int l1row = -1;
			int l1col = -1;
			int l2row = -1;
			int l2col = -1;
			int l3row = -1;
			int l3col = -1;
			int l4row = -1;
			int l4col = -1;
			
			// Get the letters of the pair of plain/cipher diagraphs
			String l1 = pairs.get(ii).getPlain().substring(0,1);
			String l2 = pairs.get(ii).getPlain().substring(1,2);
			String l3 = pairs.get(ii).getCipher().substring(0,1);
			String l4 = pairs.get(ii).getCipher().substring(1,2);
			
			// Search through the matrix to find the indexes of letters l1, l2, l3, l4
			for (int jj = 0; jj < 4; jj++)
			{
				for (int kk = 0; kk < 4; kk++)
				{
					if (matrix[jj][kk].equals(l1))
					{
						l1row = jj;
						l1col = kk;
					}
					
					if (matrix[jj][kk].equals(l2))
					{
						l2row = jj;
						l2col = kk;
					}
					
					if (matrix[jj][kk].equals(l3))
					{
						l3row = jj;
						l3col = kk;
					}
					
					if (matrix[jj][kk].equals(l4))
					{
						l4row = jj;
						l4col = kk;
					}
				}
			}
			
			// If the two plain letters occur in the matrix and have different rows/columns,
			// place the two cipher letters in the matrix that correspond with the plain letters
			if (l1row != l2row && l1col != l2col && l1row != -1 && l2row != -1)
			{
				if (missingLetters().contains(l3))
					matrix[l1row][l2col] = l3;
				if (missingLetters().contains(l4))
					matrix[l2row][l1col] = l4;
			}
			
			// If the two cipher letters occur in the matrix and have different rows/columns,
			// place the two plain letters in the matrix that correspond with the cipher letters
			if (l3row != l4row && l3col != l4col && l3row != -1 && l4row != -1)
			{
				if (missingLetters().contains(l1))
					matrix[l3row][l4col] = l1;
				if (missingLetters().contains(l2))
					matrix[l4row][l3col] = l2;
			}
		}
	}


	// Fill remaining spots based on letters already occurring in the matrix
	public void fill2() {
		for (int kk = 4; kk > 0; kk--) {
			for (int ii = 0; ii < 4; ii++) {
				if (matrix[ii + 1][kk].equals("_")) {

					String let1 = matrix[ii][kk];
					String let2 = matrix[ii][kk - 1];
					String let3 = matrix[ii + 1][kk - 1];
					String let4 = "";
					String let5 = "";
					String let6 = "";
					String let7 = "";
					String let8 = "";
					String let9 = "";

					if (kk > 1) {
						let4 = matrix[ii][kk - 2];
						let5 = matrix[ii + 1][kk - 2];
					}

					if (kk > 2) {
						let6 = matrix[ii][kk - 3];
						let7 = matrix[ii + 1][kk - 3];
					}

					if (kk > 3) {
						let8 = matrix[ii][kk - 4];
						let9 = matrix[ii + 1][kk - 4];
					}

					for (int jj = 0; jj < pairs.size(); jj++) {
						String cipher = pairs.get(jj).getCipher();
						String plain = pairs.get(jj).getPlain();

						if (cipher.equals(let1 + let3) || cipher.equals(let3 + let1)) {
							if (plain.substring(0, 1).equals(let2))
								matrix[ii + 1][kk] = plain.substring(1, 2);
							if (plain.substring(1, 2).equals(let2))
								matrix[ii + 1][kk] = plain.substring(0, 1);
						}

						else if (cipher.equals(let1 + let5) || cipher.equals(let5 + let1)) {
							if (plain.substring(0, 1).equals(let4))
								matrix[ii + 1][kk] = plain.substring(1, 2);
							if (plain.substring(1, 2).equals(let4))
								matrix[ii + 1][kk] = plain.substring(0, 1);
						}

						else if (cipher.equals(let1 + let7) || cipher.equals(let7 + let1)) {
							if (plain.substring(0, 1).equals(let6))
								matrix[ii + 1][kk] = plain.substring(1, 2);
							if (plain.substring(1, 2).equals(let6))
								matrix[ii + 1][kk] = plain.substring(0, 1);
						}

						else if (cipher.equals(let1 + let9) || cipher.equals(let9 + let1)) {
							if (plain.substring(0, 1).equals(let8))
								matrix[ii + 1][kk] = plain.substring(1, 2);
							if (plain.substring(1, 2).equals(let8))
								matrix[ii + 1][kk] = plain.substring(0, 1);
						}

						else if (plain.equals(let1 + let3) || plain.equals(let3 + let1)) {

							if (cipher.substring(0, 1).equals(let2))
								matrix[ii + 1][kk] = cipher.substring(1, 2);
							if (cipher.substring(1, 2).equals(let2))
								matrix[ii + 1][kk] = cipher.substring(0, 1);
						}

						else if (plain.equals(let1 + let5) || plain.equals(let5 + let1)) {

							if (cipher.substring(0, 1).equals(let4))
								matrix[ii + 1][kk] = cipher.substring(1, 2);
							if (cipher.substring(1, 2).equals(let4))
								matrix[ii + 1][kk] = cipher.substring(0, 1);
						}

						else if (plain.equals(let1 + let7) || plain.equals(let7 + let1)) {

							if (cipher.substring(0, 1).equals(let6))
								matrix[ii + 1][kk] = cipher.substring(1, 2);
							if (cipher.substring(1, 2).equals(let6))
								matrix[ii + 1][kk] = cipher.substring(0, 1);
						}

						else if (plain.equals(let1 + let9) || plain.equals(let9 + let1)) {

							if (cipher.substring(0, 1).equals(let8))
								matrix[ii + 1][kk] = cipher.substring(1, 2);
							if (cipher.substring(1, 2).equals(let8))
								matrix[ii + 1][kk] = cipher.substring(0, 1);
						}
					}
				}

			}
		}
	}

	// Fill remaining spots based on letters already occurring in the matrix
	public void fillUp2() {
		for (int kk = 0; kk < 3; kk++) {
			for (int ii = 0; ii < 3; ii++) {
				if (matrix[ii + 2][kk].equals("_")) {

					String let1 = matrix[ii][kk];
					String let2 = matrix[ii][kk + 1];
					String let3 = matrix[ii + 2][kk + 1];
					String let4 = "";
					String let5 = "";
					String let6 = "";
					String let7 = "";
					String let8 = "";
					String let9 = "";

					if (kk < 3) {
						let4 = matrix[ii][kk + 2];
						let5 = matrix[ii + 2][kk + 2];
					}

					if (kk < 2) {
						let6 = matrix[ii][kk + 3];
						let7 = matrix[ii + 2][kk + 3];
					}

					if (kk < 1) {
						let8 = matrix[ii][kk + 4];
						;
						let9 = matrix[ii + 2][kk + 4];
						;
					}

					for (int jj = 0; jj < pairs.size(); jj++) {
						String cipher = pairs.get(jj).getCipher();
						String plain = pairs.get(jj).getPlain();

						if (cipher.equals(let1 + let3) || cipher.equals(let3 + let1)) {
							if (plain.substring(0, 1).equals(let2))
								matrix[ii + 2][kk] = plain.substring(1, 2);
							if (plain.substring(1, 2).equals(let2))
								matrix[ii + 2][kk] = plain.substring(0, 1);
						}

						else if (cipher.equals(let1 + let5) || cipher.equals(let5 + let1)) {
							if (plain.substring(0, 1).equals(let4))
								matrix[ii + 2][kk] = plain.substring(1, 2);
							if (plain.substring(1, 2).equals(let4))
								matrix[ii + 2][kk] = plain.substring(0, 1);
						}

						else if (cipher.equals(let1 + let7) || cipher.equals(let7 + let1)) {
							if (plain.substring(0, 1).equals(let6))
								matrix[ii + 2][kk] = plain.substring(1, 2);
							if (plain.substring(1, 2).equals(let6))
								matrix[ii + 2][kk] = plain.substring(0, 1);
						}

						else if (cipher.equals(let1 + let9) || cipher.equals(let9 + let1)) {
							if (plain.substring(0, 1).equals(let8))
								matrix[ii + 2][kk] = plain.substring(1, 2);
							if (plain.substring(1, 2).equals(let8))
								matrix[ii + 2][kk] = plain.substring(0, 1);
						}

						else if (plain.equals(let1 + let3) || plain.equals(let3 + let1)) {

							if (cipher.substring(0, 1).equals(let2))
								matrix[ii + 2][kk] = cipher.substring(1, 2);
							if (cipher.substring(1, 2).equals(let2))
								matrix[ii + 2][kk] = cipher.substring(0, 1);
						}

						else if (plain.equals(let1 + let5) || plain.equals(let5 + let1)) {

							if (cipher.substring(0, 1).equals(let4))
								matrix[ii + 2][kk] = cipher.substring(1, 2);
							if (cipher.substring(1, 2).equals(let4))
								matrix[ii + 2][kk] = cipher.substring(0, 1);
						}

						else if (plain.equals(let1 + let7) || plain.equals(let7 + let1)) {

							if (cipher.substring(0, 1).equals(let6))
								matrix[ii + 2][kk] = cipher.substring(1, 2);
							if (cipher.substring(1, 2).equals(let6))
								matrix[ii + 2][kk] = cipher.substring(0, 1);
						}

						else if (plain.equals(let1 + let9) || plain.equals(let9 + let1)) {

							if (cipher.substring(0, 1).equals(let8))
								matrix[ii + 2][kk] = cipher.substring(1, 2);
							if (cipher.substring(1, 2).equals(let8))
								matrix[ii + 2][kk] = cipher.substring(0, 1);
						}
					}
				}

			}
		}
	}

	// Determines if sequences occur in the row or column
	// of the matrix based on "hint2" rules (letters occurring
	// within the same row or column)
	public void rowOrColumn(String seq) {
		for (int ii = 0; ii < hint2Row.size(); ii++) {
			String let1 = hint2Row.get(ii).substring(0, 1);
			String let2 = hint2Row.get(ii).substring(1, 2);

			if (seq.contains(let1) && seq.contains(let2)) {
				// System.out.println("ROW");
				if ((!addRow.contains(seq)) && seq.length() == 5)
					addRow.add(seq);
				else if ((!addRow3.contains(seq)) && seq.length() == 3)
					addRow3.add(seq);
			}
		}

		for (int ii = 0; ii < hint2Column.size(); ii++) {
			String let1 = hint2Column.get(ii).substring(0, 1);
			String let2 = hint2Column.get(ii).substring(1, 2);

			if (seq.contains(let1) && seq.contains(let2)) {
				// System.out.println("COLUMN");
				if ((!addCol.contains(seq)) && seq.length() == 5)
					addCol.add(seq);
				else if ((!addCol3.contains(seq)) && seq.length() == 3)
					addCol3.add(seq);
			}
		}

	}

	// Print the Playfair Matrix
	public void print() {
		System.out.println("\nPlayfair Key Matrix");
		for (int ii = 0; ii < 5; ii++) {
			for (int jj = 0; jj < 5; jj++)
				System.out.print(matrix[ii][jj]);
			System.out.println("");
		}

		if (missingLetters().length() > 0)
			System.out.print("\nMissing letters: " + missingLetters());

		System.out.println("");

	}

	// Determine if a certain letter or sequence occurs in the matrix
	public boolean inMatrix(String seq) {
		String row1 = matrix[0][0] + matrix[0][1] + matrix[0][2] + matrix[0][3] + matrix[0][4];
		String row2 = matrix[1][0] + matrix[1][1] + matrix[1][2] + matrix[1][3] + matrix[1][4];
		String row3 = matrix[2][0] + matrix[2][1] + matrix[2][2] + matrix[2][3] + matrix[2][4];
		String row4 = matrix[3][0] + matrix[3][1] + matrix[3][2] + matrix[3][3] + matrix[3][4];
		String row5 = matrix[4][0] + matrix[4][1] + matrix[4][2] + matrix[4][3] + matrix[4][4];
		String col1 = matrix[0][0] + matrix[1][0] + matrix[2][0] + matrix[3][0] + matrix[4][0];
		String col2 = matrix[0][1] + matrix[1][1] + matrix[2][1] + matrix[3][1] + matrix[4][1];
		String col3 = matrix[0][2] + matrix[1][2] + matrix[2][2] + matrix[3][2] + matrix[4][2];
		String col4 = matrix[0][3] + matrix[1][3] + matrix[2][3] + matrix[3][3] + matrix[4][3];
		String col5 = matrix[0][4] + matrix[1][4] + matrix[2][4] + matrix[3][4] + matrix[4][4];

		if (row1.contains(seq) || row2.contains(seq) || row3.contains(seq) || row4.contains(seq) || row5.contains(seq))
			return true;

		if (col1.contains(seq) || col2.contains(seq) || col3.contains(seq) || col4.contains(seq) || col5.contains(seq))
			return true;

		return false;
	}

	public void removeIfInMatrix()
	{
		for (int ii = 0; ii < hint1.size(); ii++)
			if (inMatrix(hint1.get(ii)))
				hint1.remove(ii);
		
		for (int ii = 0; ii < hint3.size(); ii++)
			if (inMatrix(hint3.get(ii)))
				hint3.remove(ii);
		
		for (int ii = 0; ii < addRow.size(); ii++)
			if (inMatrix(addRow.get(ii)))
				addRow.remove(ii);
		
		for (int ii = 0; ii < addCol.size(); ii++)
			if (inMatrix(addCol.get(ii)))
				addCol.remove(ii);
		
		for (int ii = 0; ii < addRow3.size(); ii++)
			if (inMatrix(addRow3.get(ii)))
				addRow3.remove(ii);
		
		for (int ii = 0; ii < addCol3.size(); ii++)
			if (inMatrix(addCol3.get(ii)))
				addCol3.remove(ii);
	}
	
	// Instantiate the matrix with empty spaces "_"
	public void clearMatrix() {
		for (int ii = 0; ii < 5; ii++)
			for (int jj = 0; jj < 5; jj++)
				matrix[ii][jj] = "_";
	}

	// Determine if the matrix is missing any letters
	public String missingLetters() {
		String alpha = "ABCDEFGHIKLMNOPQRSTUVWXYZ";
		String missing = "";

		for (int ii = 0; ii < alpha.length(); ii++) {
			if (!inMatrix(alpha.substring(ii, ii + 1))) {
				// System.out.print(alpha.substring(ii, ii+1));
				missing = missing + alpha.substring(ii, ii + 1);
			}
		}

		return missing;
	}

	// If the matrix is missing one letter, fill it
	public void missingOne() {
		if (missingLetters().length() == 1) {
			for (int ii = 0; ii < 5; ii++)
				for (int jj = 0; jj < 5; jj++)
					if (matrix[ii][jj].equals("_"))
						matrix[ii][jj] = missingLetters();
		}
	}

	// The matrix is completely full
	public boolean success() {
		if (missingLetters().length() == 0)
			return true;

		return false;
	}
	

	// Fill remaining spots based on letters already occurring in the matrix
	/*
	 * public void fill3() { for (int kk = 1; kk < 5; kk++) { for (int ii = 0;
	 * ii < 4; ii++) {
	 * 
	 * 
	 * if (matrix[ii+1][kk].equals(" ")) { String let1;// = matrix[ii][kk];
	 * String let2;// = matrix[ii][1]; String let3;// = matrix[ii + 1][1];
	 * 
	 * 
	 * for (int jj = 0; jj < pairs.size(); jj++) { String cipher =
	 * pairs.get(jj).getCipher(); String plain = pairs.get(jj).getPlain();
	 * 
	 * let1 = matrix[ii][kk]; let2 = matrix[ii][1]; let3 = matrix[ii + 1][1];
	 * 
	 * 
	 * if (cipher.equals(let1+let3) || cipher.equals(let3+let1)) { if
	 * (plain.substring(0, 1).equals(let2)) matrix[ii+1][kk] =
	 * plain.substring(1,2); if (plain.substring(1, 2).equals(let2))
	 * matrix[ii+1][kk] = plain.substring(0,1); } else if
	 * (plain.equals(let1+let3) || plain.equals(let3+let1)) {
	 * //System.out.println("HELLO dafdsW");
	 * 
	 * if (cipher.substring(0, 1).equals(let2)) matrix[ii+1][kk] =
	 * cipher.substring(1,2); if (cipher.substring(1, 2).equals(let2))
	 * matrix[ii+1][kk] = cipher.substring(0,1); } else if (kk < 4) {
	 * //System.out.println(let1+let3); let1 = matrix[ii][kk]; let2 =
	 * matrix[ii][kk+1]; let3 = matrix[ii + 1][kk+1];
	 * 
	 * if (cipher.equals(let1+let3) || cipher.equals(let3+let1)) {
	 * //System.out.println("HELLO W"); if (plain.substring(0, 1).equals(let2))
	 * matrix[ii+1][kk] = plain.substring(1,2); if (plain.substring(1,
	 * 2).equals(let2)) matrix[ii+1][kk] = plain.substring(0,1); } else if
	 * (plain.equals(let1+let3) || plain.equals(let3+let1)) {
	 * //System.out.println("HELLO dafdsW");
	 * 
	 * if (cipher.substring(0, 1).equals(let2)) matrix[ii+1][kk] =
	 * cipher.substring(1,2); if (cipher.substring(1, 2).equals(let2))
	 * matrix[ii+1][kk] = cipher.substring(0,1); } } } } }
	 * 
	 * } }
	 */

	
	// Additional check to determine if a cipher match can be made in the matrix
	// while only 2/4 letters are known in the matrix. 
	/*public void fill3() {
		if (missingLetters().length() < 5)
		{
			for (int kk = 0; kk < 4; kk++) {
				for (int ii = 0; ii < 4; ii++) {
					if (ii==3 && kk == 2)
						System.out.println("KJFDKF");
					if (matrix[kk+1][ii].equals("_") && matrix[kk+1][ii+1].equals("_"))
					{
						System.out.println("YES " + kk + " " + ii);
						String l1 = matrix[0][ii];
						String l2 = matrix[0][ii+1];
						String l3 = matrix[1][ii];
						String l4 = matrix[1][ii+1];
						String l5 = matrix[2][ii];
						String l6 = matrix[2][ii+1];
						String l7 = matrix[3][ii];
						String l8 = matrix[3][ii+1];
						String l9 = matrix[4][ii];
						String l10 = matrix[4][ii+1];
						
						for (int jj = 0; jj < pairs.size(); jj++)
						{
							String let1 = pairs.get(jj).getPlain().substring(0,1);
							String let2 = pairs.get(jj).getPlain().substring(1,2);
							String let3 = pairs.get(jj).getCipher().substring(0,1);
							String let4 = pairs.get(jj).getCipher().substring(1,2);
							
							if (l1.equals(let2) && l2.equals(let4))
							{
								System.out.println("0000");

								if (missingLetters().contains(let3))
									matrix[kk+1][ii] = let3;
								if (missingLetters().contains(let1))
									matrix[kk+1][ii+1] = let1;
								
							}
						}
					}
				}
			}
			
		}
	}
	*/
}
