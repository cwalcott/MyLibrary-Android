package com.cwalcott.mylibrary.di

import android.content.Context
import androidx.room.Room
import com.cwalcott.mylibrary.database.AppDatabase
import com.cwalcott.mylibrary.database.RoomAppDatabase
import com.cwalcott.mylibrary.ui.favorites.FavoritesViewModel

class Composer(val database: AppDatabase) {
    fun makeFavoritesViewModel(): FavoritesViewModel = FavoritesViewModel(database = database)
}

fun createLiveComposer(context: Context): Composer {
    val database =
        Room.databaseBuilder(context.applicationContext, RoomAppDatabase::class.java, "app.db")
            .build()
    return Composer(database = database)
}
