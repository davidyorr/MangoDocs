package com.mangoshine.doc.construct;

import java.util.Set;

import com.mangoshine.doc.annotation.Annotation;

/**
 * Singleton that represents the entire documentation.
 * The parser adds Entries to this class, and the writer
 * uses this class when generating the layout files.
 */
public enum Documentation {
    INSTANCE;

    // Set of all Entries
    private EntrySet filterSet = new EntrySet();

    /**
     * Adds the given Entry into the documentation.
     */
    public void addEntry(Entry entry) {
        filterSet.add(entry);
    }

    /**
     * Adds the given Entry into the documentation as
     * a child of the Entry thats name is parentName.
     */
    public void addEntry(String parentName, Entry entry) {
        filterSet.add(parentName, entry);
    }

    /**
     * Returns the Entry with the given name.
     */
    public Entry getEntry(String name) {
        return filterSet.get(name);
    }

    /**
     * Returns the Entry with the given name and parent.
     */
    public Entry getEntry(String parentName, String name) {
        return filterSet.get(parentName, name);
    }

    /**
     * Returns all Entries.
     */
    public Set<Entry> getEntries() {
        return filterSet.getEntries();
    }

    /**
     * Returns all Entries with the given Annotation types.
     */
    public Set<Entry> getEntries(Annotation... typeFilters) {
        return filterSet.getEntries(typeFilters);
    }

    /* To String */
    public String toString() {
        return filterSet.toString();
    }
}