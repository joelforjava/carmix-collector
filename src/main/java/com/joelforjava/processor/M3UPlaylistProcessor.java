package com.joelforjava.processor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.joelforjava.model.MusicFileData;

public class M3UPlaylistProcessor {

	// TODO - make static
	public List<MusicFileData> process(Path path) {
		List<MusicFileData> processedFileData = new ArrayList<>();
		try {
			List<String> lines = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
			String firstLine = lines.remove(0);
			if (!M3U_HEADER.equals(firstLine)) {
				LOGGER.log(Level.WARNING, "M3U Header Not Found. for file: " + path.toString());
		        return Collections.emptyList();
			}
			LOGGER.log(Level.INFO, "M3U Header Found");
			for (String s : lines) {
				if (StringUtils.isBlank(s)) {
					continue;
				} else if (s.startsWith(M3U_INFO)) {
					continue;
					// processExtraInfo(s);
				} else {
					try {
						MusicFileData data = new MusicFileData(s);
						processedFileData.add(data);
					} catch (Exception fnfe) {
						LOGGER.warning("Could not load file for processing: " + s);
					}
				}
			}
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Could not process playlist file", e);
		}
		
		return processedFileData;
	}

    private static final String M3U_HEADER = "#EXTM3U";
    private static final String M3U_INFO = "#EXTINF";

    private static final Logger LOGGER = Logger.getLogger(M3UPlaylistProcessor.class.getName());
	
}
