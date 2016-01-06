package de.meonwax.soundboard;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private SoundPool soundPool;
    private List<Integer> soundIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSounds();
    }

    private void initSounds() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(2)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 1);
        }
        soundIds.add(soundPool.load(this, R.raw.bell, 1));
        soundIds.add(soundPool.load(this, R.raw.can_open, 1));
        soundIds.add(soundPool.load(this, R.raw.coin, 1));
    }

    public void playSound(View v) {
        int sound = soundIds.get(Integer.valueOf((String) v.getTag()));
        soundPool.play(sound, 0.9f, 0.9f, 1, 0, 1);
    }
}
