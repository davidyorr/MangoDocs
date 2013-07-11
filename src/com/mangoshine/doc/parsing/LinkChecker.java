package com.mangoshine.doc.parsing;

import java.util.HashSet;
import java.util.Set;

import com.mangoshine.doc.dom.DOMNode;

public class LinkChecker {
    private static Set<String> reservedWords = initReservedWords();

    private static Set<String> initReservedWords() {
        Set<String> set = new HashSet<>();
        // paragraph
        set.add("p");
        set.add("/p");
        // code (monospaced)
        set.add("code");
        set.add("/code");

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
}