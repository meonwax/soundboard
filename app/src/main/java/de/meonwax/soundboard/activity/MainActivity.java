package de.meonwax.soundboard.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.meonwax.soundboard.R;
import de.meonwax.soundboard.filepicker.FilePickerDialogFragment;
import de.meonwax.soundboard.filepicker.dir.Directory;
import de.meonwax.soundboard.sound.Sound;
import de.meonwax.soundboard.sound.SoundAdapter;
import de.meonwax.soundboard.sound.SoundPoolBuilder;
import de.meonwax.soundboard.util.FileUtils;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private boolean hasRequestedPermissions = false;

    private SoundPool soundPool;
    private List<Sound> sounds;
    private SoundAdapter soundAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        init();

        // We were called by a view intent from another app
        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            onSendFile(getIntent());
        }
    }

    private void init() {
        // Create sound pool
        soundPool = SoundPoolBuilder.build();

        // Init sound adapter
        sounds = new ArrayList<>();
        soundAdapter = new SoundAdapter(this, sounds);
        ListView soundList = (ListView) findViewById(R.id.sound_list);
        soundList.setAdapter(soundAdapter);

        // Populate sound files
        List<File> soundFiles = FileUtils.getInternalFiles(this);
        if (!soundFiles.isEmpty()) {
            for (File file : soundFiles) {
                addSound(file);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check for runtime permissions on supported devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkForPermission();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!hasRequestedPermissions) {
                hasRequestedPermissions = true;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Workaround for bug
                    // https://code.google.com/p/android-developer-preview/issues/detail?id=2982
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage(Html.fromHtml(getString(R.string.error_permission_denied, getString(R.string.app_name))))
                            .setPositiveButton(R.string.button_ok, null)
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
            case R.id.action_remove_all:
                removeAll();
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

    public void onDirectoryAdded(Directory directory) {
        int fileCount = 0;
        if (directory != null) {
            for (File file : directory.getFiles()) {
                if (onFileAdded(file)) {
                    fileCount++;
                }
            }
        }
        Toast.makeText(this, getString(R.string.import_directory_info, fileCount), Toast.LENGTH_LONG).show();
    }

    public boolean onFileAdded(File file) {
        String internalPath = FileUtils.getInternalPath(this, file);
        if (internalPath != null &&
                !FileUtils.existsInternalFile(this, file.getName()) &&
                FileUtils.isWhitelisted(file)) {
            FileUtils.copyToInternal(this, file);
            addSound(file);
            return true;
        }
        return false;
    }

    private void onSendFile(Intent intent) {
        if (intent.getType() != null && intent.getType().startsWith("audio/")) {
            File newFile = new File(intent.getData().getPath());
            if (FileUtils.existsInternalFile(this, newFile.getName())) {
                Toast.makeText(this, getString(R.string.error_entry_exists), Toast.LENGTH_LONG).show();
            } else if (!(onFileAdded(new File(intent.getData().getPath())))) {
                Toast.makeText(this, getString(R.string.error_add, intent.getData().getPath()), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void addSound(File soundFile) {

        // Load the sound file
        int soundId = soundPool.load(soundFile.getAbsolutePath(), 1);

        // Create a new sound object and add it to the adapter
        sounds.add(new Sound(soundId, soundFile.getName()));
        soundAdapter.notifyDataSetChanged();
    }

    public void playSound(int soundId) {
        soundPool.play(soundId, 1f, 1f, 1, 0, 1);
    }

    private void removeAll() {
        new AlertDialog.Builder(this)
                .setMessage(Html.fromHtml(getString(R.string.confirm_remove_all)))
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = sounds.size() - 1; i >= 0; i--) {
                            removeSound(i);
                        }
                    }
                })
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    public void removeSound(int position) {

        // Remove from adapter
        Sound sound = sounds.remove(position);
        soundAdapter.notifyDataSetChanged();

        // Unload from sound pool
        soundPool.unload(sound.getId());

        // Delete from filesystem
        String internalPath = FileUtils.getInternalPath(this, new File(sound.getName()));
        if (internalPath == null || !new File(internalPath).delete()) {
            Toast.makeText(this, getString(R.string.error_remove), Toast.LENGTH_LONG).show();
        }
    }
}
