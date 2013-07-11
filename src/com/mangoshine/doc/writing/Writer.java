package com.mangoshine.doc.writing;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.mangoshine.doc.Config;
import com.mangoshine.doc.asset.AssetLibrary;
import com.mangoshine.doc.asset.InvalidAssetException;
import com.mangoshine.doc.annotation.Annotation;
import com.mangoshine.doc.construct.ClassEntry;
import com.mangoshine.doc.construct.Documentation;
import com.mangoshine.doc.construct.Entry;

public class Writer {
    private WriterHelper wHelper = WriterHelper.INSTANCE;

    public Writer() {
    }

    public void writeToFile(Documentation jsdoc) throws IOException,
                                                        InvalidAssetException {
        StringBuilder sb = new StringBuilder();
        FileWriter writer = null;
        InputStream src;

        // create dirs
        // String baseDirString = System.getProperty("user.dir");
        // File baseDir = new File(baseDirString+"/"+config.getOutputDir());
        File outputDir = Config.INSTANCE.getOutputDir();
        outputDir.mkdirs();

        // index
        sb.setLength(0);
        sb.append(wHelper.buildIndexHTML());
        writeSingleFile(writer, outputDir, "index.html", sb.toString());

        // each class entry
        Set<Entry> entries = jsdoc.getEntries(Annotation.CLASS);
        for (Entry entry : entries) {
            sb.setLength(0);
            sb.append(wHelper.buildClassHTML(entry));
            writeSingleFile(writer, outputDir, entry.getName()+".html", sb.toString());
        }

        // each namespace entry
        entries = jsdoc.getEntries(Annotation.NAMESPACE);
        for (Entry entry : entries) {
            sb.setLength(0);
            sb.append(wHelper.buildClassHTML(entry));
            writeSingleFile(writer, outputDir, entry.getName()+".html", sb.toString());
        }

        // style
        AssetLibrary.copyAsset("style.css", outputDir);

        // javascript
        AssetLibrary.copyAsset("app.js", outputDir);

        // font
        AssetLibrary.copyAsset("font.css", outputDir);
        AssetLibrary.copyAsset("fonts/", outputDir);
    }

    /**
     * Writes a single file.
     */
    private void writeSingleFile(FileWriter writer, File dir,
            String fileName, String content) throws IOException {
        File file = new File(dir, fileName);
        writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }
}