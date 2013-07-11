package com.mangoshine.doc.annotation;

/**
 * Exception for invalid annotations.
 */
public class InvalidAnnotationException extends AnnotationException {
    public InvalidAnnotationException(String annotation, int lineNum, String filename) {
        super("Invalid annotation", annotation, lineNum, filename);
    }
}