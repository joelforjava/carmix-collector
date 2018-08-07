package com.joelforjava.processor;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class M3UPlaylistProcessorTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testWithSingleEntry() throws Exception {
        File inFile = temporaryFolder.newFile("testIn.m3u");

        List<String> lines = new ArrayList<String>() {{
            add("#EXTM3U");
            add("#EXTINF A Great Band - That Song You Remember");
            add("M:\\Temp\\File.mp3");
        }};

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(inFile))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
        Path p = inFile.toPath();

        M3UPlaylistProcessor processor = new M3UPlaylistProcessor();
        List<String> extracted = processor.extractURIs(p);

        Assert.assertEquals(1, extracted.size());
    }

    @Test
    public void testWithMultipleEntries() throws Exception {
        File inFile = temporaryFolder.newFile("testIn.m3u");

        List<String> lines = new ArrayList<String>() {{
            add("#EXTM3U");
            add("#EXTINF A Great Band - That Song You Remember");
            add("M:\\Temp\\File.mp3");
            add("#EXTINF A Great Band - That Other Song You Remember");
            add("M:\\Temp\\File2.mp3");
            add("#EXTINF What Band Is This - That One Song by That One Band");
            add("M:\\Temp\\File3.mp3");
        }};

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(inFile))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
        Path p = inFile.toPath();

        M3UPlaylistProcessor processor = new M3UPlaylistProcessor();
        List<String> extracted = processor.extractURIs(p);

        Assert.assertEquals(3, extracted.size());
    }

    @Test
    public void testWithNoInfoEntries() throws Exception {
        File inFile = temporaryFolder.newFile("testIn.m3u");

        List<String> lines = new ArrayList<String>() {{
            add("#EXTM3U");
            add("M:\\Temp\\File.mp3");
            add("M:\\Temp\\File2.mp3");
            add("M:\\Temp\\File3.mp3");
        }};

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(inFile))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
        Path p = inFile.toPath();

        M3UPlaylistProcessor processor = new M3UPlaylistProcessor();
        List<String> extracted = processor.extractURIs(p);

        Assert.assertEquals(3, extracted.size());
    }

    @Test
    public void testWithNoM3UHeader() throws Exception {
        File inFile = temporaryFolder.newFile("testIn.m3u");

        List<String> lines = new ArrayList<String>() {{
            add("#EXTINF A Great Band - That Song You Remember");
            add("M:\\Temp\\File.mp3");
        }};

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(inFile))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
        Path p = inFile.toPath();

        M3UPlaylistProcessor processor = new M3UPlaylistProcessor();
        List<String> extracted = processor.extractURIs(p);

        Assert.assertTrue(extracted.isEmpty());
    }

    @Test
    public void testWithNullPath() {
        M3UPlaylistProcessor processor = new M3UPlaylistProcessor();
        expectedException.expect(NullPointerException.class);
        processor.extractURIs(null);
    }
}
