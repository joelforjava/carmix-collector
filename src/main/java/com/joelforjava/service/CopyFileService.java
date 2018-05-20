package com.joelforjava.service;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CopyFileService {

	public void copy(Path inPath, Path outPath) throws IOException {

		if (Files.exists(outPath) && Files.isSameFile(inPath, outPath)) {
			LOGGER.log(Level.INFO, "Files are the same, no copy performed");
			return;
		}

		verifyFile(inPath, FILE_TYPE, READ_FILE);

		if (Files.exists(outPath)) {
			if (!Files.isWritable(outPath)) {
				throw new IOException("Cannot write to: " + outPath);
			}
			// This STILL needs to be a prompt for the GUI.
			System.out.print("Overwrite existing file " + outPath.getFileName() + "? (Y/N): ");
			System.out.flush();
			BufferedReader promptIn = new BufferedReader(new InputStreamReader(System.in));
			String response = promptIn.readLine();
			if (!response.toUpperCase().equals("Y")) {
				throw new IOException("FileCopy: Existing file " + outPath.getFileName() + " was not overwritten");
			}
		} else {
			Path parentDirectory = outPath.getParent();
			if (!Files.exists(parentDirectory)) {
				Files.createDirectories(parentDirectory);
				if (!Files.exists(parentDirectory)) {
					throw new IOException("Cannot create directory: " + parentDirectory);
				}
			}

			// check for exists, isRegularFile, canWrite
			// TODO - determine why inPath instead of outPath
			verifyFile(inPath, FILE_TYPE, WRITE_FILE);
		}
		Files.copy(inPath, outPath, COPY_ATTRIBUTES, REPLACE_EXISTING);
	}

	/**
	 * Performs verifications as follows: 1. Verfies that a file or directory exists
	 * 2. Verifies a file is a file or a directory is a directory 3. Verifies that a
	 * file/directory can be read from or written to
	 * 
	 * @param aFile
	 *            - file/directory to verify
	 * @param indFileDir
	 *            - indicates if aFile is a file or directory
	 * @param indReadWrite
	 *            - indicates if read or write functionality should be checked
	 * @return
	 * @throws java.io.IOException
	 */
	private static boolean verifyFile(Path aPath, String indFileDir, String indReadWrite) throws IOException {
		if (!Files.exists(aPath)) {
			throw new IOException("File Verification: " + aPath.getFileName() + " does not exist");
		}

		switch (indFileDir) {
		case FILE_TYPE:
			if (!Files.isRegularFile(aPath)) {
				throw new IOException("File Verification: " + aPath.getFileName() + " does not exist");
			}
			break;
		case DIR_TYPE:
			if (!Files.isDirectory(aPath)) {
				throw new IOException("Directory Verification: " + aPath.getFileName() + " does not exist");
			}
			break;
		}

		switch (indReadWrite) {
		case READ_FILE:
			if (!Files.isReadable(aPath)) {
				throw new IOException("File Verification: Cannot read file " + aPath.getFileName());
			}
			break;
		case WRITE_FILE:
			if (!Files.isWritable(aPath)) {
				throw new IOException("File Verification: Cannot write to file " + aPath.getFileName());
			}
			break;

		}
		return true;
	}

	private static final Logger LOGGER = Logger.getLogger(CopyFileService.class.getName());

	private static final String READ_FILE = "R";
	private static final String WRITE_FILE = "W";

	private static final String DIR_TYPE = "D";
	private static final String FILE_TYPE = "F";

}
