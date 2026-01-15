package com.cwalcott.mylibrary.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Flow<T>.test(block: suspend List<T>.() -> Unit) {
    val elements = mutableListOf<T>()
    coroutineScope {
        launch(UnconfinedTestDispatcher()) { toList(elements) }.run {
            block(elements)
            cancel()
        }
    }
}
