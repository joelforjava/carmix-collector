package com.joelforjava.processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.ID3v1;

public class MP3DataExtractor {

	// TODO - in the future, extract everything
	public String extractArtist(Path source) {
		String artistName = "";
		if (Files.exists(source)) {
			try {
				MP3File mp3File = new MP3File(source.toFile(), false);
		        ID3v1 tag = mp3File.getID3v1Tag();
		        artistName = tag.getArtist();
			} catch (IOException | TagException e) {
				e.printStackTrace();
	            LOGGER.log(Level.SEVERE, null, e);
			}
		}
		
		return artistName;
	}
	
    private static final Logger LOGGER = Logger.getLogger(MP3DataExtractor.class.getName());

}
