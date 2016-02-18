package de.meonwax.soundboard.file;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.meonwax.soundboard.MainActivity;
import de.meonwax.soundboard.R;

public class FilePickerDialogFragment extends DialogFragment {

    public final static String[] EXTENSION_WHITELIST = new String[]{"wav", "mp3", "ogg"};

    private DirectoryEntryAdapter directoryEntryAdapter;
    
    private File rootDirectory;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Determine root directory to start browsing
        rootDirectory = Environment.getExternalStorageDirectory();

        // Create the custom ArrayAdapter
        directoryEntryAdapter = new DirectoryEntryAdapter(getContext());

        // Add directory entries
        addEntries(rootDirectory);

        // Build the dialog
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setNegativeButton(R.string.cancel, null)
                .setTitle(rootDirectory.getAbsolutePath())
                .setAdapter(directoryEntryAdapter, null)
                .create();

        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DirectoryEntry entry = directoryEntryAdapter.getItem(position);
                if (entry.isDirectory) {
                    if (addEntries(new File(entry.path))) {
                        dialog.setTitle(entry.path);
                    }
                } else {
                    ((MainActivity) (getActivity())).onFileAdded(new File(entry.path));
                    dismiss();
                }
            }
        });

        return dialog;
    }

    private boolean addEntries(File directory) {
        List<DirectoryEntry> entries = readDirectory(directory);
        if (entries != null) {
            directoryEntryAdapter.clear();
            boolean isRoot = directory.equals(rootDirectory);
            if (!isRoot) {
                directoryEntryAdapter.add(new DirectoryEntry(DirectoryEntry.PARENT_DIRECTORY_NAME, FileUtils.getParentDirectory(directory.getAbsolutePath()), 0, true));
            }
            for (DirectoryEntry e : entries) {
                FileUtils.getParentDirectory(e.path);
                directoryEntryAdapter.add(e);
            }
            directoryEntryAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    private List<DirectoryEntry> readDirectory(File directory) {
        if (directory.canRead()) {
            List<DirectoryEntry> entries = new ArrayList<>();
            for (File file : directory.listFiles()) {
                // Ignore hidden files and dirs
                if (file.isHidden()) {
                    continue;
                }
                if (!file.isDirectory() &&
                        EXTENSION_WHITELIST != null &&
                        !Arrays.asList(EXTENSION_WHITELIST).contains(FileUtils.getExtension(file).toLowerCase(Locale.US))) {
                    continue;
                }
                entries.add(new DirectoryEntry(file.getName(), file.getAbsolutePath(), file.length(), file.isDirectory()));
            }
            Collections.sort(entries);
            return entries;
        }
        return null;
    }
}