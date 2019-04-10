package com.joelforjava.processor;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class MP3DataExtractorTest {

    @Test
    public void testExtractArtistFromExistingFile() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();
        // TODO - replace with an empty MP3 file
        File file = new File(classLoader.getResource("07WhatsLeft.mp3").getFile());

        MP3DataExtractor extractor = new MP3DataExtractor();
        String artistName = extractor.extractArtist(file.toPath());

        Assert.assertEquals("Morgan Greig", artistName);
    }
}
