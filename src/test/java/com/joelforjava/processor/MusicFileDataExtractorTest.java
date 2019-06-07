package com.joelforjava.processor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class MusicFileDataExtractorTest {
    private ClassLoader classLoader = this.getClass().getClassLoader();

    private MusicFileDataExtractor extractor;

    @Before
    public void setUp() {
        extractor = new MusicFileDataExtractor();
    }

    @Test
    public void testExtractArtistFromExistingMP3File() throws Exception {
        File file = new File(classLoader.getResource("empty.mp3").getFile());
        String artistName = extractor.extractArtist(file.toPath());
        Assert.assertEquals("That Artist", artistName);
    }

    @Test
    public void testExtractArtistFromExistingALACFile() throws Exception {
        File file = new File(classLoader.getResource("empty-alac.m4a").getFile());
        String artistName = extractor.extractArtist(file.toPath());
        Assert.assertEquals("That Artist", artistName);
    }

    @Test
    public void testExtractArtistFromExistingFLACFile() throws Exception {
        File file = new File(classLoader.getResource("empty.flac").getFile());
        String artistName = extractor.extractArtist(file.toPath());
        Assert.assertEquals("That Artist", artistName);
    }

    @Test
    public void testExtractArtistFromExistingOggFile() throws Exception {
        File file = new File(classLoader.getResource("empty.ogg").getFile());
        String artistName = extractor.extractArtist(file.toPath());
        Assert.assertEquals("That Artist", artistName);
    }

    @Test
    public void testExtractArtistFromExistingWMAFile() throws Exception {
        File file = new File(classLoader.getResource("empty.wma").getFile());
        String artistName = extractor.extractArtist(file.toPath());
        Assert.assertEquals("That Artist", artistName);
    }

}
