package com.joelforjava.processor;

import com.joelforjava.service.CopyFileService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class MusicFileDataProcessorTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final TemporaryFolder temporaryInFolder = new TemporaryFolder();
    @Rule
    public final TemporaryFolder temporaryOutFolder = new TemporaryFolder();

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
}
