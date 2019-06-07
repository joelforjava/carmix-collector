package com.joelforjava.processor;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MusicFileDataExtractor implements AudioFileMetadataExtractor {

    public String extractArtist(Path source) {
        String artistName = "";
        final String fileName = source.getFileName().toString();
        if (Files.exists(source)) {
            try {
                AudioFile file = AudioFileIO.read(source.toFile());
                Tag tag = file.getTag();
                artistName = tag.getFirst(FieldKey.ARTIST);
            } catch (CannotReadException | IOException e) {
                e.printStackTrace();
                LOGGER.log(Level.SEVERE, "Unable to read data file " + fileName, e);
            } catch (TagException e) {
                e.printStackTrace();
                LOGGER.log(Level.SEVERE, "Unable to read audio tags for file " + fileName, e);
            } catch (ReadOnlyFileException e) {
                e.printStackTrace();
                LOGGER.log(Level.SEVERE, "File " + fileName + " is read only.", e);
            } catch (InvalidAudioFrameException e) {
                e.printStackTrace();
                LOGGER.log(Level.SEVERE, "File " + fileName + " is invalid.", e);
            }
        }

        return artistName;
    }

    private static final Logger LOGGER = Logger.getLogger(MusicFileDataExtractor.class.getName());
}
