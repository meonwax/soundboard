package de.meonwax.soundboard.filepicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.meonwax.soundboard.R;
import de.meonwax.soundboard.filepicker.entry.IEntry;
import de.meonwax.soundboard.util.FileUtils;

class EntryAdapter extends ArrayAdapter<IEntry> {

    public EntryAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the entry for this position
        IEntry entry = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.filepicker_entry, parent, false);
        }

        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.directory_entry_name);
        TextView size = (TextView) convertView.findViewById(R.id.directory_entry_size);
        ImageView icon = (ImageView) convertView.findViewById(R.id.directory_entry_icon);

        // Populate the data into the template view using the DirectoryEntry object
        name.setText(entry.getName());
        if (entry.isDirectory()) {
            size.setText(getContext().getString(R.string.file_directory));
            icon.setImageResource(R.drawable.ic_folder_24dp);
        } else {
            size.setText(FileUtils.getSize(entry.getSize()));
            icon.setImageResource(R.drawable.ic_insert_drive_file_24dp);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
