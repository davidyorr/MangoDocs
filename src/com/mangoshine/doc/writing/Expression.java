package com.mangoshine.doc.writing;

/**
 * Represents expressions in the layout files.
 * ( specified by {{expression}} )
 */
public enum Expression {
    CONTENT,
    CSS_IMPORT,
    FOOTER,
    JS_IMPORT,
    SIDEBAR,
    TITLE;

    /**
     * Searches for the expression with the given value.
     * Returns null if none found.
     */
    static public Expression lookup(String value) {
        for (Expression exp : Expression.values()) {
            if (exp.name().equalsIgnoreCase(value)) {
                return exp;
            }
        }
        return null;
    }
}