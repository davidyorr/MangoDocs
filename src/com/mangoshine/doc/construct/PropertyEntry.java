package com.mangoshine.doc.construct;

import com.mangoshine.doc.annotation.Annotation;

/**
 * A method entry specified by either @staticproperty or @instanceproperty.
 */
public class PropertyEntry extends Entry {
    public PropertyEntry(Annotation annotationType, String name) {
        super(new Entry.Builder(annotationType, name)
                       .includeDefault()
                       .includeType()
                       .includeParameters()
                       .includeReturnValue());
    }
}