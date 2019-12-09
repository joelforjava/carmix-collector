package com.joelforjava.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutputFormat {

    private String desiredFormat;

    private static final Pattern EXTRACT_FORMAT_TOKENS = Pattern.compile("\\{(.*?)}.*?");

    private static final String UNKNOWN_ARTIST = "Unknown Artist";

    private static final String UNKNOWN_ALBUM = "Unknown Album";

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
        if (!newFormat.startsWith(FormatToken.OUTPUT_DIR.asDelimited())) {
            return false;
        }
        Matcher matcher = EXTRACT_FORMAT_TOKENS.matcher(newFormat);
        while (matcher.find()) {
            String currentToken = matcher.group(1);
            try {
                System.out.printf("Checking for token: %s%n", currentToken);
                FormatToken.valueOf(currentToken);
            } catch (IllegalArgumentException iae) {
                System.err.printf("Token %s does not appear to be a valid token!%n", currentToken);
                return false;
            }
        }
        return true;
    }

    public List<FormatToken> validTokens() {
        return Arrays.asList(FormatToken.values());
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
        for (FormatToken token : FormatToken.values()) {
            System.out.printf("Working with Token %s%n", token);
            String delimited = token.asDelimited();
            switch (token) {
                case OUTPUT_DIR:
                    formatted = formatted.replace(delimited, outputDirectory);
                    break;
                case ARTIST:
                    String artistName = fileData.getArtistName();
                    if (artistName != null) {
                        // TODO - naive attempt to prevent colons as part of a path in Windows
                        //      - Will need to do something more comprehensive
                        formatted = formatted.replace(delimited, artistName.replace(":", "-"));
                    } else {
                        formatted = formatted.replace(delimited, UNKNOWN_ARTIST);
                    }
                    break;
                case ALBUM_ARTIST:
                    String albumArtistName = fileData.getAlbumArtistName();
                    if (albumArtistName != null) {
                        formatted = formatted.replace(delimited, albumArtistName.replace(":", "-"));
                    }
                    // TODO - handle the case where Album Artist is not available
                    //      - This is handled in MusicFileData, for now but that class may be deprecated in the future.
                    break;
                case ALBUM_NAME:
                    String albumTitle = fileData.getAlbumName();
                    if (albumTitle != null) {
                        formatted = formatted.replace(delimited, albumTitle.replace(":", "-"));
                    } else {
                        formatted = formatted.replace(delimited, UNKNOWN_ALBUM);
                    }
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

    public enum FormatToken {
        OUTPUT_DIR, ARTIST, ALBUM_ARTIST, ALBUM_NAME, SONG_NAME, FILE_NAME;

        public String asDelimited() {
            return "{" + this.name() + "}";
        }

        private static final Map<String, FormatToken> nameToTokensMap = new LinkedHashMap<>();

        static {
            List.of(FormatToken.values()).forEach(token -> nameToTokensMap.put(token.name(), token));
        }

        public static Optional<FormatToken> lookup(String tokenName) {
            return Optional.of(nameToTokensMap.get(tokenName.toUpperCase()));
        }
    }
}
