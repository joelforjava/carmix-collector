package com.joelforjava.configuration;

import com.joelforjava.model.OutputFormat;

public class Settings { // TODO - implement Serializable (but look for alternatives to it!)
    private boolean overwriteEnabled;
    private OutputFormat outputFormat;


    public boolean isOverwriteEnabled() {
        return overwriteEnabled;
    }

    public void setOverwriteEnabled(boolean overwriteEnabled) {
        this.overwriteEnabled = overwriteEnabled;
    }

    public OutputFormat getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(OutputFormat outputFormat) {
        this.outputFormat = outputFormat;
    }
}
