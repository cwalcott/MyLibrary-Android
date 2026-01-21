package com.cwalcott.mylibrary.database

import android.database.SQLException
import com.cwalcott.mylibrary.model.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class FakeInMemoryAppDatabase : AppDatabase {
    var databaseError = false

    private val books = MutableStateFlow<List<Book>>(emptyList())

    private val bookDao = FakeBookDao()

    override fun books(): BookDao = bookDao

    override fun reset() {
        books.value = emptyList()
    }

    private inner class FakeBookDao : BookDao {
        override suspend fun deleteByOpenLibraryKey(openLibraryKey: String) {
            if (databaseError) {
                throw SQLException("Database Error")
            }

            books.value = books.value.filter { it.openLibraryKey != openLibraryKey }
        }

        override suspend fun findByOpenLibraryKey(openLibraryKey: String): Book? {
            if (databaseError) {
                throw SQLException("Database Error")
            }

            return books.value.find { it.openLibraryKey == openLibraryKey }
        }

        override suspend fun insert(book: Book) {
            if (databaseError) {
                throw SQLException("Database Error")
            }

            books.value = books.value.filter { it.openLibraryKey != book.openLibraryKey } + book
        }

        override fun streamAll(): Flow<List<Book>> = if (databaseError) {
            flow { throw SQLException("Database Error") }
        } else {
            books
        }
    }
}
