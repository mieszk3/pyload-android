package org.pyload.android.client.module;

import androidx.annotation.NonNull;

public final class Utils {

    @NonNull
    public static String formatSize(long size) {
        double format = size;
        int steps = 0;
        String[] sizes = {"B", "KiB", "MiB", "GiB", "TiB"};
        while (format > 1000) {
            format /= 1024.0;
            steps++;
        }
        return String.format("%.2f %s", format, sizes[steps]);
    }
}
