package de.meonwax.soundboard.file;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.meonwax.soundboard.R;

public class FilePickerDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        File dir = Environment.getExternalStorageDirectory();

        List<DirectoryEntry> entries = new ArrayList<>();
        entries.add(new DirectoryEntry("..", "..", 0, true));
        if (dir.canRead()) {
            for (File file : dir.listFiles()) {

                // Ignore hidden files and dirs
                if (file.getName().startsWith(".")) {
                    continue;
                }

                long size = Integer.parseInt(String.valueOf(file.length()));
                entries.add(new DirectoryEntry(file.getName(), file.getAbsolutePath(), size, file.isDirectory()));
            }
        }

        DirectoryEntryAdapter directoryEntryAdapter = new DirectoryEntryAdapter(getContext(), entries);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setTitle(dir.getAbsolutePath())
                .setAdapter(directoryEntryAdapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Clicked: " + which, Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
    }
}