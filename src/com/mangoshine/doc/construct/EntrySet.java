package com.mangoshine.doc.construct;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.mangoshine.doc.annotation.Annotation;

public class EntrySet {
    /**
     * The way to uniquely identify entries is by
     * class.entry or if it is the class itself then just class.
     * This is because there may be multiple entries that share
     * the same entry.getName() (this would happen if two classes
     * both have a method or property with the same name)
     */
    private Set<Entry> entries = new TreeSet<>();

    private Map<String, Entry> entriesMap = new TreeMap<>();

    public EntrySet() {

    }

    /**
     * Add an entry to the set of entries.
     */
    public void add(Entry entry) {
        entries.add(entry);

        entriesMap.put(generateKey(entry), entry);
    }

    public void add(String parentName, Entry entry) {
        entriesMap.put(generateKey(parentName, entry), entry);
    }

    /**
     * Returns the entry thats name matches the name provided.
     */
    public Entry get(String entryName) {
        return entriesMap.get(entryName);
    }

    public Entry get(String parentName, String entryName) {
        return entriesMap.get(generateKey(parentName, entryName));
    }

    /**
     * Returns a Set of all entries.
     */
    public Set<Entry> getEntries() {
        Set<Entry> temp = new TreeSet<>();
        for (Map.Entry<String, Entry> e : entriesMap.entrySet()) {
            temp.add(e.getValue());
        }
        return temp;
    }

    /**
     * Returns a Set of Entries where each is one of the types provided.
     */
    public Set<Entry> getEntries(Annotation... typeFilters) {
        if (entries == null) {
            return new TreeSet<Entry>();
        }
        Set<Entry> set = new TreeSet<>(entries);
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

    static public String generateKey(String parentName, Entry entry) {
        return generateKey(parentName, entry.getName());
    }

    static public String generateKey(Entry entry) {
        return entry.getName();
    }

    static public String generateKey(String parentName, String entryName) {
        return parentName + "." + entryName;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Entry entry : entries) {
            sb.append(entry.toString());
        }
        return sb.toString();
    }
}