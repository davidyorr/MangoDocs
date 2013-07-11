package com.mangoshine.doc.construct;

import com.mangoshine.doc.annotation.Annotation;

/**
 * A method entry specified by either @staticmethod or @instancemthod.
 */
public class MethodEntry extends Entry {
    public MethodEntry(Annotation annotationType, String name) {
        super(new Entry.Builder(annotationType, name)
                       .includeParameters()
                       .includeReturnValue());
    }
}