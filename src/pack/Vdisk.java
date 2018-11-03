package pack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Vdisk {

	public static final int NUM_BLOCKS = 30;
	public static final char NULL_CHAR = '.';
	public int temp = 0;

	private final VirtualMachine virtualMachine;
	private final char[][] disk = new char[NUM_BLOCKS][11];
	private int pointer = 0;

	public Vdisk(final VirtualMachine virtualMachine) {
		this.virtualMachine = virtualMachine;

		for (int i = 0; i < NUM_BLOCKS; i++) {
			for (int j = 0; j < 10; j++) {
				disk[i][j] = NULL_CHAR;
			}
			disk[i][10] = 0;
		}
	}

	public boolean append(String filename, String append) {
		boolean appended = false;
		int lastBlock = fileExists(filename);
		if (lastBlock != -1) {
			int next;
			while ((next = disk[lastBlock][10]) != 0) {
				lastBlock = next - 1;
			}
		}
		if (lastBlock != -1 && pointer < NUM_BLOCKS && (NUM_BLOCKS - pointer) * 10 >= append.length()) {
			disk[lastBlock][10] = (char) (pointer + 1);
			for (int i = 0; i < append.length(); i++) {
				final int pos = i % 10;
				if (i != 0 && pos == 0) {
					disk[pointer][10] = (char) (pointer + 2);
					pointer++;
				}
				disk[pointer][pos] = append.charAt(i);
			}
			pointer++;
			appended = true;
		}
		return appended;
	}

	public int createFile(final String filename) {
		int diskstart = -1;
		if (pointer < NUM_BLOCKS) {
			disk[pointer][0] = '@';
			for (int i = 0; i < filename.length(); i++) {
				disk[pointer][i + 1] = filename.charAt(i);
			}
			diskstart = pointer++;
		}
		return diskstart;
	}

	public void defragDisk() {
		char[][] tempDisk = new char[NUM_BLOCKS][11];
		int counter = 2;
		/*
		 * Find the files which start '@' and add in the tempDisk(for
		 * temporarily sorting the disk by using the names of files and then,
		 * change 'tempDisk' with 'disk.)
		 */
		if (pointer != 0) {
			for (int i = 0; i < NUM_BLOCKS; i++) {
				if (disk[i][0] == '@') {
					int search = i;
					while (true) {
						for (int k = 0; k < 10; k++) {
							tempDisk[counter - 2][k] = disk[search][k];
						}

						if (Integer.valueOf(disk[search][10]) != 0) {
							tempDisk[counter - 2][10] = (char) (counter);
							counter++;
						} else {
							tempDisk[counter - 2][10] = 0;
							counter++;
							break;
						}
						search = Integer.valueOf(disk[search][10]) - 1;

					}
				}
			}
			/* Reset the 'disk' array to place the tempDisk */
			for (int i = 0; i < 30; i++) {
				for (int j = 0; j < 11; j++) {
					disk[i][j] = NULL_CHAR;
				}
				disk[i][10] = 0;
			}
			boolean flag = true;
			/* Change 'tempDisk' with 'disk' */
			for (int i = 0; i < 30; i++) {
				for (int j = 0; j < 11; j++) {
					if (tempDisk[i][0] != 0) {
						disk[i][j] = tempDisk[i][j];
					}
					if (flag == true && tempDisk[i][0] != 0 && tempDisk[i][0] == '.') {
						pointer = i;
						flag = false;
					}

				}
			}
			pointer = counter - 2;
		}
	}

	public boolean deleteBlocks(final String filename, final int startBlock, final int endBlock) {
		final int diskstart = fileExists(filename);
		if (diskstart != -1) {
			int blockBeforeStart = diskstart;
			/* Find the block just before the first one that will be deleted. */
			for (int i = 0; i < startBlock - 1; i++) {
				blockBeforeStart = disk[blockBeforeStart][10] - 1;
				if (blockBeforeStart < 1) {
					return false;
				}
			}
			/* Find the first block to delete. */
			int block = disk[blockBeforeStart][10] - 1;
			if (block < 1) {
				return false;
			}
			/* Find the last block to delete. */
			int end = block;
			for (int i = 0; i < endBlock - startBlock; i++) {
				end = disk[end][10] - 1;
				if (end < 1) {
					return false;
				}
			}
			/* Change the block just after the last one. */
			disk[blockBeforeStart][10] = disk[end][10];

			int nextBlock;
			for (int i = 0; i < endBlock - startBlock + 1; i++) {
				/* Put the null character (.) in the block. */
				for (int j = 0; j < 10; j++) {
					disk[block][j] = NULL_CHAR;
				}
				/*
				 * Set the current block to the next one, and clear next block
				 * from the current one.
				 */
				nextBlock = disk[block][10];
				disk[block][10] = 0;
				block = nextBlock - 1;
			}
		}
		return true;
	}

	/**
	 * Deletes a file from the virtual disk.
	 * 
	 * @param filename
	 *            the name of the file to be deleted.
	 * @return {@code true} if the deletion is successful, {@code false}
	 *         otherwise.
	 */
	public boolean deleteFile(final String filename) {
		/* Initialize a flag. */
		boolean deleted = false;
		/*
		 * Find the starting block of the file (the block holding the filename).
		 */
		int diskstart = fileExists(filename);
		/* If found, start the deleting operation. */
		if (diskstart != -1) {
			/*
			 * Create variables to traverse all the blocks associated with the
			 * file.
			 */
			int currentBlock = diskstart + 1;
			int nextBlock;
			/*
			 * As long as the end of the file is not reached (known by reaching
			 * block of index 0), clear the blocks.
			 */
			while (currentBlock != 0) {
				/* Put the null character (.) in the block. */
				for (int i = 0; i < 10; i++) {
					disk[currentBlock - 1][i] = NULL_CHAR;
				}
				/*
				 * Set the current block to the next one, and clear next block
				 * from the current one.
				 */
				nextBlock = Integer.valueOf(disk[currentBlock - 1][10]);
				disk[currentBlock - 1][10] = 0;
				currentBlock = nextBlock;
			}
			/*
			 * Successfully going through all this means the file is finally
			 * deleted. Set the flag to true.
			 */
			deleted = true;
		}
		return deleted;
	}

	// finding last location for inserting
	public void getInsertblockNext(int insertblock, int diskstart, int insertIndex) {

		int loc = diskstart;

		for (int i = 1; i <= insertblock; i++) {

			// if (i == insertblock) {
			// temp = disk[loc][10];
			// }

			if (i <= insertblock) {
				loc = disk[loc][10] - 1;
			}

			if (insertblock == 1) {
				temp = disk[diskstart][10];
				disk[diskstart][10] = (char) (insertIndex + 1);
			} else if (i == insertblock - 1) {
				temp = disk[loc][10];
				disk[loc][10] = (char) (insertIndex + 1);
			}
		}
	}

	public int lastlocation() {
		return pointer;
	}

	public void insert(String inputSplit, int lastloc) {

		/* */

		int partial;
		int stringloc = 0;

		for (int i = lastloc; i < NUM_BLOCKS; i++) {

			if (String.valueOf(disk[i][0]).equals(".")) {
				/* If the word is fit for block */
				partial = inputSplit.substring(stringloc).length();
				if (partial < 10) {
					for (int j = 0; j < partial; j++) {
						disk[i][j] = inputSplit.charAt(stringloc);
						stringloc = stringloc + 1;
					}
					disk[i][10] = (char) temp;
					i += 1;
					break;
				}

				// if the word is the same with block
				else if (partial == 10) {
					for (int j = 0; j < 10; j++) {
						disk[i][j] = inputSplit.charAt(stringloc);
						stringloc = stringloc + 1;
						disk[i][10] = (char) temp;
					}
					i += 1;
					break;
				}

				// if the word is overflowed for block
				else if (partial > 10) {

					if (String.valueOf(disk[i][0]).equals(".")) {
						for (int k = 0; k < 10; k++) {
							disk[i][k] = inputSplit.charAt(stringloc);
							stringloc = stringloc + 1;
							disk[i][10] = (char) (i + 2);
						}
					}
				}

			}

			pointer++;
		}

		pointer++;
	}

	public void printDisk() {

		virtualMachine.outln();

		for (int i = 0; i < 6; i++) {

			// final int rowStart = i * 5 + 1;

			for (int j = 0; j < 5; j++) {

				final int block = i * 5 + j;
				final int nextBlock = Integer.valueOf(disk[block][10]);

				virtualMachine.outEmphasis((block < 9 ? "0" : "") + (block + 1) + ":  ");

				for (int k = 0; k < 10; k++) {
					virtualMachine.out(String.valueOf(disk[block][k]));
				}
				virtualMachine.out(" " + nextBlock);

				if (j != 4) {
					virtualMachine.out((nextBlock < 10 ? " " : "") + "  ");
				}
			}

			virtualMachine.outln();
		}
	}

	/** TODO Temporary. */
	void printFile(String filename) {
		final int diskstart = fileExists(filename);
		if (diskstart != -1) {
			int block = diskstart + 1;
			while ((block = disk[block - 1][10]) != 0) {
				for (int i = 0; i < 10; i++) {
					final char out = disk[block - 1][i];
					if (out == NULL_CHAR) {
						continue;
					}
					virtualMachine.out(String.valueOf(out));
				}
			}
			virtualMachine.outln();
		}
	}

	/** TODO Temporary. */
	int fileExists(String filename) {
		int diskstart = -1;
		for (int i = 0; i < NUM_BLOCKS && diskstart == -1; i++) {
			if (disk[i][0] == '@') {
				String searchname = "";
				for (int k = 1; k < 10; k++) {
					if (disk[i][k] == NULL_CHAR) {
						break;
					}
					searchname += disk[i][k];
				}
				if (searchname.equalsIgnoreCase(filename)) {
					diskstart = i;
				}
			}
		}
		return diskstart;
	}

	public void store() {
		try {
			PrintWriter writer = new PrintWriter("save.txt");
			for (int i = 0; i < 30; i++) {
				for (int j = 0; j < 11; j++) {
					if (j == 10) {
						writer.println(Integer.valueOf(disk[i][j]));
					} else {
						writer.println(disk[i][j]);
					}
				}
			}
			writer.close();
		} catch (IOException e) {
		}
	}

	public void reStore() {
		final File file = new File("save.txt");
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			for (int i = 0; i < 30; i++) {
				for (int j = 0; j < 11; j++) {
					line = reader.readLine();
					if (j == 10) {
						disk[i][j] = (char) Integer.valueOf(line).intValue();
					} else {
						disk[i][j] = line.charAt(0);
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Restore file not found");
		}

		// Restructure the RAM
		virtualMachine.getRam().reload(disk);

	}
}
