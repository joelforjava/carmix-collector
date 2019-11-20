package com.joelforjava.service;

import com.joelforjava.request.CopyRequest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class CopyFileServiceTest {

    @Rule
    public final TemporaryFolder temporaryInFolder = new TemporaryFolder();
    @Rule
    public final TemporaryFolder temporaryOutFolder = new TemporaryFolder();

    @Test
    public void testCopy() throws Exception {
        File inFile = temporaryInFolder.newFile("testIn.mp3");

        BufferedWriter writer = new BufferedWriter(new FileWriter(inFile));
        writer.write("#EXTM3U");
        writer.newLine();
        writer.write("#EXTINF A Great Band - That Song You Remember");
        writer.newLine();
        writer.write("M:\\Temp\\File.mp3");
        writer.newLine();
        writer.close();

        File outFile = temporaryOutFolder.newFile("testOut.mp3");

        CopyFileService service = new CopyFileService();
        service.copy(inFile.toPath(), outFile.toPath());

        Assert.assertTrue(outFile.exists());
        Assert.assertNotEquals(0, outFile.length());
        Assert.assertEquals(inFile.length(), outFile.length());

    }

    @Test
    public void testCopyUsingRequestObject() throws Exception {
        File inFile = temporaryInFolder.newFile("testIn.mp3");

        BufferedWriter writer = new BufferedWriter(new FileWriter(inFile));
        writer.write("#EXTM3U");
        writer.newLine();
        writer.write("#EXTINF A Great Band - That Other Song You Remember");
        writer.newLine();
        writer.write("M:\\Temp\\File.mp3");
        writer.newLine();
        writer.close();

        File outFile = temporaryOutFolder.newFile("testOut.mp3");

        CopyFileService service = new CopyFileService();
        CopyRequest request = new CopyRequest(inFile.toPath(), outFile.toPath(), false);
        service.copy(request);

        Assert.assertTrue(outFile.exists());
        Assert.assertNotEquals(0, outFile.length());
        Assert.assertEquals(inFile.length(), outFile.length());

    }

}
