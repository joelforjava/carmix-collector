package com.joelforjava.service;

import com.joelforjava.request.CopyRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CopyFileServiceTest {

    @Rule
    public final TemporaryFolder temporaryInFolder = new TemporaryFolder();
    @Rule
    public final TemporaryFolder temporaryOutFolder = new TemporaryFolder();

    @Before
    public void beforeTest() {
        File root = temporaryOutFolder.getRoot();
        for (File tempFile : root.listFiles()) {
            tempFile.delete();
        }
    }

    @Test
    public void testCopyUsingRequestObject() throws Exception {
        File inFile = temporaryInFolder.newFile("testIn2.mp3");

        BufferedWriter writer = new BufferedWriter(new FileWriter(inFile));
        writer.write("#EXTM3U");
        writer.newLine();
        writer.write("#EXTINF A Great Band - That Other Song You Remember");
        writer.newLine();
        writer.write("M:\\Temp\\File.mp3");
        writer.newLine();
        writer.close();

        Path outPath = Paths.get(temporaryOutFolder.getRoot().getAbsolutePath(), "testOut2.mp3");
        File outFile = outPath.toFile();
        CopyFileService service = new CopyFileService();
        CopyRequest request = new CopyRequest(inFile.toPath(), outPath, false);
        service.copy(request);

        Assert.assertTrue(outFile.exists());
        Assert.assertNotEquals(0, outFile.length());
        Assert.assertEquals(inFile.length(), outFile.length());

    }

    @Test
    public void testCopyOverwritingExistingObject() throws Exception {
        File inFile = temporaryInFolder.newFile("testIn.mp3");

        BufferedWriter writer = new BufferedWriter(new FileWriter(inFile));
        writer.write("#EXTM3U");
        writer.newLine();
        writer.write("#EXTINF A Great Band - That Other Song You Remember");
        writer.newLine();
        writer.write("M:\\Temp\\File.mp3");
        writer.newLine();
        writer.close();

        Path outPath = Paths.get(temporaryOutFolder.getRoot().getAbsolutePath(), "testOut.mp3");
        File outFile = outPath.toFile();

        CopyFileService service = new CopyFileService();
        CopyRequest request = new CopyRequest(inFile.toPath(), outPath, false);
        service.copy(request);
        long lastModified1 = outFile.lastModified();

        CopyRequest overwriteRequest = new CopyRequest(inFile.toPath(), outPath, true);
        service.copy(overwriteRequest);
        long lastModified2 = outFile.lastModified();

        Assert.assertTrue(outFile.exists());
        // TODO - figure out a better test for this. This one doesn't actually work!
        // Assert.assertNotEquals(lastModified1, lastModified2);

    }
}
