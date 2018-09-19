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

		isFile(inPath);
		canRead(inPath);

		if (Files.exists(outPath)) {
			canWrite(outPath);
			// TODO - Add this functionality to the GUI. Until then, files will be overwritten!
            /*
            System.out.print("Overwrite existing file " + outPath.getFileName() + "? (Y/N): ");
			System.out.flush();
			BufferedReader promptIn = new BufferedReader(new InputStreamReader(System.in));
			String response = promptIn.readLine();
			if (!response.toUpperCase().equals("Y")) {
				throw new IOException("FileCopy: Existing file " + outPath.getFileName() + " was not overwritten");
			}
			*/
		} else {
			Path parentDirectory = outPath.getParent();
			if (!Files.exists(parentDirectory)) {
				Files.createDirectories(parentDirectory);
				if (!Files.exists(parentDirectory)) {
					throw new IOException("Cannot create directory: " + parentDirectory);
				}
			}

		}
		Files.copy(inPath, outPath, COPY_ATTRIBUTES, REPLACE_EXISTING);
	}

	/**
	 * Checks to see if a Path is an actual file.
	 * 
	 * @param aPath - the path we wish to check
	 * @return true if the path is a file
	 * @throws IOException when the path is not a file
	 */
	private static boolean isFile(Path aPath) throws IOException {
		if (!Files.isRegularFile(aPath)) {
			throw new IOException("File Verification: " + aPath.getFileName() + " does not exist");
		}
		return true;
	}
	
	/**
	 * Checks to see if a file represented by a Path can be read.
	 * 
	 * @param aPath - the path we wish to check
	 * @return true if the path can be read
	 * @throws IOException when the path cannot be read
	 */
	private static boolean canRead(Path aPath) throws IOException {
		if (!Files.isReadable(aPath)) {
			throw new IOException("File Verification: Cannot read file " + aPath.getFileName());
		}
		return true;
	}
	
	/**
	 * Checks to see if a file represented by a Path can be written.
	 * 
	 * @param aPath - the path we wish to check
	 * @return true if the path can be written
	 * @throws IOException when the path cannot be written
	 */
	private static boolean canWrite(Path aPath) throws IOException {
		if (!Files.isWritable(aPath)) {
			throw new IOException("File Verification: Cannot write to file " + aPath.getFileName());
		}
		return true;
	}

	private static final Logger LOGGER = Logger.getLogger(CopyFileService.class.getName());

}
