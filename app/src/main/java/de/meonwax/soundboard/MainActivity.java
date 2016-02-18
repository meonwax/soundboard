package de.meonwax.soundboard;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private boolean hasRequestedPermissions = false;

    private SoundPool soundPool;
    private List<Sound> sounds;
    private BaseAdapter soundAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        init();
    }

    private void init() {
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

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!hasRequestedPermissions) {
                hasRequestedPermissions = true;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Workaround for bug
                    // https://code.google.com/p/android-developer-preview/issues/detail?id=2982
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage(Html.fromHtml(getString(R.string.error_permission_denied, getString(R.string.app_name))))
                            .setPositiveButton(R.string.ok, null)
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    finish();
                                }
                            })
                            .show();
                }
                return;
            }
        }
        hasRequestedPermissions = false;
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
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
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
            Toast.makeText(this, getString(R.string.error_entry_exists), Toast.LENGTH_LONG).show();
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
}
