package com.mangoshine.doc.construct;

import java.util.ArrayList;
import java.util.List;

import com.mangoshine.doc.annotation.Annotation;

/**
 * A class entry specified by @class.
 */
public class ClassEntry extends Entry {
    public ClassEntry(String name) {
        super(new Entry.Builder(Annotation.CLASS, name)
                       .includeMethods()
                       .includeProperties()
                       .includeConstructor()
                       .includeNestedNamespaces());
    }
}