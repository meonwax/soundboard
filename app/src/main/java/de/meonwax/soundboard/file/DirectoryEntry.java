package de.meonwax.soundboard.file;

import android.support.annotation.NonNull;

class DirectoryEntry implements Comparable<DirectoryEntry> {

    public final static String PARENT_DIRECTORY_NAME = "..";

    public final String name;
    public final String path;
    public final long size;
    public final boolean isDirectory;

    public DirectoryEntry(String name, String path, long size, boolean isDirectory) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.isDirectory = isDirectory;
    }

    @Override
    public int compareTo(@NonNull DirectoryEntry another) {
        // Parent directory will always be the first entry
        if (name.equals(PARENT_DIRECTORY_NAME)) {
            return -1;
        }
        if ((isDirectory && another.isDirectory) || (!isDirectory && !another.isDirectory)) {
            return name.toLowerCase().compareTo(another.name.toLowerCase());
        }
        if (isDirectory) {
            return -1;
        }
        return 1;
    }

    public String toString() {
        return "DirectoryEntry{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", size=" + size +
                ", isDirectory=" + isDirectory +
                '}';
    }
}
