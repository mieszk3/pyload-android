package org.pyload.android.client.module

object Utils {
    @JvmStatic
    fun formatSize(size: Long): String {
        var format = size.toDouble()
        var steps = 0
        val sizes = arrayOf("B", "KiB", "MiB", "GiB", "TiB")
        while (format > 1000) {
            format /= 1024.0
            steps++
        }
        return String.format("%.2f %s", format, sizes[steps])
    }
}