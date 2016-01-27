package de.meonwax.soundboard.file;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.meonwax.soundboard.R;

public class DirectoryEntryAdapter extends ArrayAdapter<DirectoryEntry> {

    public DirectoryEntryAdapter(Context context, List<DirectoryEntry> entries) {
        super(context, 0, entries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the entry for this position
        DirectoryEntry entry = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.directory_entry, parent, false);
        }

        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.directory_entry_name);
        TextView size = (TextView) convertView.findViewById(R.id.directory_entry_size);
        ImageView icon = (ImageView) convertView.findViewById(R.id.directory_entry_icon);

        // Populate the data into the template view using the data object
        name.setText(entry.name);
        if (entry.isDirectory) {
            size.setText(getContext().getString(R.string.file_directory));
            icon.setImageResource(R.drawable.ic_folder_black_24dp);
        } else {
            size.setText(getSize(entry.size));
            icon.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
        }

        // Return the completed view to render on screen
        return convertView;
    }

    private String getSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return Math.round(bytes / 1024f) + " KB";
        }
        return Math.round(bytes / 1024f / 1024f) + " MB";
    }
}
