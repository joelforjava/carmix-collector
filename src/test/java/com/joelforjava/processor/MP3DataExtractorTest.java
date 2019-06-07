package com.joelforjava.processor;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

public class MP3DataExtractorTest {

    private ClassLoader classLoader = this.getClass().getClassLoader();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testExtractArtistFromExistingMP3File() throws Exception {
        File file = new File(classLoader.getResource("empty.mp3").getFile());

        MP3DataExtractor extractor = new MP3DataExtractor();
        String artistName = extractor.extractArtist(file.toPath());

        Assert.assertEquals("That Artist", artistName);
    }

    @Test
    public void testExtractArtistFromExistingALACFileThrowsException() throws Exception {
        File file = new File(classLoader.getResource("empty-alac.m4a").getFile());

        MP3DataExtractor extractor = new MP3DataExtractor();
        expectedException.expect(NullPointerException.class);
        extractor.extractArtist(file.toPath());
    }

    @Test
    public void testExtractArtistFromExistingFLACFileThrowsException() throws Exception {
        File file = new File(classLoader.getResource("empty.flac").getFile());

        MP3DataExtractor extractor = new MP3DataExtractor();
        expectedException.expect(NullPointerException.class);
        extractor.extractArtist(file.toPath());
    }

    @Test
    public void testExtractArtistFromExistingOggFileThrowsException() throws Exception {
        File file = new File(classLoader.getResource("empty.ogg").getFile());

        MP3DataExtractor extractor = new MP3DataExtractor();
        expectedException.expect(NullPointerException.class);
        extractor.extractArtist(file.toPath());
    }

    @Test
    public void testExtractArtistFromExistingWMAFileThrowsException() throws Exception {
        File file = new File(classLoader.getResource("empty.wma").getFile());

        MP3DataExtractor extractor = new MP3DataExtractor();
        expectedException.expect(NullPointerException.class);
        extractor.extractArtist(file.toPath());
    }
}
