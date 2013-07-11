package com.mangoshine.doc.annotation;

/**
 * Exception for missing annotation values.
 */
public class MissingAnnotationValueException extends AnnotationException {
    public MissingAnnotationValueException(String annotation, int lineNum, String filename) {
        super("Missing annotation value", annotation, lineNum, filename);
    }
}