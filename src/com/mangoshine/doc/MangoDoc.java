package com.mangoshine.doc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mangoshine.doc.construct.Documentation;
import com.mangoshine.doc.logging.Logger;
import com.mangoshine.doc.parsing.Parser;
import com.mangoshine.doc.writing.Writer;

public class MangoDoc {
    /**
     * Entry point.
     * Try to catch all errors here.
     */
    public static void main(String[] args) {
        try {
            // Set up configuration
            parseArguments(args);

            // Parse the files
            Parser parser = new Parser();
            Documentation jsdoc = parser.parse();

            // Write to output
            Logger.log("Generating documentation");
            Writer writer = new Writer();
            writer.writeToFile(jsdoc);
            Logger.log("Complete");
        } catch (Exception e) {
            System.err.println("\nError : \n");
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Parses the arguments and generates the Config.
     */
    private static void parseArguments(String[] args) {
        Config config = Config.INSTANCE;

        for (int i = 0; i < args.length; i++) {
            switch (args[i].charAt(0)) {
                case '-':
                    if (args[i].equals("-i") || args[i].equals("--input")) {
                        while (hasNextArg(args, i) && nextArgIsValue(args, i)) {
                            config.addInput(args[++i]);
                        }
                    } else if (args[i].equals("-o") || args[i].equals("--output")) {
                        if (hasNextArg(args, i) && nextArgIsValue(args, i)) {
                            config.setOutput(args[++i]);
                        }
                    } else if (args[i].equals("-s") || args[i].equals("--silent")) {
                        config.setSilent(true);
                    } else if (args[i].equals("-h") || args[i].equals("--help")) {
                        printUsage();
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid argument : " + args[i]);
            }
        }

        if (!config.check()) {
            printUsage();
        }
    }

    /**
     * Print the usage message and exit.
     */
    private static void printUsage() {
        System.out.println();
        System.out.println("usage: java -jar mangodoc [options] [-i input files|directories] [-o output directory]");
        System.out.println();
        System.exit(-1);
    }

    /**
     * Returns true if there is another argument.
     */
    private static boolean hasNextArg(String[] args, int currIndex) {
        return args.length > currIndex+1;
    }

    /**
     * Returns true if the next argument is a value (rather than a flag)
     */
    private static boolean nextArgIsValue(String[] args, int currIndex) {
        if (hasNextArg(args, currIndex)) {
            if (args[currIndex+1].charAt(0) != '-') {
                return true;
            }
        }
        return false;
    }
}