package com.mangoshine.doc.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an HTML DOM element.
 * DOMNodes can be created and altered in one statement,
 * i.e., addAttribute and appendNode chained after the constructor.
 * This is useful for creating DOMNodes that have attributes and
 * children without having to create a reference.
 *
 * Example : new DOMNode("div")
 *                  .addAttribute("id", "myDiv")
 *                  .appendNode(otherNode);
 */
public class DOMNode {
    protected Map<String, String> attributes;
    protected String tagName;
    protected String text;
    protected String node;
    protected List<DOMNode> children;

    public DOMNode(String tagName) {
        this(tagName, "");
    }

    public DOMNode(String tagName, String text) {
        this.tagName = tagName;
        this.text = text;
        this.attributes = new HashMap<>();
        this.children = new ArrayList<>();
        this.node = createNode(tagName);
    }

    public DOMNode addAttribute(String name, String value) {
        if (name.length() > 0 && value.length() > 0) {
            attributes.put(name, value);
        }
        return this;
    }

    public DOMNode appendNode(DOMNode node) {
        if (node != null) {
            this.children.add(node);
        }
        return this;
    }

    public DOMNode appendNodes(DOMNode... nodes) {
        if (nodes != null) {
            for (DOMNode node : nodes) {
                appendNode(node);
            }
        }
        return this;
    }

    public DOMNode appendText(String text) {
        this.text = this.text + " " + text;
        return this;
    }

    /**
     * Returns the tag name of the node.
     * i.e. the element type
     */
    public String getTagName() {
        return this.tagName;
    }

    public String getText() {
        return this.text;
    }

    private String createNodeString(DOMNode node) {
        StringBuilder sb = new StringBuilder();
        String tagName = node.getTagName();
        sb.append(node.openingTag(tagName)).append(node.getText());

        List<DOMNode> children = node.children;
        for (DOMNode child : children) {
            sb.append(createNodeString(child));
        }

        sb.append(node.closingTag(tagName));

        return sb.toString();
    }

    private String createNode(String name) {
        return openingTag(name)+closingTag(name);
    }

    public String openingTag(String name) {
        StringBuilder sb = new StringBuilder("<").append(name);

        for (Map.Entry<String, String> attr : attributes.entrySet()) {
            sb.append(" ").append(attr.getKey()).append(
                    "=\"").append(attr.getValue()).append("\"");
        }
        sb.append(">");

        return sb.toString();
    }

    public String closingTag(String name) {
        return "</"+name+">";
    }

    /**
     * Returns the DOM element and all of
     * its children as a string.
     */
    public String toString() {
        return createNodeString(this);
    }

    /**
     * Returns the given DOMNode as a String. This is useful because
     * null.toString() throws an exception, but with this method it
     * returns an empty String.
     */
    public static String toString(DOMNode node) {
        if (node == null) {
            return "";
        }
        return node.toString();
    }
}