package com.joelforjava.processor;

import com.joelforjava.model.MusicFileData;
import com.joelforjava.model.OutputFormat;
import com.joelforjava.service.CopyFileService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

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
        new MusicFileDataProcessor(null, new OutputFormat(), true, new CopyFileService());
    }

    @Test
    public void testCreatingWithNullFormatString() {
        expectedException.expect(NullPointerException.class);
        new MusicFileDataProcessor("outputDirectory", null, true, new CopyFileService());
    }

    @Test
    public void testCreatingWithNullCopyFileService() {
        expectedException.expect(NullPointerException.class);
        new MusicFileDataProcessor("outputDirectory", new OutputFormat(), true, null);
    }

    @Test
    public void testProcessingASingleValidDataFileWithNoOverwriting() throws Exception {
        // given:
        File outputDirectory = temporaryOutFolder.getRoot();
        String outputFormat = "{OUTPUT_DIR}" + FILE_SEPARATOR + "{ARTIST}" + FILE_SEPARATOR + "{FILE_NAME}";
        boolean overwriteExisting = false;
        CopyFileService mockCopyFileService = Mockito.mock(CopyFileService.class);

        // TODO - should probably mock out the OutputFormat
        MusicFileDataProcessor processor = new MusicFileDataProcessor(
                outputDirectory.getAbsolutePath(),
                new OutputFormat(outputFormat),
                overwriteExisting,
                mockCopyFileService);

        // and:
        String filePath = loadTestFileNameFromResources("empty.mp3");  // XXX: encoded artist name is 'That Artist'
        MusicFileData musicFileData = new MusicFileData(filePath);

        processor.process(musicFileData);
        // The method is void. We don't have a way to verify anything yet

    }

    @Test
    public void testAttemptingToCreateMusicFileDataWithInvalidFilePathResultsInException() throws Exception {
        // when:
        File outputDirectory = temporaryOutFolder.getRoot();
        String invalidPath = outputDirectory.getAbsolutePath() + FILE_SEPARATOR + "INVALID_PATHE";
        // then:
        expectedException.expect(IllegalStateException.class);
        MusicFileData musicFileData = new MusicFileData(invalidPath);

    }

    private String loadTestFileNameFromResources(String testFileName) throws Exception {
        URL fileUri = this.getClass().getClassLoader().getResource(testFileName);
        return Paths.get(fileUri.toURI()).toFile().getCanonicalPath();
    }
}
