package de.meonwax.soundboard.file;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.meonwax.soundboard.R;

public class FileUtils {

    private final static String[] EXTENSION_WHITELIST = new String[]{"wav", "mp3", "ogg"};

    /**
     * Returns the absolute path of the parent directory.
     * If the path points to a file instead of a directory, its containing directory is returned.
     */
    public static String getParentDirectory(String directoryPath) {
        if (directoryPath.endsWith(File.separator)) {
            directoryPath = directoryPath.substring(0, directoryPath.lastIndexOf(File.separator));
        }
        return directoryPath.substring(0, directoryPath.lastIndexOf(File.separator));
    }

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
        File externalDir = context.getExternalFilesDir("Sound");
        if (externalDir == null) {
            Toast.makeText(context, context.getString(R.string.error_no_external_storage), Toast.LENGTH_LONG).show();
            return null;
        }
        return externalDir + File.separator + fileName;
    }

    public static List<File> getExternalFiles(Context context) {
        List<File> files = new ArrayList<>();
        File externalDir = context.getExternalFilesDir("Sound");
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
                Log.d(FileUtils.class.getSimpleName(), String.format("Copied %s to %s", file.getAbsolutePath(), outputFile.getAbsolutePath()));
            } catch (IOException e) {
                Log.e(FileUtils.class.getSimpleName(), e.getMessage());
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
}
