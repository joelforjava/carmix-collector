package com.joelforjava.processor;

import com.joelforjava.model.MusicFileData;
import com.joelforjava.request.CopyRequest;
import com.joelforjava.service.CopyFileService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicFileDataProcessor {

    private String outputFormat;

    private List<String> formatTokens;

    private String outputDirectoryName;

    private boolean overwriteExisting;

    private final CopyFileService copyService;

    private static final String FILE_SEPARATOR = File.separator;

    private static final Logger LOGGER = Logger.getLogger(MusicFileData.class.getName());

    private static final Pattern EXTRACT_FORMAT_TOKENS = Pattern.compile("\\{(.*?)}");

    public MusicFileDataProcessor(String outputDirectoryName, String outputFormat,
                                  boolean overwriteExisting, CopyFileService copyFileService) {
        Objects.requireNonNull(outputDirectoryName);
        Objects.requireNonNull(outputFormat);
        Objects.requireNonNull(copyFileService);
        this.outputDirectoryName = outputDirectoryName;
        this.outputFormat = outputFormat;
        this.formatTokens = parseOutputFormatString();
        this.overwriteExisting = overwriteExisting;
        this.copyService = copyFileService;
    }

    public void process(List<MusicFileData> musicFileData) {
        musicFileData.forEach(this::process); // TODO - for now
    }

    public void process(MusicFileData fileData) {
        String fileDataUri = fileData.getUri();
        Path source = Paths.get(fileDataUri);
        if (Files.exists(source)) {
            try {
                final String newFileName = generateDestinationFileUri(source, fileData);
                Path target = Paths.get(newFileName);
                CopyRequest request = new CopyRequest(source, target, overwriteExisting);
                copyService.copy(request);
                String strLogInfo = "Copied: " + fileDataUri + "\n to " + newFileName;
                // TODO - how do we replicate this??
                // setProgressInfoText(strLogInfo);
                LOGGER.info(strLogInfo);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        } else {
            String strLogWarning = "File not found! - " + fileDataUri;
            LOGGER.warning(strLogWarning);
        }
    }

    private String generateDestinationFileUri(Path source, MusicFileData fileData) {
        String fileName = source.getFileName().toString();
        final String newFIleUri;
        if (this.formatTokens.contains("ARTIST")) {
            String artistName = fileData.getArtistName();
            newFIleUri = this.getStrDestDirectoryName() + artistName + FILE_SEPARATOR + fileName;
        } else {
            newFIleUri = this.getStrDestDirectoryName() + fileName;
        }
        return newFIleUri;
    }

    private List<String> parseOutputFormatString() {
        String[] tokens = outputFormat.split(FILE_SEPARATOR);
        List<String> formatTokens = new ArrayList<>();
        for (String token : tokens) {
            Matcher matcher = EXTRACT_FORMAT_TOKENS.matcher(token);
            if (matcher.find()) {
                formatTokens.add(matcher.group(1));
            }
        }
        return Collections.unmodifiableList(formatTokens);
    }

    private String getStrDestDirectoryName() {
        return this.outputDirectoryName;
    }

    public List<String> getFormatTokens() {
        return this.formatTokens;
    }
}
