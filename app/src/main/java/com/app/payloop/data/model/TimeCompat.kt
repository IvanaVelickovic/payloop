package com.app.payloop.data.model

private const val MILLIS_THRESHOLD = 100_000_000_000L

fun normalizeEpochSeconds(timestamp: Long): Long {
    return if (timestamp > MILLIS_THRESHOLD) timestamp / 1000L else timestamp
}

