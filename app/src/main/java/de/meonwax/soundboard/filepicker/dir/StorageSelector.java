package de.meonwax.soundboard.filepicker.dir;

import android.content.Context;

import java.io.File;
import java.util.Set;

import de.meonwax.soundboard.R;

public class StorageSelector extends Directory {

    private final Set<File> storageDirectories;
    private final Context context;

    public StorageSelector(Context context, Set<File> storageDirectories) {
        super(null, null);
        this.context = context;
        this.storageDirectories = storageDirectories;
    }

    @Override
    public String getPath() {
        return "STORAGE_SELECTOR";
    }

    @Override
    public String getTitle() {
        return context.getString(R.string.file_storage_selection);
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public Set<File> getFiles() {
        return storageDirectories;
    }

    @Override
    public String toString() {
        return "StorageSelector{storageDirectories=" + storageDirectories + '}';
    }
}
