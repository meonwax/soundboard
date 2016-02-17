package de.meonwax.soundboard;

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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.meonwax.soundboard.file.FilePickerDialogFragment;
import de.meonwax.soundboard.file.FileUtils;
import de.meonwax.soundboard.sound.Sound;
import de.meonwax.soundboard.sound.SoundAdapter;

public class MainActivity extends AppCompatActivity {

    private SoundPool soundPool;
    private List<Sound> sounds;
    private BaseAdapter soundAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Only initialize if we're not being restored from a previous state
        if (savedInstanceState == null) {
            initSoundSystem();

            // Init sound adapter
            sounds = new ArrayList<>();
            soundAdapter = new SoundAdapter(this, sounds);
            ListView soundList = (ListView) findViewById(R.id.sound_list);
            soundList.setAdapter(soundAdapter);

            // Populate sound files
            List<File> soundFiles = FileUtils.getExternalFiles(this);
            if (!soundFiles.isEmpty()) {
                for (File file : soundFiles) {
                    addSound(file);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                DialogFragment filePickerFragment = new FilePickerDialogFragment();
                filePickerFragment.show(getSupportFragmentManager(), "filePicker");
                break;
            case R.id.action_info:
                showAbout();
                break;
            case R.id.action_exit:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onFileAdded(File file) {
        String externalPath = FileUtils.getExternalPath(this, file);
        if (externalPath == null) {
            return;
        }
        if (new File(externalPath).exists()) {
            Toast.makeText(this, getString(R.string.entry_exists), Toast.LENGTH_LONG).show();
        } else {
            FileUtils.copyToExternal(this, file);
            addSound(file);
        }
    }

    @SuppressWarnings("deprecation")
    private void initSoundSystem() {
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
    }

    private void addSound(File soundFile) {

        // Load the sound file
        int soundId = soundPool.load(soundFile.getAbsolutePath(), 1);

        // Create a new sound object and ad it to the adapter
        sounds.add(new Sound(soundId, soundFile.getName()));
        soundAdapter.notifyDataSetChanged();
    }

    public void playSound(int soundId) {
        soundPool.play(soundId, 1f, 1f, 1, 0, 1);
    }

    public void removeSound(int soundId) {

        // Unload from sound pool
        soundPool.unload(soundId);

        // Remove from adapter
        Sound sound = sounds.remove(soundId - 1);
        soundAdapter.notifyDataSetChanged();

        // Delete from filesystem
        String externalPath = FileUtils.getExternalPath(this, new File(sound.getName()));
        if (externalPath == null || !new File(externalPath).delete()) {
            Toast.makeText(this, getString(R.string.error_remove), Toast.LENGTH_LONG).show();
        }
    }

    private void showAbout() {
        StringBuilder sb = new StringBuilder(getString(R.string.app_name));
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            sb.append(" v").append(info.versionName).append("\n\n");
        } catch (PackageManager.NameNotFoundException e) {
        }
        sb.append("Copyright © 2016 Sebastian Wolf").append("\n\n");
        sb.append("released under the GPLv3");
        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
    }
}
