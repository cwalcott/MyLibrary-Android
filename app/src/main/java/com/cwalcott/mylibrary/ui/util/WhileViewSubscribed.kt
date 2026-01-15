package com.cwalcott.mylibrary.ui.util

import kotlinx.coroutines.flow.SharingStarted

private const val StopTimeoutMillis = 5_000L
val WhileViewSubscribed = SharingStarted.WhileSubscribed(StopTimeoutMillis)
