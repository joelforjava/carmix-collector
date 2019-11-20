package com.joelforjava.request;

import java.nio.file.Path;

public class CopyRequest {

    private final Path inPath;
    private final Path outPath;
    private final boolean overwriteExisting;

    // TODO - consider a params object or map
    public CopyRequest(Path inPath, Path outPath, boolean overwriteExisting) {
        this.inPath = inPath;
        this.outPath = outPath;
        this.overwriteExisting = overwriteExisting;
    }

    public Path getInPath() {
        return inPath;
    }

    public Path getOutPath() {
        return outPath;
    }

    public boolean isOverwriteExisting() {
        return overwriteExisting;
    }
}
