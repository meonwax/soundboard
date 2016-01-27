package de.meonwax.soundboard.file;

public class DirectoryEntry {

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
}
