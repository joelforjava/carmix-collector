package com.joelforjava.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutputFormat {

    private String desiredFormat;

    private static final Pattern EXTRACT_FORMAT_TOKENS = Pattern.compile("\\{(.*?)}.*?");

    public OutputFormat() {

    }

    public OutputFormat(String desiredFormat) {
        if (!OutputFormat.validate(desiredFormat)) {
            throw new IllegalArgumentException("Invalid Format String provided!");
        }
        this.desiredFormat = desiredFormat;
    }

    public boolean validate() {
        return OutputFormat.validate(this.desiredFormat);
    }

    public static boolean validate(String newFormat) {
        if (newFormat == null) {
            return false;
        }
        if (!newFormat.startsWith(Tokens.OUTPUT_DIR.asDelimited())) {
            return false;
        }
        Matcher matcher = EXTRACT_FORMAT_TOKENS.matcher(newFormat);
        while (matcher.find()) {
            String currentToken = matcher.group(1);
            try {
                System.out.printf("Checking for token: %s%n", currentToken);
                Tokens.valueOf(currentToken);
            } catch (IllegalArgumentException iae) {
                System.err.printf("Token %s does not appear to be a valid token!%n", currentToken);
                return false;
            }
        }
        return true;
    }

    public List<Tokens> validTokens() {
        return Arrays.asList(Tokens.values());
    }

    void setDesiredFormat(String desiredFormat) {
        this.desiredFormat = desiredFormat;
    }

    public OutputFormat withDesiredFormat(String desiredFormat) {
        if (!OutputFormat.validate(desiredFormat)) {
            throw new IllegalArgumentException("Invalid Format String provided!");
        }
        setDesiredFormat(desiredFormat);
        return this;
    }

    public String produceFormatted(MusicFileData fileData, String outputDirectory) {
        if (desiredFormat == null) {
            throw new IllegalStateException("desiredFormat must be set before calling this method!");
        }
        String fileDataUri = fileData.getUri();
        Path source = Paths.get(fileDataUri);
        // TODO - not quite sure why I'm using a Path to get a URI I already have, unless I was using
        //        it to check for existence? - this is code pulled from CarMixCreatorGUI
        String fileName = source.getFileName().toString();
        String formatted = desiredFormat;
        for (Tokens token : Tokens.values()) {
            System.out.printf("Working with Token %s%n", token);
            String delimited = token.asDelimited();
            switch (token) {
                case OUTPUT_DIR:
                    formatted = formatted.replace(delimited, outputDirectory);
                    break;
                case ARTIST:
                    String artistName = fileData.getArtistName();
                    if (artistName != null) {
                        formatted = formatted.replace(delimited, artistName);
                    }
                    break;
                case ALBUM_ARTIST:
                    String albumArtistName = fileData.getAlbumArtistName();
                    if (albumArtistName != null) {
                        formatted = formatted.replace(delimited, albumArtistName);
                    }
                    // TODO - handle the case where Album Artist is not available
                    //      - This is handled in MusicFileData, for now but that class may be deprecated in the future.
                    break;
                case FILE_NAME:
                    formatted = formatted.replace(delimited, fileName);
                    break;
                case SONG_NAME:
                    // TODO - Since MusicFileData ONLY contains ARTIST and ALBUM_ARTIST name,
                    //  we cannot currently handle SONG_NAME
                    break;
                default:
                    break;
            }
        }
        return formatted;
    }

    public enum Tokens {
        OUTPUT_DIR, ARTIST, ALBUM_ARTIST, SONG_NAME, FILE_NAME;

        public String asDelimited() {
            return "{" + this.name() + "}";
        }

        private static final Map<String, Tokens> nameToTokensMap = new LinkedHashMap<>();

        static {
            List.of(Tokens.values()).forEach(token -> nameToTokensMap.put(token.name(), token));
        }

        public static Optional<Tokens> lookup(String tokenName) {
            return Optional.of(nameToTokensMap.get(tokenName.toUpperCase()));
        }
    }
}
