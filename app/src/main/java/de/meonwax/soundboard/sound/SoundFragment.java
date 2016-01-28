package de.meonwax.soundboard.sound;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import de.meonwax.soundboard.R;

public class SoundFragment extends Fragment implements OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.sound_fragment, container, false);

        // Configure the buttons
        Button playButton = (Button) view.findViewById(R.id.sound_play);
        playButton.setText(getArguments().getString("name"));
        playButton.setOnClickListener(this);

        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.sound_delete);
        deleteButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sound_play:
                Toast.makeText(getContext(), "PLAY!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sound_delete:
                Toast.makeText(getContext(), "DELETE!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
