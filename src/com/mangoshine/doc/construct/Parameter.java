package com.mangoshine.doc.construct;

import com.mangoshine.doc.parsing.Line;

/**
 * A parameter in a constructor or method.
 */
public class Parameter {
    private String name;
    private String argType;
    private String description;

    public Parameter(String name, String argType, String description) {
        this.name = name;
        this.argType = argType;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getArgType() {
        return this.argType;
    }

    public String getDescription() {
        return Line.handleLinks(this.description);
    }

    public String getPlainDescription() {
        return this.description;
    }

    public void appendToDescription(String text) {
        this.description = this.description + " " + text;
    }

    public void setArgType(String argType) {
        this.argType = argType;
    }

    public String toString() {
        return String.format("{%s} %s : %s", argType, name, description);
    }
}