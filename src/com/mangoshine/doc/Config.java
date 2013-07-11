package com.mangoshine.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class. These values are set when parsing
 * the command line arguments.
 */
public enum Config {
    INSTANCE;

    static private List<String> input = new ArrayList<>();
    static private String output;
    static private File outputDir;
    static private boolean silent = false;

    static public void addInput(String name) {
        input.add(name);
    }

    static public void setOutput(String name) {
        output = name;
    }

    static public void setSilent(boolean b) {
        silent = b;
    }

    static public List getInputs() {
        return input;
    }

    static public File getOutputDir() {
        if (outputDir == null) {
            outputDir = new File(System.getProperty("user.dir")+"/"+output);
        }
        return outputDir;
    }

    static public boolean isSilent() {
        return silent;
    }

    /**
     * Checks if all configuration has been set.
     * Returns true if everything has been set.
     */
    static public boolean check() {
        return input.size() > 0 && output != null && !output.equals("");
    }
}