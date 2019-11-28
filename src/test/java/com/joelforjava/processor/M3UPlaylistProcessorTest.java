package com.joelforjava.processor;

import com.joelforjava.model.MusicFileData;
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

        String fileName = loadTestFileNameFromResources("empty.mp3");
        List<String> lines = new ArrayList<>() {{
            add("#EXTM3U");
            add("#EXTINF A Great Band - That Song You Remember");
            add(fileName);
        }};

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(inFile))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
        Path p = inFile.toPath();

        M3UPlaylistProcessor processor = new M3UPlaylistProcessor();
        List<MusicFileData> extracted = processor.process(p);

        Assert.assertEquals(1, extracted.size());
    }

    @Test
    public void testWithMultipleEntries() throws Exception {
        File inFile = temporaryFolder.newFile("testIn.m3u");

        List<String> lines = new ArrayList<String>() {{
            add("#EXTM3U");
            add("#EXTINF A Great Band - That Song You Remember");
            add(loadTestFileNameFromResources("empty.mp3"));
            add("#EXTINF A Great Band - That Other Song You Remember");
            add(loadTestFileNameFromResources("empty.flac"));
            add("#EXTINF What Band Is This - That One Song by That One Band");
            add(loadTestFileNameFromResources("empty.ogg"));
        }};

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(inFile))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
        Path p = inFile.toPath();

        M3UPlaylistProcessor processor = new M3UPlaylistProcessor();
        List<MusicFileData> extracted = processor.process(p);

        Assert.assertEquals(3, extracted.size());
    }

    @Test
    public void testWithNoInfoEntries() throws Exception {
        File inFile = temporaryFolder.newFile("testIn.m3u");

        List<String> lines = new ArrayList<String>() {{
            add("#EXTM3U");
            add(loadTestFileNameFromResources("empty.mp3"));
            add(loadTestFileNameFromResources("empty-alac.m4a"));
            add(loadTestFileNameFromResources("empty.wma"));
        }};

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(inFile))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
        Path p = inFile.toPath();

        M3UPlaylistProcessor processor = new M3UPlaylistProcessor();
        List<MusicFileData> extracted = processor.process(p);

        Assert.assertEquals(3, extracted.size());
    }

    @Test
    public void testWithNoM3UHeader() throws Exception {
        File inFile = temporaryFolder.newFile("testIn.m3u");

        List<String> lines = new ArrayList<String>() {{
            add("#EXTINF A Great Band - That Song You Remember");
            add(loadTestFileNameFromResources("empty.mp3"));
        }};

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(inFile))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
        Path p = inFile.toPath();

        M3UPlaylistProcessor processor = new M3UPlaylistProcessor();
        List<MusicFileData> extracted = processor.process(p);

        Assert.assertTrue(extracted.isEmpty());
    }

    @Test
    public void testWithNullPath() {
        M3UPlaylistProcessor processor = new M3UPlaylistProcessor();
        expectedException.expect(NullPointerException.class);
        processor.process(null);
    }

    private String loadTestFileNameFromResources(String testFileName) {
        return this.getClass().getClassLoader().getResource(testFileName).getFile();
    }
}
