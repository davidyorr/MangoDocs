package com.mangoshine.doc.annotation;

/**
 * Base Exception class for annotations.
 */
public abstract class AnnotationException extends Exception {
    public AnnotationException(String title, String annotation, int lineNum, String filename) {
        super(String.format(
            "%s"+
            "\n%-11s: %s"+
            "\n%-11s: %s"+
            "\n%-11s: %s",

            title,
            "annotation", annotation,
            "at line", lineNum,
            "in file", filename)
        );
    }
}