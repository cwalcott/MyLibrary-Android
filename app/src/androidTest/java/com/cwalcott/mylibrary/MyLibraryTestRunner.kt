package com.cwalcott.mylibrary

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

@Suppress("unused")
class MyLibraryTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application? = super.newApplication(cl, MyLibraryTestApp::class.java.name, context)
}
