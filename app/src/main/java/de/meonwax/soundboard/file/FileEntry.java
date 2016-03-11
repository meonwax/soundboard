package de.meonwax.soundboard.file;

public class FileEntry extends DirectoryEntry {

    public FileEntry(String name, long size, String filePath) {
        super(name, null);
        this.size = size;
        this.filePath = filePath;
    }
}
