package com.cwalcott.mylibrary.util

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.cwalcott.mylibrary.database.RoomAppDatabase

fun inMemoryDatabase(): RoomAppDatabase =
    Room.inMemoryDatabaseBuilder(getInstrumentation().context, RoomAppDatabase::class.java)
        .build()
