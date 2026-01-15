package com.cwalcott.mylibrary.util

import androidx.test.core.app.ApplicationProvider
import com.cwalcott.mylibrary.MyLibraryApp
import com.cwalcott.mylibrary.di.Composer

fun getApplicationComposer(): Composer =
    ApplicationProvider.getApplicationContext<MyLibraryApp>().composer
