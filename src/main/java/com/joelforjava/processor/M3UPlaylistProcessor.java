package com.joelforjava.processor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.joelforjava.model.MusicFileData;

public class M3UPlaylistProcessor {

	public List<MusicFileData> process(Path path) {
		List<MusicFileData> extractedUris = new ArrayList<>();
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
					MusicFileData data = new MusicFileData(s);
					extractedUris.add(data);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return extractedUris;
	}
	
    private static final String M3U_HEADER = "#EXTM3U";
    private static final String M3U_INFO = "#EXTINF";

    private static final Logger LOGGER = Logger.getLogger(M3UPlaylistProcessor.class.getName());
	
}
