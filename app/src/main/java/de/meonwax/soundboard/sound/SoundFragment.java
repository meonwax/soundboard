package de.meonwax.soundboard.sound;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import de.meonwax.soundboard.MainActivity;
import de.meonwax.soundboard.R;

public class SoundFragment extends Fragment implements OnClickListener {

    public final static String ARGUMENT_NAME = "name";
    public final static String ARGUMENT_SOUND_ID = "soundId";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.sound_fragment, container, false);

        // Configure the buttons
        Button playButton = (Button) view.findViewById(R.id.sound_play);
        playButton.setText(getArguments().getString(ARGUMENT_NAME));
        playButton.setOnClickListener(this);

        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.sound_delete);
        deleteButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sound_play:
                ((MainActivity) getActivity()).playSound(getArguments().getInt(ARGUMENT_SOUND_ID));
                break;
            case R.id.sound_delete:
                new AlertDialog.Builder(getContext())
                        .setMessage(Html.fromHtml(getString(R.string.confirm_remove, getArguments().getString(ARGUMENT_NAME))))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((MainActivity) getActivity()).removeSound(getArguments().getInt(ARGUMENT_SOUND_ID), SoundFragment.this);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                break;
        }
    }
}
