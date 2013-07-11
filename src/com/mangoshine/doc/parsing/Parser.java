package com.mangoshine.doc.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

import com.mangoshine.doc.Config;
import com.mangoshine.doc.annotation.Annotation;
import com.mangoshine.doc.annotation.AnnotationException;
import com.mangoshine.doc.construct.Documentation;

/**
 *
 */
public class Parser {

    /**
     * Construct a parser.
     */
    public Parser() {

    }

    /**
     * Parse each input file specified in the config.
     */
    public Documentation parse()
            throws IOException,
                   AnnotationException {
        List<String> inputs = Config.INSTANCE.getInputs();
        for (String input : inputs) {
            parse(new File(input));
        }

        return Documentation.INSTANCE;
    }

    /**
     * Parse a single file.
     *
     * Delegates all work to the ParserHelper.
     */
    private void parse(File file)
            throws IOException,
                   AnnotationException {
        try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file)))) {
            String line;
            String filename = file.toString();
            int lineNum = 0;
            ParserHelper pHelper = ParserHelper.INSTANCE;

            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (Line.isComment(line) && Line.isUseful(line)) {
                    line = Line.trim(line);

                    // Check if there's a new annotation on this line
                    String annotation = Line.checkForAnnotation(line, lineNum, filename);
                    if (annotation != "") {
                        pHelper.handleAnnotation(annotation);
                    }
                    // if not, check if it's an instance property
                    else {
                        if (Line.isStandaloneInstanceProperty(line)) {
                            pHelper.handleContextInstanceProperty(line);
                        }
                    }

                    switch (Annotation.current) {
                        case CLASS:             pHelper.handleContextClass(line);
                                                break;
                        case PARAM:             pHelper.handleContextParam(line);
                                                break;
                        case STATICMETHOD:      pHelper.handleContextStaticMethod(line);
                                                break;
                        case INSTANCEMETHOD:    pHelper.handleContextInstanceMethod(line);
                                                break;
                        case STATICPROPERTY:    pHelper.handleContextStaticProperty(line);
                                                break;
                        case RETURN:            pHelper.handleContextReturn(line);
                                                break;
                        case CONSTRUCTOR:       pHelper.handleContextConstructor(line);
                                                break;
                        case EVENT:             pHelper.handleContextEvent(line);
                                                break;
                        case NAMESPACE:         pHelper.handleContextNamespace(line);
                                                break;
                        default:                break;
                    }
                }
                else {
                    // instance properties do not require an annotation
                    if (Line.isInstanceProperty(line)) {
                        line = Line.trim(line);
                        pHelper.handleContextInstanceProperty(line);
                    }
                    // if the line has code we reset the context
                    if (!Line.isComment(line)) {
                        pHelper.setToNoContext();
                    }
                }
            }
        }
    }

    /**
     * Ensure the file is valid and exists
     */
    private File getFileFromFilename(String filename) {
        File file =  new File(filename);
        if (!file.exists()) {

        }

        return file;
    }
}