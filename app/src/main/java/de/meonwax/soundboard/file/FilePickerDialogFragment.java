package de.meonwax.soundboard.file;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.meonwax.soundboard.MainActivity;
import de.meonwax.soundboard.R;

public class FilePickerDialogFragment extends DialogFragment {

    private DirectoryEntryAdapter directoryEntryAdapter;

    private Directory currentDirectory;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Determine root directory to start browsing
        Directory root;
        Set<File> directories = FileUtils.getExternalStorageDirectories();
        if (directories != null & directories.size() == 1) {
            root = new Directory(directories.iterator().next(), null);
        } else {
            root = new StorageSelector(getContext(), directories);
        }

        // Create the custom ArrayAdapter
        directoryEntryAdapter = new DirectoryEntryAdapter(getContext());

        // Build the dialog
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(getString(R.string.button_import_directory), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity) (getActivity())).onDirectoryAdded(currentDirectory);
                    }
                })
                .setTitle(root.getTitle())
                .setAdapter(directoryEntryAdapter, null)
                .create();

        // Add directory entries
        addEntries(root);

        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DirectoryEntry entry = directoryEntryAdapter.getItem(position);
                if (entry.isDirectory()) {
                    if (addEntries(entry.getDirectory())) {
                        dialog.setTitle(entry.getDirectory().getTitle());
                    }
                } else {
                    if (FileUtils.existsExternalFile(getContext(), entry.getName())) {
                        Toast.makeText(getContext(), getString(R.string.error_entry_exists), Toast.LENGTH_LONG).show();
                    } else {
                        ((MainActivity) (getActivity())).onFileAdded(new File(entry.getPath()));
                    }
                    dismiss();
                }
            }
        });

        return dialog;
    }

    private boolean addEntries(Directory directory) {
        currentDirectory = directory;
        List<DirectoryEntry> entries = readDirectory(directory);
        if (entries != null) {
            directoryEntryAdapter.clear();
            if (!directory.isRoot()) {
                directoryEntryAdapter.add(new DirectoryEntry(DirectoryEntry.PARENT_DIRECTORY_NAME, directory.getParent()));
            }
            for (DirectoryEntry e : entries) {
                directoryEntryAdapter.add(e);
            }
            directoryEntryAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    private List<DirectoryEntry> readDirectory(Directory directory) {
        if (directory.canRead()) {
            List<DirectoryEntry> entries = new ArrayList<>();
            for (File file : directory.getFiles()) {
                // Ignore hidden files and dirs
                if (file.isHidden()) {
                    continue;
                }
                if (file.isDirectory()) {
                    entries.add(new DirectoryEntry(file.getName(), new Directory(file, directory)));
                } else {
                    // Ignore non-whitelisted files
                    if (!FileUtils.isWhitelisted(file)) {
                        continue;
                    }
                    entries.add(new FileEntry(file.getName(), file.length(), file.getAbsolutePath()));
                }
            }
            Collections.sort(entries);
            return entries;
        }
        return null;
    }
}