package com.joelforjava.processor;

import com.joelforjava.model.MusicFileData;
import com.joelforjava.service.CopyFileService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;
import java.util.List;

public class MusicFileDataProcessorTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final TemporaryFolder temporaryInFolder = new TemporaryFolder();
    @Rule
    public final TemporaryFolder temporaryOutFolder = new TemporaryFolder();

    private static final String FILE_SEPARATOR = File.separator;

    @Test
    public void testCreatingWithNullOutputDirectoryName() {
        expectedException.expect(NullPointerException.class);
        MusicFileDataProcessor processor = new MusicFileDataProcessor(null, "format", true, new CopyFileService());
    }

    @Test
    public void testCreatingWIthNullFormatString() {
        expectedException.expect(NullPointerException.class);
        MusicFileDataProcessor processor = new MusicFileDataProcessor("outputDirectory", null, true, new CopyFileService());
    }

    @Test
    public void testCreatingWithNullCopyFileService() {
        expectedException.expect(NullPointerException.class);
        MusicFileDataProcessor processor = new MusicFileDataProcessor("outputDirectory", "format", true, null);
    }

    @Test
    public void testProcessorParsesOutputFormatAsExpected() {
        // given:
        File outputDirectory = temporaryOutFolder.getRoot();
        String outputFormat = "{OUTPUT_DIR}" + FILE_SEPARATOR + "{ARTIST}";
        boolean overwriteExisting = false;
        CopyFileService mockCopyFileService = Mockito.mock(CopyFileService.class);

        MusicFileDataProcessor processor = new MusicFileDataProcessor(outputDirectory.getAbsolutePath(), outputFormat, overwriteExisting, mockCopyFileService);

        List<String> tokens = processor.getFormatTokens();
        Assert.assertEquals(2, tokens.size());
        Assert.assertEquals("OUTPUT_DIR", tokens.get(0));
        Assert.assertEquals("ARTIST", tokens.get(1));
    }

    @Test
    public void testProcessingASingleValidDataFileWithNoOverwriting() throws Exception {
        // given:
        File outputDirectory = temporaryOutFolder.getRoot();
        String outputFormat = "{OUTPUT_DIR}" + FILE_SEPARATOR + "{ARTIST}";
        boolean overwriteExisting = false;
        CopyFileService mockCopyFileService = Mockito.mock(CopyFileService.class);

        MusicFileDataProcessor processor = new MusicFileDataProcessor(outputDirectory.getAbsolutePath(), outputFormat, overwriteExisting, mockCopyFileService);

        // and:
        File inFile = temporaryInFolder.newFile("testIn.mp3");
        MusicFileData musicFileData = new MusicFileData(inFile.getAbsolutePath()).withArtistName("TEST_ARTIST");

        processor.process(musicFileData);
        // The method is void. We don't have a way to verify anything yet

    }

    @Test
    public void testProcessingASingleInvalidDataFileResultsInNoFileWritten() throws Exception {
        // given:
        File outputDirectory = temporaryOutFolder.getRoot();
        String outputFormat = "{OUTPUT_DIR}" + FILE_SEPARATOR + "{ARTIST}";
        boolean overwriteExisting = false;
        CopyFileService mockCopyFileService = Mockito.mock(CopyFileService.class);

        MusicFileDataProcessor processor = new MusicFileDataProcessor(outputDirectory.getAbsolutePath(), outputFormat, overwriteExisting, mockCopyFileService);

        // and:
        String invalidPath = outputDirectory.getAbsolutePath() + FILE_SEPARATOR + "INVALID_PATHE";
        MusicFileData musicFileData = new MusicFileData(invalidPath).withArtistName("TEST_ARTIST");

        processor.process(musicFileData);
        // The method is void. We don't have a way to verify anything yet
    }
}
