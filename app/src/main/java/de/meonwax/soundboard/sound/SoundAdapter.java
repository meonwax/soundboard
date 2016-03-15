package de.meonwax.soundboard.sound;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.List;

import de.meonwax.soundboard.R;
import de.meonwax.soundboard.activity.MainActivity;

public class SoundAdapter extends BaseAdapter {

    private final Context context;
    private final List<Sound> sounds;

    public SoundAdapter(Context context, List<Sound> sounds) {
        this.context = context;
        this.sounds = sounds;
    }

    @Override
    public int getCount() {
        return sounds.size();
    }

    @Override
    public Sound getItem(int position) {
        return sounds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.sound_row, parent, false);
        }

        final Sound sound = getItem(position);

        Button playButton = (Button) convertView.findViewById(R.id.sound_play);
        playButton.setText(sound.getName());
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).playSound(sound.getId());
            }
        });

        ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.sound_delete);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage(Html.fromHtml(context.getString(R.string.confirm_remove, sound.getName())))
                        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((MainActivity) context).removeSound(position);
                            }
                        })
                        .setNegativeButton(R.string.button_cancel, null)
                        .show();
            }
        });

        return convertView;
    }
}
