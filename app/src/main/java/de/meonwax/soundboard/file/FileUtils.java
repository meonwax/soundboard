package de.meonwax.soundboard.file;

import java.io.File;

public class FileUtils {

    /**
     * Returns the absolute path of the parent directory.
     * If the path points to a file instead of a directory, its containing directory is returned.
     */
    public static String getParentDirectory(String directoryPath) {
        if (directoryPath.endsWith(File.separator)) {
            directoryPath = directoryPath.substring(0, directoryPath.lastIndexOf(File.separator));
        }
        String parent = directoryPath.substring(0, directoryPath.lastIndexOf(File.separator));
        return parent;
    }

    public static String getSize(File file) {
        return getSize(file.length());
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
}
