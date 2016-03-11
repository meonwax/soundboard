package de.meonwax.soundboard.file;

import android.support.annotation.NonNull;

import java.util.Locale;

public class DirectoryEntry implements Comparable<DirectoryEntry> {

    public final static String PARENT_DIRECTORY_NAME = "..";

    protected String name;
    protected long size;
    protected String filePath;
    private Directory directory;

    public DirectoryEntry(String name, Directory directory) {
        this.name = name;
        this.size = 0l;
        this.directory = directory;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return directory != null ? directory.getPath() : filePath;
    }

    public long getSize() {
        return size;
    }

    public Directory getDirectory() {
        return directory;
    }

    public boolean isDirectory() {
        return directory != null;
    }

    @Override
    public int compareTo(@NonNull DirectoryEntry another) {
        // Parent directory will always be the first entry
        if (name.equals(PARENT_DIRECTORY_NAME)) {
            return -1;
        }
        if ((isDirectory() && another.isDirectory()) || (!isDirectory() && !another.isDirectory())) {
            return name.toLowerCase(Locale.US).compareTo(another.name.toLowerCase(Locale.US));
        }
        if (isDirectory()) {
            return -1;
        }
        return 1;
    }

    @Override
    public String toString() {
        return "DirectoryEntry{name='" + name + "', size=" + size + ", directory=" + directory + '}';
    }
}
