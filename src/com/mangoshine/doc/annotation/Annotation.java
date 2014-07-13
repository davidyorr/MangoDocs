package com.mangoshine.doc.annotation;

import java.util.HashMap;
import java.util.Map;

import com.mangoshine.doc.construct.EntrySet;

public enum Annotation {
    // entry types
    CLASS               (null),
    CONSTRUCTOR         (null),
    EVENT               (null),
    NAMESPACE           (null),
    INSTANCEMETHOD      (null),
    INSTANCEPROPERTY    (null),
    STATICMETHOD        (null),
    STATICPROPERTY      (null),
    // other
    AUTHOR              (null),
    DEFAULT             (null),
    EXAMPLE             (null),
    PARAM               (null),
    RETURN              (null),
    VERSION             (null),

    NONE                (null);

    /* The name value of the most recent annotation
       of that type that was parsed.
       This is part of the key value in the Map of Entries. */
    private String currentName;

    Annotation(String currentName) {
        this.currentName = currentName;
    }

    public String getCurrentName() {
        return currentName;
    }

    public void setCurrentName(String value) {
        currentName = value;
    }

    public void setCurrentName(String parentName, String value) {
        setCurrentName(EntrySet.generateKey(parentName, value));
    }

    /**
     * Allows annotations to be declared with short hand versions.
     */
    public static Annotation getAlias(String str) {
        switch (str.toUpperCase()) {
            case "CTOR" : return Annotation.CONSTRUCTOR;
            case "EVT"  : return Annotation.EVENT;
            case "EX"   : return Annotation.EXAMPLE;
            case "IM"   : return Annotation.INSTANCEMETHOD;
            case "IP"   : return Annotation.INSTANCEPROPERTY;
            case "RET"  : return Annotation.RETURN;
            case "SM"   : return Annotation.STATICMETHOD;
            case "SP"   : return Annotation.STATICPROPERTY;
            default     : return null;
        }
    }

    /* The current annotation type being parsed */
    public static Annotation current = null;

    /* The current class or method being parsed */
    public static Annotation currentClassOrMethod = null;

    /* The current class or namespace being parsed.
       This is used to determine what entry an
       instance property belongs to */
    public static Annotation currentClassOrNamespace = null;

    /* A reference for checking if an annotation is valid
       so that we don't recreate a reference over and over. */
    public static Annotation validChecker = null;

    /**
     * Sets the current annotation from the String value.
     */
    public static void setCurrent(String value) {
        value = value.toUpperCase();
        try {
            current = Annotation.valueOf(value);
        } catch (IllegalArgumentException e) {
            current = getAlias(value);
        }
    }

    /* Sets the current class or method from the String value */
    public static void setCurrentClassOrMethod(String value) {
        try {
            currentClassOrMethod = Annotation.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {

        }
    }

    /* Set the current class or namespace from the String value */
    public static void setCurrentClassOrNamespace(String value) {
        try {
            currentClassOrNamespace = Annotation.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {

        }
    }

    /**
     * Gets the current annotation being parsed.
     */
    public static Annotation getCurrent() {
        return current;
    }

    /**
     * Checks if the value is a valid annotation type.
     */
    public static boolean isValid(String value) {
        try {
            validChecker = Annotation.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            if (getAlias(value) != null) {
                return true;
            }
            return false;
        }
    }

    /**
     * Helps the Parser.
     * For a single line read, holds the annotation type and remainder
     * of the line.
     */
    public static class Pair {
        public String type;
        public String value;

        public Pair(String type, String value) {
            this.type = type;
            this.value = value;
        }

        public String toString() {
            return type + ", " + value;
        }
    }
}