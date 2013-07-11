package com.mangoshine.doc.parsing;

import java.util.HashSet;
import java.util.Set;

import com.mangoshine.doc.annotation.Annotation;
import com.mangoshine.doc.annotation.AnnotationException;
import com.mangoshine.doc.annotation.InvalidAnnotationException;
import com.mangoshine.doc.annotation.MissingAnnotationValueException;
import com.mangoshine.doc.dom.DOMNode;

/**
 * Several static functions to call on single lines while
 * parsing the documentation.
 */
public class Line {
    private static Set<String> reservedWords = initReservedWords();

    private static Set<String> initReservedWords() {
        Set<String> set = new HashSet<>();
        set.add("p");
        set.add("/p");
        set.add("tt");
        set.add("/tt");

        return set;
    }

    /**
     * Handle a line by converting links to 'a' DOM elements.
     * Links are specified by surrounding carets.
     * ex: Handler for <Class>.
     */
    public static String handleLinks(String line) {
        StringBuilder sb = new StringBuilder();
        String linkName;
        DOMNode linkNode;
        int start = 0;
        int end = 0;
        boolean containsStart = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '<') {
                containsStart = true;
                start = i;
            } else if (c == '>') {
                end = i;
            }

            if (containsStart && end > 0) {
                linkName = line.substring(start+1,end);
                if (!reservedWords.contains(linkName)) {
                    linkNode = new DOMNode("tt")
                                        .appendNode(new DOMNode("a", linkName)
                                            .addAttribute("href", linkName+".html"));
                    sb.replace(start, end, linkNode.toString());
                } else {
                    sb.append(c);
                }
                containsStart = false;
                start = 0;
                end = 0;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /* --------------------------------------------------------------------------- */

    /**
     * Checks if the line contains an annotation.
     * The line number and filename are only passed in order
     * to give that information to the exception.
     * Returns an empty String if not.
     */
    static public String checkForAnnotation(String line, int lineNum, String filename)
            throws AnnotationException {
        String annotation = "";
        if (isAnnotation(line)) {
            try {
                annotation = line.substring(1, line.indexOf(' '));
            } catch (StringIndexOutOfBoundsException e) {
                throw new MissingAnnotationValueException(line, lineNum, filename);
            }
            if (!Annotation.isValid(annotation)) {
                throw new InvalidAnnotationException(annotation, lineNum, filename);
            }
            return annotation;
        }

        return "";
    }

    /**
     * Checks if the line contains an annotation definition for 'default'.
     */
    static public boolean containsDefault(String line) {
        int posA = line.indexOf("{@default");
        int posB = line.lastIndexOf("}");
        return posA > -1 && posB > posA;
    }

    /**
     * Returns true if the line is a comment.
     */
    static public boolean isComment(String line) {
        return line.startsWith("//");
    }

    /**
     * Returns false if the line contains no useful information.
     * ex : // ----------- or ////////////////////
     */
    static public boolean isUseful(String line) {
        char c;
        for (int i = 0, l = line.length(); i < l; i++) {
            c = line.charAt(i);
            if (Character.isLetter(c) || Character.isDigit(c)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if the line is an instance property.
     * Instance properties are a special case because
     * they require no annotation.
     */
    static public boolean isInstanceProperty(String line) {
        int commentPos = line.indexOf("//");
        int bracePosA = line.indexOf("{", commentPos);
        int bracePosB = line.indexOf("}", commentPos);
        int colonPos = line.indexOf(":", commentPos);

        return commentPos > -1 && bracePosA > 0 && bracePosB > 0 && colonPos > 0 &&
                commentPos < bracePosA && bracePosA < bracePosB && bracePosB < colonPos;
    }

    /**
     * There is probably a better way than this.
     * I added this method because when parsing the file, we strip the '//'
     * from the comment, then start doing the operations on it. But after we've
     * already stripped the '//', we then have a check if it's an instance
     * property, but that method expects there to be a '//' in the line.
     */
    static public boolean isStandaloneInstanceProperty(String line) {
        int bracePosA = line.indexOf("{");
        int bracePosB = line.indexOf("}");
        int colonPos = line.indexOf(":");

        return bracePosA > -1 && bracePosB > bracePosA && colonPos > bracePosB;
    }

    /**
     * Trims the // prefix and space from the line.
     */
    static public String trim(String line) {
        int commentPos = line.indexOf("//");
        return line.substring(commentPos+2, line.length()).trim();
    }

    /**
     * Returns true if the line contains an annotation declaration.
     */
    static boolean isAnnotation(String line) {
        return line.startsWith("@");
    }
}