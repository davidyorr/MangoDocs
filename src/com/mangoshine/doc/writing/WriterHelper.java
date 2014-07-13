package com.mangoshine.doc.writing;

import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import com.mangoshine.doc.asset.AssetLibrary;
import com.mangoshine.doc.asset.InvalidAssetException;
import com.mangoshine.doc.dom.DOMNode;
import com.mangoshine.doc.dom.DOMBuilder;
import com.mangoshine.doc.construct.Documentation;
import com.mangoshine.doc.construct.Entry;

public enum WriterHelper {
    INSTANCE;

    public Entry currentEntry = null;

    // private String baseDirString = System.getProperty("user.dir");
    // private File baseDir = new File(baseDirString+"/output");

    /**
     * Checks if the line contains an expression.
     * Returns the Expression if found, null if not.
     */
    private Expression checkForExpression(String line) {
        try {
            String expression = line.substring(line.indexOf("{{")+2, line.indexOf("}}"));
            return Expression.lookup(expression);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Returns the position of the expression.
     *
     * @return position     int[start, end]
     */
    private int[] expressionStartEnd(String expressionName, StringBuilder sb) {
        int[] startEnd = new int[2];
        String expression = "{{" + expressionName.toLowerCase() + "}}";
        startEnd[0] = sb.indexOf(expression);
        startEnd[1] = startEnd[0]+expression.length();

        return startEnd;
    }

    /**
     * Handles the expression.
     */
    public String handleExpression(Expression expression) {
        switch (expression) {
            case CONTENT:       return handleContent();
            case SIDEBAR:       return handleSidebar();
            case FOOTER:        return handleFooter();
            case CSS_IMPORT:    return handleCssImport();
            case TITLE:         return handleTitle();
            default:            return "";
        }
    }

    private String handleSidebar() {
        return DOMBuilder.buildSidebarHTML();
    }

    private String handleFooter() {
        return DOMBuilder.buildFooterHTML();
    }

    private String handleContent() {
        return DOMBuilder.buildContentHTML();
    }

    private String handleCssImport() {
        return "style.css";
    }

    private String handleTitle() {
        return DOMBuilder.buildTitleHTML();
    }

    /**
     * builds the HTML for a class.
     * This method is not in DOMBuilder because we need to
     * read from a file.
     */
    public String buildClassHTML(Entry entry) throws IOException,
                                                     InvalidAssetException {
        currentEntry = entry;
        StringBuilder sb = new StringBuilder();
        InputStream base = AssetLibrary.getAsset("base.html");

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(base))) {
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");

                Expression expression = checkForExpression(line);
                if (expression != null) {
                    int[] startEnd = expressionStartEnd(expression.name(), sb);
                    sb.replace(startEnd[0], startEnd[1],
                            handleExpression(expression));
                }
            }
        }

        return sb.toString();
    }

    /**
     * Builds the HTML for the index file.
     * This method is not in DOMBuilder because we need to
     * read from a file.
     */
    public String buildIndexHTML() throws IOException,
                                          InvalidAssetException {
        StringBuilder sb = new StringBuilder();
        InputStream base = AssetLibrary.getAsset("base.html");

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(base))) {
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");

                Expression expression = checkForExpression(line);
                if (expression != null) {
                    int[] startEnd = expressionStartEnd(expression.name(), sb);
                    sb.replace(startEnd[0], startEnd[1],
                            handleExpression(expression));
                }
            }
        }

        return sb.toString();
    }

    /**
     * Copies the src file to the dest file.
     */
    public void copyFile(File src, File dst) throws IOException {
        if (src.isDirectory()) {
            copyDirectory(src, dst);
        } else {
            Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Copies the contents found in the given InputStream to a file with the
     * given name (into the output folder specified by the Config).
     */
    public void copyInputStream(InputStream src, String name, File outputDir) throws IOException {
        File dst = new File(outputDir, name);
        if (name.charAt(name.length()-1) == '/') {
            dst.mkdirs();
            return;
        }
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(dst));
        try (BufferedInputStream br = new BufferedInputStream(src)) {
            byte[] buffer = new byte[65536];
            int len;
            while ((len = br.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.close();
        }
    }

    /**
     * Copies the src directory to the dst directory recursively.
     */
    private void copyDirectory(File src, File dst) throws IOException {
        dst.mkdirs();
        String filename;
        File[] files = src.listFiles();
        for (File file : files) {
            filename = file.getName();
            copyFile(file, new File(dst.getPath()+File.separator+
                    filename.substring(filename.lastIndexOf("/")+1,
                    filename.length())));
        }
    }
}