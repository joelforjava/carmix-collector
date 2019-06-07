package com.joelforjava.processor;

import java.nio.file.Path;

public interface AudioFileMetadataExtractor {
    String extractArtist(Path source);
}
