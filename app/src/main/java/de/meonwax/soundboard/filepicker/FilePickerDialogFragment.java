package de.meonwax.soundboard.filepicker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.meonwax.soundboard.R;
import de.meonwax.soundboard.activity.MainActivity;
import de.meonwax.soundboard.filepicker.dir.Directory;
import de.meonwax.soundboard.filepicker.dir.StorageSelector;
import de.meonwax.soundboard.filepicker.entry.DirectoryEntry;
import de.meonwax.soundboard.filepicker.entry.FileEntry;
import de.meonwax.soundboard.filepicker.entry.IEntry;
import de.meonwax.soundboard.util.FileUtils;

public class FilePickerDialogFragment extends DialogFragment {

    private EntryAdapter entryAdapter;

    private Directory currentDirectory;

    private Button importDirectoryButton;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Determine root directory to start browsing
        final Directory root;
        Set<File> directories = FileUtils.getStorageDirectories(getContext());
        if (directories != null && directories.size() == 1) {
            root = new Directory(directories.iterator().next(), null);
        } else {
            root = new StorageSelector(getContext(), directories);
        }

        // Create the custom ArrayAdapter
        entryAdapter = new EntryAdapter(getContext());

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
                .setAdapter(entryAdapter, null)
                .create();

        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IEntry entry = entryAdapter.getItem(position);
                if (entry.isDirectory()) {
                    if (addEntries(entry.getDirectory(), dialog)) {
                        dialog.setTitle(entry.getDirectory().getTitle());
                    }
                } else {
                    if (FileUtils.existsInternalFile(getContext(), entry.getName())) {
                        Toast.makeText(getContext(), getString(R.string.error_entry_exists), Toast.LENGTH_LONG).show();
                    } else {
                        ((MainActivity) (getActivity())).onFileAdded(new File(entry.getPath()));
                    }
                    dismiss();
                }
            }
        });

        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                importDirectoryButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                // Add directory entries
                addEntries(root, ((AlertDialog) dialog));
            }
        });

        return dialog;
    }

    private boolean addEntries(Directory directory, final AlertDialog dialog) {
        // Hide the button on storage selection
        importDirectoryButton.setVisibility(directory instanceof StorageSelector ? View.INVISIBLE : View.VISIBLE);
        currentDirectory = directory;
        List<DirectoryEntry> entries = readDirectory(directory);
        if (entries != null) {
            entryAdapter.clear();
            if (!directory.isRoot()) {
                entryAdapter.add(new DirectoryEntry(DirectoryEntry.PARENT_DIRECTORY_NAME, directory.getParent()));
            }
            for (DirectoryEntry e : entries) {
                entryAdapter.add(e);
            }
            entryAdapter.notifyDataSetChanged();
            // Be sure to scroll to top when the list contents change
            dialog.getListView().post(new Runnable() {
                @Override
                public void run() {
                    dialog.getListView().setSelectionAfterHeaderView();
                }
            });
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
                    String dirName = directory instanceof StorageSelector ? file.getAbsolutePath() : file.getName();
                    entries.add(new DirectoryEntry(dirName, new Directory(file, directory)));
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