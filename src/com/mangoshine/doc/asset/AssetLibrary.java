package com.mangoshine.doc.asset;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.mangoshine.doc.writing.WriterHelper;

/**
 * For retrieving asset files.
 * And constants for creating assets.
 */
public class AssetLibrary {
    // valid assets
    private static Set<String> validAssets = initValidAssets();;

    private static Set<String> initValidAssets() {
        Set<String> assets = new HashSet<>();
        assets.add("base.html");
        assets.add("font.css");
        assets.add("fonts/");
        assets.add("style.css");
        assets.add("templating.js");
        assets.add("app.js");
        return assets;
    }

    static public InputStream getAsset(String name) throws InvalidAssetException {
        checkAsset(name);
        return AssetLibrary.class.getResourceAsStream("/assets/"+name);
    }

    static public void copyAsset(String name, File outputDir) throws InvalidAssetException, IOException {
        checkAsset(name);
        copyAsset(name, outputDir, null);
    }

    /**
     * Copies an asset file from the jar to the output folder specified in the config.
     * @param onlyThisDir - will only copy the file if the outputDir matches this value
     */
    static public void copyAsset(String name, File outputDir, String onlyThisDir) throws IOException {
        if (onlyThisDir != null) {
            // ensure it is the right dir
            if (!name.startsWith(onlyThisDir)) {
                return;
            }
        }
        if (outputDir == null) {
            return;
        }
        CodeSource src = AssetLibrary.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            try {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                ZipEntry zEntry;
                while ( (zEntry = zip.getNextEntry()) != null ) {
                    if (zEntry.getName().equals("assets/"+ name)) {
                        if (zEntry.isDirectory()) {
                            WriterHelper.INSTANCE.copyInputStream(null, name, outputDir);
                            String folder = zEntry.getName().replaceFirst("assets/", "");
                            while ( (zEntry = zip.getNextEntry()) != null ) {
                                copyAsset(zEntry.getName().replaceFirst("assets/", ""),
                                        outputDir, folder);
                            }
                            return;
                        }
                        InputStream is = AssetLibrary.class.getResourceAsStream("/"+zEntry.getName());
                        WriterHelper.INSTANCE.copyInputStream(is, name, outputDir);
                        return;
                    }
                }
            } catch (Exception e) {

            }
        }
        else {

        }
    }

    static private void checkAsset(String name) throws InvalidAssetException {
        if (!validAssets.contains(name)) {
            throw new InvalidAssetException(name);
        }
    }
}