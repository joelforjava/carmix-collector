package com.joelforjava.processor;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@PrepareForTest(M3UPlaylistProcessor.class)
@RunWith(PowerMockRunner.class)
public class M3UPlaylistProcessorTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testWithSingleEntry() throws Exception {
        Path p = Paths.get("Test");

        List<String> lines = new ArrayList<String>() {{
            add("#EXTM3U");
            add("#EXTINF A Great Band - That Song You Remember");
            add("M:\\Temp\\File.mp3");
        }};

        PowerMockito.mockStatic(Files.class);
        Mockito.when(Files.readAllLines(p, StandardCharsets.ISO_8859_1)).thenReturn(lines);

        M3UPlaylistProcessor processor = new M3UPlaylistProcessor();
        List<String> extracted = processor.extractURIs(p);

        Assert.assertEquals(1, extracted.size());
    }

    @Test
    public void testWithMultipleEntries() throws Exception {
        Path p = Mockito.mock(Path.class);

        List<String> lines = new ArrayList<String>() {{
            add("#EXTM3U");
            add("#EXTINF A Great Band - That Song You Remember");
            add("M:\\Temp\\File.mp3");
            add("#EXTINF A Great Band - That Other Song You Remember");
            add("M:\\Temp\\File2.mp3");
            add("#EXTINF What Band Is This - That One Song by That One Band");
            add("M:\\Temp\\File3.mp3");
        }};

        PowerMockito.mockStatic(Files.class);
        Mockito.when(Files.readAllLines(p, StandardCharsets.ISO_8859_1)).thenReturn(lines);

        M3UPlaylistProcessor processor = new M3UPlaylistProcessor();
        List<String> extracted = processor.extractURIs(p);

        Assert.assertEquals(3, extracted.size());
    }

    @Test
    public void testWithNoInfoEntries() throws Exception {
        Path p = Mockito.mock(Path.class);

        List<String> lines = new ArrayList<String>() {{
            add("#EXTM3U");
            add("M:\\Temp\\File.mp3");
            add("M:\\Temp\\File2.mp3");
            add("M:\\Temp\\File3.mp3");
        }};

        PowerMockito.mockStatic(Files.class);
        Mockito.when(Files.readAllLines(p, StandardCharsets.ISO_8859_1)).thenReturn(lines);

        M3UPlaylistProcessor processor = new M3UPlaylistProcessor();
        List<String> extracted = processor.extractURIs(p);

        Assert.assertEquals(3, extracted.size());
    }

    @Test
    public void testWithNoM3UHeader() throws Exception {
        Path p = Mockito.mock(Path.class);

        List<String> lines = new ArrayList<String>() {{
            add("#EXTINF A Great Band - That Song You Remember");
            add("M:\\Temp\\File.mp3");
        }};

        PowerMockito.mockStatic(Files.class);
        Mockito.when(Files.readAllLines(p, StandardCharsets.ISO_8859_1)).thenReturn(lines);

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
