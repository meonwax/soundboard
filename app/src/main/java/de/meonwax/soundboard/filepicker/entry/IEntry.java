package de.meonwax.soundboard.filepicker.entry;

import de.meonwax.soundboard.filepicker.dir.Directory;

public interface IEntry extends Comparable<IEntry> {

    String getName();

    String getPath();

    long getSize();

    Directory getDirectory();

    boolean isDirectory();
}
