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

    private static final String FILE_SEPARATOR = File.separator;

    @Test
    public void testBasicDesiredFormatIsValid() {
        String desiredFormat = generateTestDesiredFormat("{OUTPUT_DIR}", "{ARTIST}");
        OutputFormat outputFormat = new OutputFormat(desiredFormat);

        Assert.assertTrue(outputFormat.validate());

    }

    @Test
    public void testMismatchedBracketsCausesDesiredFormatToBeInvalid() {
        String desiredFormat = generateTestDesiredFormat("{OUTPUT_DIR", "{ARTIST}");
        expectedException.expect(IllegalArgumentException.class);
        new OutputFormat(desiredFormat);
    }

    @Test // TODO - however, how do we trim the spaces out? DO we trim the spaces out?
    public void testDesiredFormatCanHaveSpaces() {
        String desiredFormat = generateTestDesiredFormat("{OUTPUT_DIR} ", " {ARTIST}");
        OutputFormat outputFormat = new OutputFormat(desiredFormat);

        Assert.assertTrue(outputFormat.validate());
    }

    @Test
    public void testDesiredFormatWithUnknownTokenIsInvalid() {
        String desiredFormat = generateTestDesiredFormat("{OUTPUT_DIRECTORY}", "{ARTIST}");
        expectedException.expect(IllegalArgumentException.class);
        new OutputFormat(desiredFormat);
    }

    @Test
    public void testDesiredFormatWithoutOutputDirTokenFirstIsInvalid() {
        String desiredFormat = generateTestDesiredFormat("{ARTIST}", "{OUTPUT_DIR}");
        Assert.assertFalse(OutputFormat.validate(desiredFormat));
    }

    @Test
    public void testDesiredFormatWithNonTokensIsValid() {
        String desiredFormat = generateTestDesiredFormat("{OUTPUT_DIR}", "subdirectory", "{ARTIST}");
        OutputFormat outputFormat = new OutputFormat(desiredFormat);

        Assert.assertTrue(outputFormat.validate());
    }

    @Test
    public void testOutputFormatWillProduceExcpectedFormat() throws Exception {
        String desiredFormat = generateTestDesiredFormat("{OUTPUT_DIR}", "{ARTIST}", "{FILE_NAME}");
        OutputFormat outputFormat = new OutputFormat(desiredFormat);
        String outputDirectory = "/my/output/directory";
        String filePath = loadTestFileNameFromResources("empty.mp3");
        MusicFileData musicFileData = new MusicFileData(filePath).withArtistName("TEST_ARTIST");

        String outputUri = outputFormat.produceFormatted(musicFileData, outputDirectory);
        String expectedOutput = outputDirectory + File.separator + "TEST_ARTIST" + File.separator + "empty.mp3";
        Assert.assertEquals(expectedOutput, outputUri);

    }

    @Test
    public void testAttemptingToReplaceValidTokenWithInvalidTokenResultsInException() {
        OutputFormat outputFormat = new OutputFormat(generateTestDesiredFormat("{OUTPUT_DIR} ", " {ARTIST}"));
        expectedException.expect(IllegalArgumentException.class);
        outputFormat.withDesiredFormat("{OUTPUT_DIRECTORY}/{ARTIST}");
    }

    @Test
    public void testAttemptingToProduceExpectedFormatBeforeSettingDesiredFormatResultsInException() throws Exception {
        String filePath = loadTestFileNameFromResources("empty.mp3");
        MusicFileData musicFileData = new MusicFileData(filePath).withArtistName("TEST_ARTIST");
        expectedException.expect(IllegalStateException.class);
        new OutputFormat().produceFormatted(musicFileData, "/any/directory");
    }

    private String loadTestFileNameFromResources(String testFileName) {
        return this.getClass().getClassLoader().getResource(testFileName).getFile();
    }

    private String generateTestDesiredFormat(String... tokens) {
        return String.join(FILE_SEPARATOR, tokens);
    }

}
