package de.meonwax.soundboard.file;

public class DirectoryEntry implements Comparable<DirectoryEntry> {

    public final static String PARENT_DIRECTORY_NAME = "..";

    public String name;
    public String path;
    public long size;
    public boolean isDirectory;

    public DirectoryEntry(String name, String path, long size, boolean isDirectory) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.isDirectory = isDirectory;
    }

    @Override
    public int compareTo(DirectoryEntry another) {
        // Parent directory will always be the first entry
        if (name.equals(PARENT_DIRECTORY_NAME)) {
            return -1;
        }
        if ((isDirectory && another.isDirectory) || (!isDirectory && !another.isDirectory)) {
            return name.toLowerCase().compareTo(another.name.toLowerCase());
        }
        if (isDirectory && !another.isDirectory) {
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
