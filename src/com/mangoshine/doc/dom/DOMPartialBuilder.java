package com.mangoshine.doc.dom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mangoshine.doc.annotation.Annotation;
import com.mangoshine.doc.construct.Entry;
import com.mangoshine.doc.construct.Parameter;
import com.mangoshine.doc.construct.ReturnValue;
import com.mangoshine.doc.util.Pair;

/**
 * Contains methods that build DOM elements and are
 * expected to be called multiple times, or methods
 * that don't create a main section.
 * Builds smaller DOM elements and assists the DOMBuilder.
 * This class is to better organize DOMBuilder by
 * removing the methods that are called multiple times.
 */
public enum DOMPartialBuilder {
    INSTANCE;

    /**
     * Generates a header for a page.
     * @param entry - the Entry to build a header for
     */
    static DOMNode buildContentHeader(Entry entry) {
        return buildContentHeader(entry.getName());
    }

    /**
     * Generates a header for a page.
     * @param title - the title for the header
     */
    static DOMNode buildContentHeader(String title) {
        return new DOMNode("div").addAttribute("id", "content-header")
                                 .appendNode(new DOMNode("h1", title));
    }

    /**
     * Generates a DOMNode for a member's description.
     * @param text - the text content for the description
     */
    static DOMNode buildDescription(String text) {
        if (text.length() == 0) {
            return null;
        }

        return new DOMNode("div", text).addAttribute("class", "description");
    }

    /**
     * Generates a DOMNode link in a span.
     * @param text - the text of the link
     * @param href - the url of the link
     * @param postText - any text to go after the link part
     */
    static DOMNode buildLink(String text, String href, String postText) {
        return new DOMNode("span").appendText(
                                        (new DOMNode("a", text)
                                            .addAttribute("href", href)).toString())
                                  .appendText(postText);
    }

    static DOMNode buildSidebarEntry(Entry entry) {
        return buildSidebarEntry(entry, 1);
    }

    /**
     * Generates a DOMNode for an entry in the sidebar.
     * @param entry = the Entry
     */
    static DOMNode buildSidebarEntry(Entry entry, int nestedLevel) {
        String entryName = entry.getName();
        DOMNode div
            = new DOMNode("div")
                    .appendNode(
                        new DOMNode("a", entryName)
                            .addAttribute("href", entryName+".html")
                            .addAttribute("style", "margin-left: "+nestedLevel+"em;"));

        Set<Entry> nestedNamespaces;
        nestedNamespaces = entry.getNestedNamespaces();
        if (nestedNamespaces.size() > 0) {
            div.appendNode(
                new DOMNode("div")
                    .addAttribute("class", "namespaceFold entypo-right-open"));
            div.addAttribute("class", "hasNamespaces");
            DOMNode nested
                = new DOMNode("div")
                        .addAttribute("class", "nestedNamespaces");
            for (Entry namespace : nestedNamespaces) {
                entryName = namespace.getName();
                nested.appendNode(buildSidebarEntry(namespace, nestedLevel+1));
            }
            div.appendNode(nested);
        }

        return div;
    }

    /**
     * Generates a DOMNode for the top row in a summary table.
     */
    private static DOMNode buildSummaryTableTitle(String text) {
        return new DOMNode("tr")
                        .addAttribute("class", "summaryTableHeader")
                        .appendNode(new DOMNode("th")
                                .addAttribute("colspan", "12")
                                .appendNode(new DOMNode("a", text)
                                        .addAttribute("href", "#section-"+text)));
    }

    /**
     * Generates a DOMNode table for the return value of a method.
     * @param method - the Entry to generate the returns table for
     */
    static DOMNode buildReturnsTable(Entry method) {
        ReturnValue returnValue = method.getReturnValue();
        if (returnValue == null || returnValue.isEmpty()) {
            return null;
        }

        DOMNode table = new DOMNode("table").addAttribute("class", "returnsTable");
        DOMNode tbody
            = new DOMNode("tbody")
                    // returns [left side]
                    .appendNode(
                        new DOMNode("th", "<strong>returns</strong>"))
                    // type and description [right side]
                    .appendNode(
                        new DOMNode("tr")
                                .appendNode(
                                    new DOMNode("td", method.getType()))
                               .appendNode(
                                    new DOMNode("td", method.getDescription())));

        table.appendNode(tbody);
        return table;
    }

    /**
     * Generates the DOMNode for the signature of a function in a function section.
     * @param method - the Entry to build a signature for
     */
    static DOMNode buildMethodSignature(Entry method) {
        StringBuilder sb = new StringBuilder(
                "<strong>").append(
                method.getName()).append(
                "</strong> ( ");

        List<Parameter> params = method.getParameters();
        Parameter param;
        int size = params.size();
        for (int i = 0; i < size; i++) {
            param = params.get(i);
            sb.append("<em>").append(param.getArgType()).append("</em> ").append(param.getName());
            if (i != size-1) {
                sb.append(", ");
            }
        }

        sb.append(size == 0 ? ")" : " )");

        return new DOMNode("span", sb.toString())
                        .addAttribute("class", "signature");
    }

    /**
     * Generates a DOMNode for a section's title.
     * (Overview, Contrustor, Methods, etc.)
     * @param title - the title for the section
     */
    static DOMNode buildSectionTitle(String title) {
        return new DOMNode("h2", title)
                        .addAttribute("id", "section-"+title);
    }

    /**
     * Generates a DOMNode for a sub section's title.
     * (Static Methods, Instance Methods, Static properties, etc.)
     * @param title - the title for the sub section
     */
    static DOMNode buildSubSectionTitle(String title) {
        return new DOMNode("h3", title);
    }

    static DOMNode buildSummaryTableTwoCol(String title, Entry entry) {
        if (entry == null) {
            return null;
        }
        Set<Entry> set = new HashSet<>();
        set.add(entry);
        return buildSummaryTableTwoCol(title, set);
    }

    /**
     * Generates the DOMNode for a two column table in the summary section.
     */
    static DOMNode buildSummaryTableTwoCol(String title, Set<Entry> entries) {
        if (entries.size() == 0) {
            return null;
        }

        DOMNode table = new DOMNode ("table");
        table.addAttribute("class", "summaryTable");
        DOMNode tbody = new DOMNode("tbody");
        String type;
        ReturnValue retVal;
        tbody.appendNode(buildSummaryTableTitle(title));
        DOMNode div = new DOMNode("div").addAttribute("class", "summaryTableBody");

        for (Entry entry : entries) {
            retVal = entry.getReturnValue();
            retVal = entry.getReturnValue();
            type = retVal != null ? retVal.getType() : "";
            tbody.appendNode(
                // row
                new DOMNode("tr")
                        .addAttribute("class", "summaryTableBody")
                        // type [left side]
                        .appendNode(
                            new DOMNode("td", type)
                                    .addAttribute("class", "propTypeCol")
                                    .addAttribute("style",
                                        type.equals("undefined") ? "font-style: italic;" : "")
                        )
                        // name and description [right side]
                        .appendNode(
                            new DOMNode("td")
                                    .addAttribute("width", "100%")
                                    // name
                                    .appendNode(buildLink(entry.getName(),
                                                    "#"+entry.getName(),
                                                    buildSignatureParamsString(entry))
                                    )
                                    // description
                                        .appendNode(
                                            new DOMNode("div", entry.getDescription())
                                                            .addAttribute("class", "descrDiv")
                                        )
                        )
            );
        }
        table.appendNode(tbody);

        return table;
    }

    /**
     * Generates the DOMNode for a three column table in the summary section.
     */
    static DOMNode buildSummaryTableThreeCol(String title, Set<Entry> entries, boolean topLevelLinks) {
        if (entries.size() == 0) {
            return null;
        }
        DOMNode table = new DOMNode("table")
                            .addAttribute("class", "summaryTable");
        DOMNode tbody = new DOMNode("tbody")
                            .appendNode(buildSummaryTableTitle(title));

        for (Entry entry : entries) {
            tbody.appendNode(
                //row
                new DOMNode("tr")
                        .addAttribute("class", "summaryTableBody")
                        // type [col A]
                        .appendNode(
                            new DOMNode("td", entry.getType())
                                .addAttribute("class", "propTypeCol")
                        )
                        // name [col B]
                        .appendNode(
                            new DOMNode("td")
                                .appendNode(
                                    new DOMNode("a", entry.getName())
                                        .addAttribute("href",
                                                topLevelLinks
                                                ? entry.getName()+".html"
                                                : "#"+entry.getName())
                                )
                        )
                        // description [col C]
                        .appendNode(
                            new DOMNode("td", entry.getDescription())
                                    .addAttribute("width", "100%")
                        )
            );
        }
        table.appendNode(tbody);

        return table;
    }

    /**
     * Generates an array of DOMNode tables that go in the summary section.
     */
    static DOMNode[] buildSummaryTables(Entry entry) {
        List<DOMNode> tables = new ArrayList<>();
        tables.add(buildSummaryTableTwoCol("Constructor", entry.getConstructor()));
        tables.add(buildSummaryTableThreeCol("Nested Namespaces", entry.getNestedNamespaces(), true));
        tables.add(buildSummaryTableThreeCol("Instance Properties", entry.getProperties(Annotation.INSTANCEPROPERTY), false));
        tables.add(buildSummaryTableThreeCol("Static Properties", entry.getProperties(Annotation.STATICPROPERTY), false));
        tables.add(buildSummaryTableTwoCol("Instance Methods", entry.getMethods(Annotation.INSTANCEMETHOD)));
        tables.add(buildSummaryTableTwoCol("Static Methods", entry.getMethods(Annotation.STATICMETHOD)));
        tables.add(buildSummaryTableTwoCol("Events", entry.getMethods(Annotation.EVENT)));

        return tables.toArray(new DOMNode[tables.size()]);
    }

    /**
     * Generates the DOMNode for the parameters table in a function section.
     */
    static private DOMNode buildMethodParamsTable(Entry method) {
        if (method.getParameters().size() == 0) {
            return null;
        }
        // Params
        DOMNode paramsContainer
            = new DOMNode("div")
                    .addAttribute("class", "parametersContainer")
                    .appendNode(
                        new DOMNode("span", "Parameters")
                            .addAttribute("class", "parametersTitle")
                    );

        List<Pair<String, String>> tableList = new ArrayList<>();
        List<Parameter> params = method.getParameters();
        Pair<String, String> pair;
        Parameter param;
        for (int i = 0; i < params.size(); i++) {
            param = params.get(i);
            tableList.add(new Pair(param.getName(), param.getDescription()));
        }

        paramsContainer.appendNode(buildParamTable("parametersTable", tableList));

        return paramsContainer;
    }

    static private DOMNode buildDefaultDiv(Entry property) {
        DOMNode container
            = new DOMNode("div")
                    .addAttribute("class", "defaultContainer")
                    .appendNode(
                        new DOMNode("span", "Default")
                            .addAttribute("class", "defaultTitle"))
                    .appendNode(
                        new DOMNode("div", property.getDefault())
                            .addAttribute("class", "defaultDescr"));

        return container;
    }

    /**
     * Generates a DOMNode table.
     *
     * @param classAttr the css class to apply to the element
     * @param tableList a List of the entries in the table
     */
    static private DOMNode buildParamTable(String classAttr, List<Pair<String, String>> tableList) {
        DOMNode table = new DOMNode("table").addAttribute("class", classAttr);
        DOMNode tbody = new DOMNode("tbody");

        Pair<String, String> item;
        for (int i = 0; i < tableList.size(); i++) {
            item = tableList.get(i);
            tbody.appendNode(new DOMNode("tr")
                                    .appendNode(new DOMNode("th", item.first))
                                    .appendNode(new DOMNode("td", item.second)));
        }
        table.appendNode(tbody);

        return table;
    }

    /**
     * Returns a String of the signature of an Entry.
     */
    static private String buildSignatureParamsString(Entry entry) {
        StringBuilder sb = new StringBuilder(" ( ");

        List<Parameter> params = entry.getParameters();
        Parameter param;
        int size = params.size();
        for (int i = 0; i < size; i++) {
            param = params.get(i);
            sb.append(param.getName());
            if (i != size-1) {
                sb.append(", ");
            }
        }
        sb.append(" )");
        return sb.toString();
    }

    /**
     * Generates a String for the Entry's signature.
     * ex for property: static int maxSize
     * ex for method  : boolean isReady()
     */
    static private DOMNode buildEntrySignature(Entry entry) {
        String name = entry.getName();
        Annotation annotationType = entry.getAnnotationType();
        boolean isStatic
            = annotationType == Annotation.STATICMETHOD ||
              annotationType == Annotation.STATICPROPERTY
            ? true
            : false;

        if (annotationType == Annotation.STATICPROPERTY ||
                annotationType == Annotation.INSTANCEPROPERTY) {
            return new DOMNode("h4", (isStatic ? "static " : "") +
                                            entry.getType() + " " +
                                            "<strong>"+entry.getName()+"</strong>")
                            .addAttribute("id", entry.getName());
        }

        StringBuilder sb = new StringBuilder(isStatic ? "static " : "");

        // entry will have a null return value if it is the constructor
        String returnValueType;
        ReturnValue rv = entry.getReturnValue();
        if (rv == null) {
            returnValueType = "";
        } else {
            returnValueType = rv.getType();
        }

        sb.append(returnValueType)
          .append(" <strong>")
          .append(entry.getName())
          .append("</strong> ( ");
        List<Parameter> parameters = entry.getParameters();
        int size = parameters.size();
        Parameter parameter;
        for (int i = 0; i < size; i++) {
            parameter = parameters.get(i);
            sb.append(parameter.getArgType())
              .append(" ")
              .append(parameter.getName());
            if (i != size-1) {
                sb.append(", ");
            } else {
                sb.append(" ");
            }
        }
        sb.append(")");

        return new DOMNode("h4", sb.toString());
    }

    /**
     * Generates the DOMNode for the body of a property section.
     */
    static DOMNode[] buildPropertiesSectionBody(Entry entry, Annotation propertyType) {
        Set<Entry> properties = entry.getProperties(propertyType);
        if (properties.size() == 0) {
            return null;
        }

        List<DOMNode> entries = new ArrayList<>();

        for (Entry property : properties) {
            entries.add(new DOMNode("div")
                                .addAttribute("class", "memberDiv")
                                .appendNode(buildEntrySignature(property))
                                .appendNode(buildPropertyEntryBody(property)));
        }

        return entries.toArray(new DOMNode[entries.size()]);

    }

    static DOMNode buildPropertyEntryBody(Entry entry) {
        DOMNode div
            = new DOMNode("div")
                    .addAttribute("class", "memberDivBody")
                    .appendNode(
                        new DOMNode("div", entry.getDescription())
                            .addAttribute("class", "memberDescr")
                    );
        String _default = entry.getDefault();
        if (_default.length() > 0) {
            div.appendNode(buildDefaultDiv(entry));
        }

        return div;
    }

    /**
     * Generates the DOMNode for the body of a method section.
     */
    static DOMNode[] buildMethodsSectionBody(Entry entry, Annotation methodType) {
        Set<Entry> methods;

        if (methodType == Annotation.CONSTRUCTOR) {
            methods = new HashSet<>();
            methods.add(entry.getConstructor());
        } else {
            methods = entry.getMethods(methodType);
        }

        if (methods.size() == 0) {
            return null;
        }

        List<DOMNode> entries = new ArrayList<>();

        for (Entry method : methods) {
            entries.add(new DOMNode("div")
                                .addAttribute("class", "memberDiv")
                                .addAttribute("id", method.getName())
                                .appendNode(buildEntrySignature(method))
                                .appendNode(
                                    new DOMNode("div")
                                        .addAttribute("class", "memberDivBody")
                                        .appendNode(
                                            method.getDescription().length() > 0
                                            ? new DOMNode("span", method.getDescription())
                                            : null
                                        )
                                        .appendNode(buildMethodParamsTable(method))
                                )
                        );
        }

        return entries.toArray(new DOMNode[entries.size()]);
    }
}