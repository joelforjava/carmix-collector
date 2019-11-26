package com.joelforjava.model;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;

public class OutputFormatTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final TemporaryFolder temporaryInFolder = new TemporaryFolder();

    @Test
    public void testBasicDesiredFormatIsValid() {
        String desiredFormat = "{OUTPUT_DIR}/{ARTIST}";
        OutputFormat outputFormat = new OutputFormat(desiredFormat);

        Assert.assertTrue(outputFormat.validate());

    }

    @Test
    public void testMismatchedBracketsCausesDesiredFormatToBeInvalid() {
        String desiredFormat = "{OUTPUT_DIR/{ARTIST}";
        expectedException.expect(IllegalArgumentException.class);
        new OutputFormat(desiredFormat);
    }

    @Test // TODO - however, how do we trim the spaces out? DO we trim the spaces out?
    public void testDesiredFormatCanHaveSpaces() {
        String desiredFormat = "{OUTPUT_DIR} / {ARTIST}";
        OutputFormat outputFormat = new OutputFormat(desiredFormat);

        Assert.assertTrue(outputFormat.validate());
    }

    @Test
    public void testDesiredFormatWithUnknownTokenIsInvalid() {
        String desiredFormat = "{OUTPUT_DIRECTORY}/{ARTIST}";
        expectedException.expect(IllegalArgumentException.class);
        new OutputFormat(desiredFormat);
    }

    @Test
    public void testDesiredFormatWithNonTokensIsValid() {
        String desiredFormat = "{OUTPUT_DIR}/subdirectory/{ARTIST}";
        OutputFormat outputFormat = new OutputFormat(desiredFormat);

        Assert.assertTrue(outputFormat.validate());
    }

    @Test
    public void testOutputFormatWillProduceExcpectedFormat() throws Exception {
        String desiredFormat = "{OUTPUT_DIR}/{ARTIST}/{FILE_NAME}";
        OutputFormat outputFormat = new OutputFormat(desiredFormat);
        String outputDirectory = "/my/output/directory";
        File inFile = temporaryInFolder.newFile("testIn.mp3");
        MusicFileData musicFileData = new MusicFileData(inFile.getCanonicalPath()).withArtistName("TEST_ARTIST");

        String outputUri = outputFormat.produceFormatted(musicFileData, outputDirectory);
        String expectedOutput = outputDirectory + File.separator + "TEST_ARTIST" + File.separator + "testIn.mp3";
        Assert.assertEquals(expectedOutput, outputUri);

    }

    @Test
    public void testAttemptingToReplaceValidTokenWithInvalidTokenResultsInException() {
        OutputFormat outputFormat = new OutputFormat("{OUTPUT_DIR} / {ARTIST}");
        expectedException.expect(IllegalArgumentException.class);
        outputFormat.withDesiredFormat("{OUTPUT_DIRECTORY}/{ARTIST}");
    }
}
