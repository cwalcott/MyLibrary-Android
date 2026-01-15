package com.cwalcott.mylibrary.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cwalcott.mylibrary.model.Book

@Database(entities = [Book::class], version = 1)
abstract class RoomAppDatabase : RoomDatabase(), AppDatabase {
    abstract override fun books(): BookDao

    override fun reset() {
        clearAllTables()
    }
}
