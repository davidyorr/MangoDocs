package com.mangoshine.doc.construct;

import com.mangoshine.doc.annotation.Annotation;

/**
 * A constructor entry.
 */
public class ConstructorEntry extends Entry {
    public ConstructorEntry(String name) {
        super(new Entry.Builder(Annotation.CONSTRUCTOR, name)
                       .includeParameters());
    }
}