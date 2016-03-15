package de.meonwax.soundboard.util;

import android.content.Context;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.meonwax.soundboard.R;

public class FileUtils {

    private static final String LOG_TAG = FileUtils.class.getSimpleName();

    private final static String[] EXTENSION_WHITELIST = new String[]{"wav", "mp3", "ogg"};

    private final static String TYPE_SOUND = "Sound";

    private static Set<File> storageDirectories;

    /**
     * Converts the bytes value into a human readable string
     */
    public static String getSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return Math.round(bytes / 1024f) + " KB";
        }
        if (bytes < 1024 * 1024 * 1024) {
            return Math.round(bytes / 1024f / 1024f) + " MB";
        }
        return Math.round(bytes / 1024f / 1024f / 1024f) + " GB";
    }

    public static boolean isWhitelisted(File file) {
        return EXTENSION_WHITELIST != null && Arrays.asList(EXTENSION_WHITELIST).contains(getExtension(file).toLowerCase(Locale.US));
    }

    private static String getExtension(File file) {
        int i = file.getName().lastIndexOf('.');
        int k = file.getName().lastIndexOf(File.separator);
        if (i > k) {
            return file.getName().substring(i + 1);
        }
        return "";
    }

    public static String getExternalPath(Context context, File file) {
        return getExternalPath(context, file.getName());
    }

    private static String getExternalPath(Context context, String fileName) {
        File externalDir = context.getExternalFilesDir(TYPE_SOUND);
        if (externalDir == null) {
            Toast.makeText(context, context.getString(R.string.error_no_external_storage), Toast.LENGTH_LONG).show();
            return null;
        }
        return externalDir + File.separator + fileName;
    }

    public static List<File> getExternalFiles(Context context) {
        List<File> files = new ArrayList<>();
        File externalDir = context.getExternalFilesDir(TYPE_SOUND);
        if (externalDir != null) {
            Collections.addAll(files, new File(externalDir.getAbsolutePath()).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return isWhitelisted(file);
                }
            }));
        } else {
            Toast.makeText(context, context.getString(R.string.error_no_external_storage), Toast.LENGTH_LONG).show();
        }
        return files;
    }

    public static boolean existsExternalFile(Context context, String fileName) {
        String externalPath = FileUtils.getExternalPath(context, fileName);
        return (externalPath != null && new File(externalPath).exists());
    }

    public static void copyToExternal(Context context, File file) {
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        String externalPath = getExternalPath(context, file);
        if (externalPath != null) {
            try {
                File outputFile = new File(externalPath);
                inChannel = new FileInputStream(file).getChannel();
                outChannel = new FileOutputStream(outputFile).getChannel();
                inChannel.transferTo(0, inChannel.size(), outChannel);
                Log.d(LOG_TAG, String.format("Copied %s to %s", file.getAbsolutePath(), outputFile.getAbsolutePath()));
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
                try {
                    if (inChannel != null && inChannel.isOpen()) {
                        inChannel.close();
                    }
                    if (outChannel != null && outChannel.isOpen()) {
                        outChannel.close();
                    }
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Get all available storage directories.
     * Inspired by CyanogenMod File Manager:
     * https://github.com/CyanogenMod/android_packages_apps_CMFileManager
     */
    public static Set<File> getStorageDirectories(Context context) {
        if (storageDirectories == null) {
            try {
                // Use reflection to retrieve storage volumes because required classes and methods are hidden in AOSP.
                StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
                Method method = storageManager.getClass().getMethod("getVolumeList");
                StorageVolume[] storageVolumes = (StorageVolume[]) method.invoke(storageManager);
                if (storageVolumes != null && storageVolumes.length > 0) {
                    storageDirectories = new HashSet<>();
                    for (StorageVolume volume : storageVolumes) {
                        storageDirectories.add(new File(volume.getPath()));
                    }
                }

            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
        return storageDirectories;
    }
}
