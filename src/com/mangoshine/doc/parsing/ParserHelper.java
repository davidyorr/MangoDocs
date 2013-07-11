package com.mangoshine.doc.parsing;

import com.mangoshine.doc.annotation.Annotation;
import com.mangoshine.doc.annotation.AnnotationException;
import com.mangoshine.doc.annotation.InvalidAnnotationException;
import com.mangoshine.doc.annotation.MissingAnnotationValueException;
import com.mangoshine.doc.construct.ClassEntry;
import com.mangoshine.doc.construct.ConstructorEntry;
import com.mangoshine.doc.construct.Documentation;
import com.mangoshine.doc.construct.Entry;
import com.mangoshine.doc.construct.MethodEntry;
import com.mangoshine.doc.construct.NamespaceEntry;
import com.mangoshine.doc.construct.Parameter;
import com.mangoshine.doc.construct.PropertyEntry;
import com.mangoshine.doc.construct.ReturnValue;
import com.mangoshine.doc.dom.DOMNode;
import com.mangoshine.doc.util.Pair;

/**
 * Helper functions for Parser.
 * Singleton.
 */
public enum ParserHelper {
    INSTANCE;

    // -------------------------------------------------------------------------
    // Handlers
    // -------------------------------------------------------------------------

    /**
     * Switches context based on the annotation.
     */
    public void handleAnnotation(String annotation) {
        Annotation.setCurrent(annotation);
    }

    /**
     * Switches context to NONE.
     */
    public void setToNoContext() {
        Annotation.setCurrent("none");
    }

    public String handleDefault(String line, Entry entry) {
        int start = line.indexOf("{@default ");
        int end = line.lastIndexOf("}");
        entry.setDefault(Line.handleLinks(line.substring(start+10, end)));
        return line.substring(0, start-1);
    }

    /**
     * Handles the line when in class mode.
     */
    public void handleContextClass(String line) {
        Documentation doc = Documentation.INSTANCE;
        Annotation.Pair ann = parseAnnotation(line);

        if (ann != null && ann.type.equals("class")) {
            doc.addEntry(new ClassEntry(ann.value));
            Annotation.CLASS.setCurrentName(ann.value);
            Annotation.setCurrentClassOrMethod(ann.type);
            Annotation.setCurrentClassOrNamespace(ann.type);
        } else {
            Entry entry = doc.getEntry(Annotation.CLASS.getCurrentName());
            entry.appendToDescription(line);
        }
    }

    public void handleContextNamespace(String line) {
        Documentation doc = Documentation.INSTANCE;
        Annotation.Pair ann = parseAnnotation(line);
        Entry parentEntry;

        if (ann != null && ann.type.equals("namespace")) {
            doc.addEntry(new NamespaceEntry(ann.value));
            Annotation.NAMESPACE.setCurrentName(ann.value);
            Annotation.setCurrentClassOrNamespace(ann.type);

            // check if it is a nested namespace
            int period = ann.value.lastIndexOf(".");
            try {
                parentEntry = doc.getEntry(ann.value.substring(0, period));
                parentEntry.addNestedNamespace(doc.getEntry(ann.value));
            } catch( Exception e) {

            }
        } else {
            Entry entry = doc.getEntry(Annotation.NAMESPACE.getCurrentName());
            entry.appendToDescription(line);
        }
    }

    public void handleContextConstructor(String line) {
        Documentation doc = Documentation.INSTANCE;
        Entry parentEntry = doc.getEntry(Annotation.CLASS.getCurrentName());
        Annotation.Pair ann = parseAnnotation(line);
        Entry entry = parentEntry.getConstructor();

        if (ann != null && ann.type.equals("constructor") ) {
            entry.appendToDescription(ann.value);
            Annotation.CONSTRUCTOR.setCurrentName(parentEntry.getName());
        } else {
            entry.appendToDescription(line);
        }
    }

    /**
     * Handles the line when in param mode.
     */
    public void handleContextParam(String line) {
        Documentation doc = Documentation.INSTANCE;
        Annotation currentClassOrMethod = Annotation.currentClassOrMethod;
        Entry entry = null;
        if (currentClassOrMethod == Annotation.CLASS) {
            entry = doc.getEntry(currentClassOrMethod.getCurrentName());
        } else {
            entry = doc.getEntry(Annotation.CLASS.getCurrentName(), currentClassOrMethod.getCurrentName());
        }
        Annotation.Pair ann = parseAnnotation(line);
        Parameter param = parseParam(line);

        if (currentClassOrMethod == Annotation.CLASS) {
            entry.getConstructor().addParameter(param);
        } else {
            entry.addParameter(param);
        }
    }

    private void handleContextMethod(String line, Annotation annotationType) {
        Documentation doc = Documentation.INSTANCE;
        MethodEntry method;
        Annotation.Pair ann = parseAnnotation(line);
        Entry parentEntry = doc.getEntry(Annotation.CLASS.getCurrentName());
        String methodName;

        if (ann != null && (ann.type.equals("staticmethod")
                         || ann.type.equals("instancemethod")
                         || ann.type.equals("event"))) {
            if (line.contains("#")) {
                int pound = ann.value.indexOf("#");
                parentEntry = doc.getEntry(ann.value.substring(0, pound));
                methodName = ann.value.substring(pound+1, ann.value.length());
                method = new MethodEntry(annotationType, methodName);
                // method.setReturnValue("undefined", "");
                parentEntry.addMethod(method);
                // annotationType.setCurrentName(parentEntry.getName(), methodName);
                doc.addEntry(parentEntry.getName(), method);
                annotationType.setCurrentName(methodName);
                Annotation.setCurrentClassOrMethod(ann.type);
            }
        } else {
            Entry entry  = doc.getEntry(parentEntry.getName(), annotationType.getCurrentName());

            try {
                entry.appendToDescription(line);
                // System.out.println("appended [" + line + "] to function " +  entry.getName());
            } catch (Exception e) {
                System.err.println("error on line : " + line);
                e.printStackTrace();
            }
        }
    }

    public void handleContextProperty(String line, Annotation propertyType) {
        Documentation doc = Documentation.INSTANCE;
        // Entry parentEntry = doc.getEntry(Annotation.CLASS.getCurrentName());
        Entry parentEntry = doc.getEntry(Annotation.currentClassOrNamespace.getCurrentName());
        Annotation.Pair ann = parseAnnotation(line);
        Entry property;
        String propertyName;

        // if it's the static property annotation declaration line
        if (ann != null && ann.type.equals("staticproperty") ) {
            int period = ann.value.indexOf(".");
            try {
                parentEntry = doc.getEntry(ann.value.substring(0, period));
            } catch( Exception e) {
                System.err.println(ann + " could not be substringed");
                System.exit(-1);
            }
            propertyName = ann.value.substring(period+1, ann.value.length());
            property = new PropertyEntry(propertyType, propertyName);

            parentEntry.addProperty(property);
            doc.addEntry(parentEntry.getName(), property);
            Annotation.STATICPROPERTY.setCurrentName(propertyName);
            Annotation.setCurrentClassOrMethod(ann.type);
        } else {
            if (propertyType == Annotation.INSTANCEPROPERTY) {
                Parameter parameter = parseParam(line);
                property = new PropertyEntry(propertyType, parameter.getName());
                property.setType(parameter.getArgType());
                String description = parameter.getPlainDescription();
                if (Line.containsDefault(description)) {
                    description = handleDefault(description, property);
                }
                property.appendToDescription(description);
                parentEntry.addProperty(property);
            } else {
                Pair<String, String> typeDescription = parseStaticProperty(line);
                property = doc.getEntry(parentEntry.getName(),
                        Annotation.currentClassOrMethod.getCurrentName());
                if (property == null) {
                    String s = Annotation.currentClassOrMethod +"\n"+
                        Annotation.currentClassOrMethod.getCurrentName() +"\n"+
                        line +"\n";
                    System.err.println(s);
                    System.exit(-200);
                }
                property.setType(typeDescription.first);
                property.appendToDescription(typeDescription.second);
            }
        }
    }

    public void handleContextInstanceMethod(String line) {
        handleContextMethod(line, Annotation.INSTANCEMETHOD);
    }

    public void handleContextStaticMethod(String line) {
        handleContextMethod(line, Annotation.STATICMETHOD);
    }

    public void handleContextEvent(String line) {
        handleContextMethod(line, Annotation.EVENT);
    }

    public void handleContextInstanceProperty(String line) {
        handleContextProperty(line, Annotation.INSTANCEPROPERTY);
    }

    public void handleContextStaticProperty(String line) {
        handleContextProperty(line, Annotation.STATICPROPERTY);
    }

    public void handleContextReturn(String line) {
        Documentation doc = Documentation.INSTANCE;
        Annotation.Pair ann = parseAnnotation(line);

        // if it's the return declaration
        if (ann != null && ann.type.equals("return")) {
            ReturnValue returnValue = parseReturn(line);
            Entry entry = doc.getEntry(Annotation.CLASS.getCurrentName(),
                    Annotation.currentClassOrMethod.getCurrentName());
            entry.setReturnValue(returnValue);
        }
    }

    // -------------------------------------------------------------------------
    // specific parsers
    // -------------------------------------------------------------------------

    /**
     * Retrieves a pair of annotation type => annotation value
     */
    private Annotation.Pair parseAnnotation(String line) {
        if (!Line.isAnnotation(line)) return null;
        int space = line.indexOf(' ');
        if (space < 0) {
            return new Annotation.Pair("", "");
        }
        return new Annotation.Pair(line.substring(1, space),
                line.substring(space+1, line.length()));
    }

    /**
     * Parses a param line.
     */
    private Parameter parseParam(String line) {
        String name, argType, description;
        int i, j;
        i = line.indexOf('}');
        argType = line.substring(line.indexOf('{')+1, i);
        j = line.indexOf(':');
        name = line.substring(i+1, j).trim();
        description = line.substring(j+1, line.length()).trim();

        return new Parameter(name, argType, description);
    }

    /**
     * Parses a static property.
     * @return a pair with type, description
     */
    private Pair<String, String> parseStaticProperty(String line) {
        String type, description;
        int i, j;
        i = line.indexOf('}');
        type = line.substring(line.indexOf('{')+1, i);
        description = line.substring(i+1, line.length()).trim();

        return new Pair<String, String>(type, description);
    }

    /**
     * Parses a parameter line.
     */
    private ReturnValue parseReturn(String line) {
        String type, description;
        type = line.substring(line.indexOf('{')+1, line.indexOf('}'));
        description = line.substring(line.indexOf(':')+1, line.length());
        return new ReturnValue(type, description);
    }
}