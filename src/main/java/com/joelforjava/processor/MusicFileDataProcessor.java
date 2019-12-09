package com.joelforjava.processor;

import com.joelforjava.model.MusicFileData;
import com.joelforjava.model.OutputFormat;
import com.joelforjava.request.CopyRequest;
import com.joelforjava.service.CopyFileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MusicFileDataProcessor {

    private final OutputFormat outputFormat;

    private final String outputDirectoryName;

    private final boolean overwriteExisting;

    private final CopyFileService copyService;

    private static final Logger LOGGER = Logger.getLogger(MusicFileData.class.getName());

    public MusicFileDataProcessor(String outputDirectoryName, OutputFormat outputFormat,
                                  boolean overwriteExisting, CopyFileService copyFileService) {
        Objects.requireNonNull(outputDirectoryName);
        Objects.requireNonNull(outputFormat);
        Objects.requireNonNull(copyFileService);
        this.outputDirectoryName = outputDirectoryName;
        this.outputFormat = outputFormat;
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
                final String newFileName = generateDestinationFileUri(fileData);
                LOGGER.info(String.format("Generated new file name of %s%n", newFileName));
                Path target = Paths.get(newFileName);
                CopyRequest request = new CopyRequest(source, target, overwriteExisting);
                copyService.copy(request);
                String strLogInfo = String.format("Copied: %s\tto\t%s",fileDataUri, newFileName);
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

    private String generateDestinationFileUri(MusicFileData fileData) {
        return outputFormat.produceFormatted(fileData, this.outputDirectoryName);
    }

}
