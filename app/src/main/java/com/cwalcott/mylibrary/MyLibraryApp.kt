package com.cwalcott.mylibrary

import android.app.Application
import com.cwalcott.mylibrary.di.Composer
import com.cwalcott.mylibrary.di.createLiveComposer

open class MyLibraryApp : Application() {
    lateinit var composer: Composer

    override fun onCreate() {
        super.onCreate()

        composer = createComposer()
    }

    open fun createComposer(): Composer = createLiveComposer(this)
}
