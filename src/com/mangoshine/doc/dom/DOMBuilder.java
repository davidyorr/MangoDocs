package com.mangoshine.doc.dom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.text.SimpleDateFormat;

import com.mangoshine.doc.annotation.Annotation;
import com.mangoshine.doc.construct.Documentation;
import com.mangoshine.doc.construct.Entry;
import com.mangoshine.doc.construct.MethodEntry;
import com.mangoshine.doc.construct.Parameter;
import com.mangoshine.doc.util.Pair;
import com.mangoshine.doc.writing.WriterHelper;

/**
 * Responsible for generating parts of layout files.
 * Never reads from files directly, that is the sole
 * resonsibility of the WriterHelper.
 * Handles filling in the expressions ( {{expression}} ).
 *
 * Methods that end in 'HTML' and return a String generate content that
 * goes inside an element, essentially the same as jQuery's html() function.
 * Methods that return a DOMNode object return the DOM element itself.
 */
public class DOMBuilder {
    /* Because the header and footer should be the same, keep
       a reference so they are not generated multiple times. */
    private static String footerHTML = null;
    private static String sidebarHTML = null;

    private static DOMPartialBuilder partialBuilder = DOMPartialBuilder.INSTANCE;

    /**
     * Generates the HTML that goes inside the div element with
     * the id 'content', which is the {{content}} block of the
     * layout files.
     * @return a page's HTML content as a string
     */
    static public String buildContentHTML() {
        Entry currentEntry = WriterHelper.INSTANCE.currentEntry;

        if (currentEntry == null) {
            return buildIndexContentHTML();
        }

        // header
        DOMNode contentHeader = partialBuilder.buildContentHeader(currentEntry);
        StringBuilder sb = new StringBuilder(contentHeader.toString());

        // body
        sb.append(buildOverviewSection(currentEntry).toString());
        sb.append(DOMNode.toString(buildSubSection("Summary", currentEntry, Annotation.NONE)));
        sb.append(DOMNode.toString(buildSubSection("Constructor", currentEntry, Annotation.CONSTRUCTOR)));
        sb.append(DOMNode.toString(buildSubSection("Instance Properties", currentEntry, Annotation.INSTANCEPROPERTY)));
        sb.append(DOMNode.toString(buildSubSection("Static Properties", currentEntry, Annotation.STATICPROPERTY)));
        sb.append(DOMNode.toString(buildSubSection("Instance Methods", currentEntry, Annotation.INSTANCEMETHOD)));
        sb.append(DOMNode.toString(buildSubSection("Static Methods", currentEntry, Annotation.STATICMETHOD)));
        sb.append(DOMNode.toString(buildSubSection("Events", currentEntry, Annotation.EVENT)));

        return sb.toString();
    }

    /**
     * Generates a DOMNode for a sub section on a class page.
     * @param title - the title of the section
     * @param entry - the Entry to build the subsection for
     * @param body  - an array of DOMNodes to append as the body of the subsection
     */
    static private DOMNode buildSubSection(String title, Entry entry, DOMNode[] body) {
        return new DOMNode("div")
                        .addAttribute("class", "subSection")
                        .appendNode(partialBuilder.buildSectionTitle(title))
                        .appendNodes(body);
    }

    /**
     * Generates a DOMNode for a sub section on a class page.
     */
    static private DOMNode buildSubSection(String title, Entry entry, DOMNode body) {
        return buildSubSection(title, entry, new DOMNode[] { body });
    }

    /**
     * Generates a DOMNode for a sub section on a class page.
     */
    static private DOMNode buildSubSection(String title, Entry entry, Annotation sectionType) {
        if (sectionType == Annotation.NONE) {
            return buildSubSection(title, entry, partialBuilder.buildSummaryTables(entry));
        }

        // getMembers() may return null if the entry type is NAMESPACE
        try {
            if (entry.getMembers(sectionType).size() == 0) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        if (sectionType == Annotation.INSTANCEPROPERTY || sectionType == Annotation.STATICPROPERTY)
            return buildSubSection(title, entry, partialBuilder.buildPropertiesSectionBody(
                entry, sectionType));
        if (sectionType == Annotation.INSTANCEMETHOD || sectionType == Annotation.STATICMETHOD
                || sectionType == Annotation.CONSTRUCTOR || sectionType == Annotation.EVENT)
            return buildSubSection(title, entry, partialBuilder.buildMethodsSectionBody(
                entry, sectionType));
        return null;
    }

    /**
     * Generates the DOMNode for the Overview section on a class page.
     */
    static private DOMNode buildOverviewSection(Entry entry) {
        return buildSubSection("Overview", entry, partialBuilder.buildDescription(entry.getDescription()));
    }

    /**
     * Generates the DOMNode for the Methods sections on a class page.
     */
    static private DOMNode buildMethodsSection(Set<Entry> methods) {
        if (methods.size() == 0) {
            return null;
        }

        DOMNode container
            = new DOMNode("div")
                    .addAttribute("class", "subSection")
                    .addAttribute("id", "functions-section")
                    .appendNode(partialBuilder.buildSectionTitle("Methods"));

        for (Entry method : methods) {
            container.appendNode(
                new DOMNode("div")
                        .addAttribute("class", "functionContainer")
                        .appendNode(partialBuilder.buildMethodSignature(method))
                        .appendNode(
                            new DOMNode("div")
                                    .addAttribute("class", "details")
                                    .appendNode(partialBuilder.buildDescription(method.getDescription()))
                                    .appendNode(buildMethodParamsTable(method))));
        }

        return container;
    }

    /**
     * Generates the DOMNode for the properties table.
     */
    static private DOMNode buildPropertiesTable(String title, Set<Entry> properties) {
        if (properties.size() == 0) {
            return null;
        }
        DOMNode table
            = new DOMNode("table")
                    .addAttribute("class", "propertiesTable");
        DOMNode tbody
            = new DOMNode("tbody").appendNode(
                new DOMNode("tr")
                    .appendNode(
                        new DOMNode("th", title).addAttribute("colspan", "12")
                    )
            );

        for (Entry property : properties) {
            tbody.appendNode(
                new DOMNode("tr")
                    .appendNode(
                        new DOMNode("td", property.getType())
                            .addAttribute("class", "propTypeCol")
                            .appendNode(
                                new DOMNode("a", property.getName())
                                    .addAttribute("href", "#"+property.getName())
                            )
                    )
                    .appendNode(
                        new DOMNode("td", property.getDescription())
                            .addAttribute("width", "100%")
                    )
            );
        }
        table.appendNode(tbody);

        return table;
    }

    /**
     * Generates the DOMNode for the parameters table in a function section.
     */
    static private DOMNode buildMethodParamsTable(Entry method) {
        if (method.getParameters().size() < 0) {
            return null;
        }

        DOMNode paramsContainer
            = new DOMNode("div")
                    .addAttribute("class", "parameters")
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

    /**
     * Generates a DOMNode table.
     *
     * @param classAttr the css class to apply to the element
     * @param tableList a List of the entries in the table
     */
    static private DOMNode buildParamTable(String classAttr, List<Pair<String, String>> tableList) {
        DOMNode table = new DOMNode("table")
                            .addAttribute("class", classAttr);
        DOMNode tbody = new DOMNode("tbody");

        Pair<String, String> item;
        for (int i = 0; i < tableList.size(); i++) {
            item = tableList.get(i);
            tbody.appendNode(
                new DOMNode("tr")
                    .appendNode(
                        new DOMNode("th", item.first)
                    )
                    .appendNode(
                        new DOMNode("td", item.second)
                    )
            );
        }
        table.appendNode(tbody);

        return table;
    }

    /**
     * Generates the HTML for the footer.
     */
    static public String buildFooterHTML() {
        if (footerHTML == null) {
            Date now = Calendar.getInstance().getTime();

            DOMNode a = new DOMNode("a", "MangoDocs").addAttribute("href", "#");
            DOMNode span = new DOMNode("span", "Generated by "+a.toString()+" on " +
                    new SimpleDateFormat("yyyy MMM dd HH:mm z").format(now));

            footerHTML = span.toString();
        }

        return footerHTML;
    }

    /**
     * Generates the HTML for the sidebar.
     */
    static public String buildSidebarHTML() {
        if (sidebarHTML == null) {
            StringBuilder sb = new StringBuilder();
            Documentation jsdoc = Documentation.INSTANCE;

            Set<Entry> entries = jsdoc.getEntries(Annotation.CLASS);
            Set<Entry> nestedNamespaces;
            String entryName;
            for (Entry entry : entries) {
                DOMNode div = partialBuilder.buildSidebarEntry(entry);
                sb.append(div.toString());
            }

            sidebarHTML = sb.toString();
        }

        return sidebarHTML;
    }

    // -------------------------------------------------------------------------

    /**
     * Generates content for the index file.
     */
    static private String buildIndexContentHTML() {
        // header
        DOMNode contentHeader = partialBuilder.buildContentHeader("Class Index");
        StringBuilder sb = new StringBuilder(contentHeader.toString());

        // entries
        String entryName;
        Documentation jsdoc = Documentation.INSTANCE;
        Set<Entry> entries = jsdoc.getEntries(Annotation.CLASS);

        for (Entry entry : entries) {
            entryName = entry.getName();
            sb.append((
                new DOMNode("div")
                    .addAttribute("class", "itemContainer")
                    .appendNode(
                        new DOMNode("span", (new DOMNode("a", entryName)
                                                    .addAttribute("href", entryName+".html")).toString())
                            .addAttribute("class", "entryName")
                    )
                    .appendNode(
                        new DOMNode("span", entry.getDescription())
                            .addAttribute("class", "entryDescription")
                    )
            ).toString());
        }
        // extra container for border
        sb.append(
            (new DOMNode("div")
                        .addAttribute("class", "itemContainer")).toString()
        );

        return sb.toString();
    }
}