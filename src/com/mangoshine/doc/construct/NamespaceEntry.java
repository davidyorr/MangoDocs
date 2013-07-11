package com.mangoshine.doc.construct;

import com.mangoshine.doc.annotation.Annotation;

/**
 * A class entry specified by @class.
 */
public class NamespaceEntry extends Entry {
    public NamespaceEntry(String name) {
        super(new Entry.Builder(Annotation.NAMESPACE, name)
                       .includeMethods()
                       .includeProperties()
                       .includeNestedNamespaces());
    }

    @Override
    public String getType() {
        return "";
    }
}