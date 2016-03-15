package de.meonwax.soundboard.filepicker.entry;

import android.support.annotation.NonNull;

import java.util.Locale;

import de.meonwax.soundboard.filepicker.dir.Directory;

public class DirectoryEntry implements IEntry {

    public final static String PARENT_DIRECTORY_NAME = "..";

    private final String name;
    long size;
    String filePath;
    private final Directory directory;

    public DirectoryEntry(String name, Directory directory) {
        this.name = name;
        this.size = 0l;
        this.directory = directory;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPath() {
        return directory != null ? directory.getPath() : filePath;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public Directory getDirectory() {
        return directory;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public int compareTo(@NonNull IEntry anotherEntry) {
        // Parent directory will always be the first entry
        if (getName().equals(PARENT_DIRECTORY_NAME)) {
            return -1;
        }
        if ((isDirectory() && anotherEntry.isDirectory()) || (!isDirectory() && !anotherEntry.isDirectory())) {
            return getName().toLowerCase(Locale.US).compareTo(anotherEntry.getName().toLowerCase(Locale.US));
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
