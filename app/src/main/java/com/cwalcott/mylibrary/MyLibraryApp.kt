package com.cwalcott.mylibrary

import android.app.Application
import androidx.room.Room
import com.cwalcott.mylibrary.database.AppDatabase
import com.cwalcott.mylibrary.database.RoomAppDatabase

class MyLibraryApp : Application() {
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()

        database =
            Room.databaseBuilder(applicationContext, RoomAppDatabase::class.java, "app.db").build()
    }
}
