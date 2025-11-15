package com.chanssem.freedive.ui.table

object TimeFormatter {
    fun formatMillis(millis: Long): String {
        val totalSeconds = (millis / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun formatSeconds(seconds: Int): String {
        return "${seconds}초"
    }
}

