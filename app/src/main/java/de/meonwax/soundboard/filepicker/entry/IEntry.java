package de.meonwax.soundboard.filepicker.entry;

import de.meonwax.soundboard.filepicker.dir.Directory;

public interface IEntry extends Comparable<IEntry> {

    public String getName();

    public String getPath();

    public long getSize();

    public Directory getDirectory();

    public boolean isDirectory();
}
