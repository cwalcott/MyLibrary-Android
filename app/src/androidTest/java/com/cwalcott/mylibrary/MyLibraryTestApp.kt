package com.cwalcott.mylibrary

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import coil3.ColorImage
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.test.FakeImageLoaderEngine
import com.cwalcott.mylibrary.di.Composer
import com.cwalcott.mylibrary.networking.FakeOpenLibraryApiClient
import com.cwalcott.mylibrary.util.inMemoryDatabase

class MyLibraryTestApp : MyLibraryApp() {
    override fun createComposer(): Composer =
        Composer(database = inMemoryDatabase(), openLibraryApiClient = FakeOpenLibraryApiClient())

    override fun onCreate() {
        super.onCreate()

        SingletonImageLoader.setSafe { context ->
            val engine = FakeImageLoaderEngine.Builder()
                .default(ColorImage(Color.Red.toArgb()))
                .build()
            ImageLoader.Builder(context)
                .components { add(engine) }
                .build()
        }
    }
}
