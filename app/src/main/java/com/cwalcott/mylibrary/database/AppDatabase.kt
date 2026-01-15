package com.cwalcott.mylibrary.database

interface AppDatabase {
    fun books(): BookDao

    fun reset()
}
