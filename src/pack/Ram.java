package pack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Ram {

	private final VirtualMachine virtualMachine;
	private MultiLinked mll = new MultiLinked();

	public Ram(final VirtualMachine virtualMachine) {
		this.virtualMachine = virtualMachine;
	}

	public boolean appendToFile(final String filename, final String append) {
		final boolean appended = virtualMachine.getVdisk().append(filename, append);
		if (appended) {
			/* Append to RAM structure. */
			final int blocks = (int) Math.ceil(append.length() / 10.0);
			for (int i = 0; i < blocks; i++) {
				final int index = i * 10;
				final String block = (i + 1 == blocks) ? append.substring(index) : append.substring(index, index + 10);
				mll.addBlock(filename, block);
			}
		}
		return appended;
	}

	public boolean createFile(final String filename) {
		final int diskstart = virtualMachine.getVdisk().createFile(filename);
		if (diskstart != -1) {
			/* TODO Add file to RAM structure. */
			mll.addFile(filename, diskstart);
		}

		return diskstart != -1;
	}

	public boolean deleteBlocks(final String filename, final int startBlock, final int endBlock) {
		final boolean deleted = virtualMachine.getVdisk().deleteBlocks(filename, startBlock, endBlock);
		if (deleted) {
			mll.deleteBlock(filename, startBlock, endBlock);
		}
		return deleted;
	}

	/**
	 * Deletes a file from the RAM and the virtual disk.
	 * 
	 * @param filename
	 *            the name of the file to be deleted.
	 * @return {@code true} if the deletion from both places is successful,
	 *         {@code false} otherwise.
	 */
	public boolean deleteFile(final String filename) {
		/* Tell the virtual disk to delete the file. */
		final boolean deleted = virtualMachine.getVdisk().deleteFile(filename);
		if (deleted) {
			/*
			 * If the deletion from the virtual disk was successful, delete also
			 * from the RAM.
			 */
			mll.deleteFile(filename);
			/* TODO Delete from RAM structure. */
		}
		return deleted;
	}

	public void insert(final String filename, final String append, final int insertBlock) {

		final int blocks = (int) Math.ceil(append.length() / 10.0);
		for (int i = 0; i < blocks; i++) {
			final int index = i * 10;
			final String block = (i + 1 == blocks) ? append.substring(index) : append.substring(index, index + 10);
			mll.addBlock(filename, block, insertBlock - 2 + i);
		}
	}

	public boolean fileExists(final String filename) {
		return mll.search(filename) != null;
		// return virtualMachine.getVdisk().fileExists(filename) != -1;
	}

	public int fileSize(final String filename) {
		final FileNode file = mll.search(filename);
		return file == null ? -1 : file.size;
	}

	public void printFile(final String filename) {
		/* TODO This is temporary. Files should be printed directly from RAM. */
		final String text = mll.printFile(filename);
		if (text != null) {
			virtualMachine.outln(text);
		} else {
			virtualMachine.outWarning("File is empty");
		}

		// virtualMachine.getVdisk().printFile(filename);
	}

	public void reload(final char[][] disk) {
		mll = new MultiLinked();

		String block;
		int nextBlock = 0;
		for (int i = 0; i < Vdisk.NUM_BLOCKS; i++) {

			block = makeString(disk[i]);

			if (block.charAt(0) == '@') {
				final String filename = block.substring(1, block.indexOf('.'));
				mll.addFile(filename, i);

				nextBlock = i;

				while (true) {
					nextBlock = disk[nextBlock][10] - 1;
					if (nextBlock != -1) {
						String text = makeString(disk[nextBlock]);
						if (text.indexOf('.') > 0) {
							text = text.substring(0, text.indexOf('.'));
						}
						mll.addBlock(filename, text);
					} else {
						break;
					}
				}
			}
		}
	}

	private String makeString(char[] chars) {
		String string = "";
		for (int i = 0; i < chars.length - 1; i++) {
			string += chars[i];
		}
		return string;
	}

	public void runBatch(final String filename) {
		final File file = new File(filename);
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				virtualMachine.out(line + " : ");
				virtualMachine.getCpu().parseInput(line);
			}
			reader.close();
		} catch (IOException e) {
			virtualMachine.outErr("Batch file not found");
		}
	}

	public void printDisk(boolean b) {
		virtualMachine.outln(mll.printAll(b));
	}

}
