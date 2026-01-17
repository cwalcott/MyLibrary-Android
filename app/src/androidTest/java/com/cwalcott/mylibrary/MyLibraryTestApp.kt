package com.cwalcott.mylibrary

import com.cwalcott.mylibrary.di.Composer
import com.cwalcott.mylibrary.networking.FakeOpenLibraryApiClient
import com.cwalcott.mylibrary.util.inMemoryDatabase

class MyLibraryTestApp : MyLibraryApp() {
    override fun createComposer(): Composer =
        Composer(database = inMemoryDatabase(), openLibraryApiClient = FakeOpenLibraryApiClient())
}
