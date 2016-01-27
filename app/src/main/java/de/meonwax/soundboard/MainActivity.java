package de.meonwax.soundboard;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.meonwax.soundboard.file.FilePickerDialogFragment;
import de.meonwax.soundboard.file.FileUtils;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private SoundPool soundPool;
    private List<Integer> soundIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initSounds();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, getString(R.string.action_settings), Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_new:
                DialogFragment filePickerFragment = new FilePickerDialogFragment();
                filePickerFragment.show(getSupportFragmentManager(), "filePicker");
                break;
            case R.id.action_about:
                showAbout();
                break;
            case R.id.action_exit:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onFileAdded(File file) {
        if (new File(FileUtils.getInternalPath(this, file)).exists()) {
            Toast.makeText(this, getString(R.string.entry_exists), Toast.LENGTH_LONG).show();
        } else {
            FileUtils.copyToInternal(this, file);
        }
    }

    @SuppressWarnings("deprecation")
    private void initSounds() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
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

    private int getRecommendedSampleRate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            String sampleRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            if (sampleRate != null) {
                return Integer.parseInt(sampleRate);
            }
        }
        return 44100;
    }

    public void playSound(View v) {
        int sound = soundIds.get(Integer.valueOf((String) v.getTag()));
        soundPool.play(sound, 0.9f, 0.9f, 1, 0, 1);
    }

    private void showAbout() {
        StringBuilder sb = new StringBuilder(getString(R.string.app_name));
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            sb.append(" v");
            sb.append(info.versionName);
            sb.append("\n");
            sb.append("\n");
        } catch (PackageManager.NameNotFoundException e) {
        }
        sb.append("Copyright © 2016 Sebastian Wolf");
        sb.append("\n");
        sb.append("\n");
        sb.append("released under the GPLv3");
        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
    }
}
