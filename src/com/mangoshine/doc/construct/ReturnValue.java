package com.mangoshine.doc.construct;

public class ReturnValue {
    private String description, type;

    public ReturnValue() {
        this("","");
    }

    public ReturnValue(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public void appendToDescription(String text) {
        this.description = this.description + " " + text;
    }

    public void setType(String text) {
        this.type = text;
    }

    public String getDescription() {
        return this.description;
    }

    public String getType() {
        if (this.type.length() == 0) {
            return "undefined";
        }
        return this.type;
    }

    public boolean isEmpty() {
        return (description.length() == 0 && type.length() == 0);
    }

    public String toString() {
        return getType() + " - " + getDescription();
    }
}