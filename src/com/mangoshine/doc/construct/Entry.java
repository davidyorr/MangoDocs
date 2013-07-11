package com.mangoshine.doc.construct;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.mangoshine.doc.annotation.Annotation;
import com.mangoshine.doc.parsing.Line;

/**
 * Base class for all entries. Represents an entry in the documentation.
 * Optional fields are set from the Builder.
 *
 * Optional fields are defaulted to null, and the corresponding methods
 * that use those fields do nothing if the value is set to null. That
 * forces subclasses that don't include those options to do nothing if
 * one of those methods is called. For example, calling addProperty()
 * on a MethodEntry will do nothing.
 */
public class Entry implements Comparable<Entry> {
    private Annotation annotation;
    private String name;
    private String description;
    private String _default;
    private String type;
    private Entry constructor;
    private List<Parameter> parameters;
    private ReturnValue returnValue;
    private Set<Entry> methods;
    private Set<Entry> nestedNamespaces;
    private Set<Entry> properties;

    protected Entry(Builder builder) {
        annotation = builder.annotation;
        name = builder.name;
        type = builder.type;
        constructor = builder.constructor;
        description = builder.description;
        this._default = builder._default;
        methods = builder.methods;
        properties = builder.properties;
        nestedNamespaces = builder.nestedNamespaces;
        parameters = builder.parameters;
        returnValue = builder.returnValue;
    }

    public void appendToDescription(String text) {
        if (this.description.equals("")) {
            this.description = "<p>" + text;
        } else {
            this.description += " " + text;
        }
    }

    public void setDefault(String str) {
        if (_default != null) {
            _default = str;
        }
    }

    public void setType(String str) {
        if (type != null) {
            type = str;
        }
    }

    public void setReturnValue(ReturnValue returnValue) {
        if (this.returnValue != null) {
            this.returnValue = returnValue;
        }
    }

    public void setReturnValue(String type, String description) {
        if (this.returnValue != null) {
            this.returnValue.setType(type);
            this.returnValue.appendToDescription(description);
        }
    }

    public void addParameter(Parameter parameter) {
        if (parameters != null) {
            parameters.add(parameter);
        }
    }

    public void addProperty(Entry property) {
        if (properties != null) {
            properties.add(property);
        }
    }

    public void addNestedNamespace(Entry entry) {
        if (nestedNamespaces != null) {
            nestedNamespaces.add(entry);
        }
    }

    public void addMethod(MethodEntry method) {
        if (methods != null) {
            methods.add(method);
        }
    }

    public Annotation getAnnotationType() {
        return annotation;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return Line.handleLinks(description);
    }

    public String getDefault() {
        return _default;
    }

    public String getType() {
        return type;
    }

    public Entry getConstructor() {
        return constructor;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public ReturnValue getReturnValue() {
        return returnValue;
    }

    public Set<Entry> getProperties() {
        return properties;
    }

    public Set<Entry> getNestedNamespaces() {
        return nestedNamespaces;
    }

    public Set<Entry> getMethods() {
        return methods;
    }

    public Entry getMethod(String name) {
        if (methods != null) {
            for (Entry method : methods) {
                if (method.equals(name)) {
                    return method;
                }
            }
        }
        return null;
    }

    public Set<Entry> getMethods(Annotation... typeFilters) {
        return getFilteredSet(methods, typeFilters);
    }

    public Set<Entry> getProperties(Annotation... typeFilters) {
        return getFilteredSet(properties, typeFilters);
    }

    /**
     * Gets either the methods or properties or constructor.
     */
    public Set<Entry> getMembers(Annotation... typeFilters) {
        Set<Entry> set = getMethods(typeFilters);
        set.addAll(getProperties(typeFilters));
        if (Arrays.asList(typeFilters).contains(Annotation.CONSTRUCTOR)) {
            set.add(constructor);
        }
        return set;
    }

    private Set<Entry> getFilteredSet(Set<Entry> entrySet, Annotation... typeFilters) {
        if (entrySet == null) {
            return new TreeSet<Entry>();
        }
        Set<Entry> set = new TreeSet<>(entrySet);
        boolean keep;
        Entry entry;
        Iterator<Entry> it = set.iterator();
        while (it.hasNext()) {
            entry = it.next();
            keep = false;
            for (Annotation type : typeFilters) {
                if (entry.isAnnotationType(type)) {
                    keep = true;
                }
            }
            if (!keep) {
                it.remove();
            }
        }

        return set;
    }

    public boolean isAnnotationType(Annotation type) {
        return annotation == type;
    }

    public String toString() {
        String s =
             "-------------------------"
          +"\nEntry"
          +"\n  annotation   : [" + annotation + "]"
          +"\n  name         : [" + name + "]"
          +"\n  description  : [" + description + "]";
          if (constructor != null) {
            s += "\n  Constructor:\n" + constructor.toString();
          }
          if (parameters != null) {
            s += "\nParameters :";
            for (Parameter param : parameters) {
                s += "\n" + param.toString();
            }
          }
          if (returnValue != null) {
            s += "\nReturn value:";
            s += "\n" + returnValue.toString();
          }
          // +"\n  parameters?  : [" + (parameters == null ? "false" : "true") + "]"
          // +"\n  returnValue? : [" + (returnValue == null ? "false" : "true") + "]"
         s+="\n-------------------------";

        return s;
    }

    @Override
    public int compareTo(Entry o) {
        return name.compareTo(o.getName());
    }

    public boolean equals(String o) {
        return o.equals(name);
    }

    /**
     * Entry builder
     */
    public static class Builder {
        // required
        private final Annotation annotation;
        private final String name;

        // optional
        private String description = "";
        private String _default = null;
        private String type = null;
        private Entry constructor = null;
        private List<Parameter> parameters = null;
        private ReturnValue returnValue = null;
        private Set<Entry> methods = null;
        private Set<Entry> nestedNamespaces = null;
        private Set<Entry> properties = null;

        public Builder(Annotation annotation, String name) {
            this.annotation = annotation;
            this.name = name;
        }

        public Builder description(String value) {
            description = value;
            return this;
        }

        public Builder _default(String value) {
            _default = value;
            return this;
        }

        public Builder includeDefault() {
            _default = "";
            return  this;
        }

        public Builder includeType() {
            type = "";
            return this;
        }

        public Builder includeConstructor() {
            constructor = new ConstructorEntry(this.name);
            return this;
        }

        public Builder includeParameters() {
            parameters = new ArrayList<>();
            return this;
        }

        public Builder includeReturnValue() {
            returnValue = new ReturnValue();
            return this;
        }

        public Builder includeMethods() {
            methods = new TreeSet<>();
            return this;
        }

        public Builder includeNestedNamespaces() {
            nestedNamespaces = new TreeSet<>();
            return this;
        }

        public Builder includeProperties() {
            properties = new TreeSet<>();
            return this;
        }
    }
}