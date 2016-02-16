package de.meonwax.soundboard.file;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileUtils {

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

    public static String getExtension(File file) {
        int i = file.getName().lastIndexOf('.');
        int k = file.getName().lastIndexOf(File.separator);
        if (i > k) {
            return file.getName().substring(i + 1);
        }
        return "";
    }

    public static String getInternalPath(Context context, File file) {
        return context.getFilesDir() + File.separator + file.getName();
    }

    public static List<File> getInternalFiles(Context context) {
        List<File> files = new ArrayList<>();
        Collections.addAll(files, new File(context.getFilesDir().getAbsolutePath()).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return Arrays.asList(FilePickerDialogFragment.EXTENSION_WHITELIST).contains(FileUtils.getExtension(file).toLowerCase());
            }
        }));
        return files;
    }

    public static void copyToInternal(Context context, File file) {
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = new FileInputStream(file).getChannel();
            outChannel = context.openFileOutput(file.getName(), Context.MODE_PRIVATE).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            Log.d(FileUtils.class.getSimpleName(), String.format("Copied %s to %s", file.getAbsolutePath(), getInternalPath(context, file)));
        } catch (IOException e) {
            Log.e(FileUtils.class.getSimpleName(), e.getMessage());
            try {
                if (inChannel != null && inChannel.isOpen()) {
                    inChannel.close();
                }
                if (outChannel != null && outChannel.isOpen()) {
                    outChannel.close();
                }
            } catch (IOException e1) {
            }
        }
    }
}
