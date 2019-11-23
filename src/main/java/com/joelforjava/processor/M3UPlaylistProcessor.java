package com.joelforjava.processor;

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

	private boolean extractArtist;

	private AudioFileMetadataExtractor dataExtractor;

	public M3UPlaylistProcessor() {
	    this(false);
    }

    public M3UPlaylistProcessor(boolean extractArtist) {
	    this.extractArtist = extractArtist;
    }

    // TODO - we should change this to extract data from the file itself rather than from the Playlist file
	//      - Data might be the same, but it's better to get it from the source.
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
					MusicFileData data = new MusicFileData(s);
					if (this.extractArtist) {
                        Path musicFilePath = Paths.get(s);
					    String artistName = dataExtractor.extractArtist(musicFilePath);
					    processedFileData.add(data.withArtistName(artistName));
                    } else {
                        processedFileData.add(data);
                    }
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return processedFileData;
	}

	public void setDataExtractor(AudioFileMetadataExtractor dataExtractor) {
	    this.dataExtractor = dataExtractor;
    }

    public M3UPlaylistProcessor withDataExtractor(AudioFileMetadataExtractor mp3DataExtractor) {
	    setDataExtractor(mp3DataExtractor);
	    return this;
    }

    public void setExtractArtist(boolean extractArtist) {
	    this.extractArtist = extractArtist;
    }

    public M3UPlaylistProcessor withExtractArtist(boolean extractArtist) {
	    setExtractArtist(extractArtist);
	    return this;
    }
	
    private static final String M3U_HEADER = "#EXTM3U";
    private static final String M3U_INFO = "#EXTINF";

    private static final Logger LOGGER = Logger.getLogger(M3UPlaylistProcessor.class.getName());
	
}
