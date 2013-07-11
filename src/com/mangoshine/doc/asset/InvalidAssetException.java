package com.mangoshine.doc.asset;

/**
 * Exception for invalid assets.
 */
public class InvalidAssetException extends Exception {
    public InvalidAssetException(String name) {
        super("Invalid asset : " + name);
    }
}