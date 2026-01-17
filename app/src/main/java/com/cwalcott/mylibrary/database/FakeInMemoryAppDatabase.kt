package com.cwalcott.mylibrary.database

import com.cwalcott.mylibrary.model.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.collections.plus

class FakeInMemoryAppDatabase : AppDatabase {
    private val books = MutableStateFlow<List<Book>>(emptyList())

    private val bookDao = FakeBookDao()

    override fun books(): BookDao = bookDao

    override fun reset() {
        books.value = emptyList()
    }

    private inner class FakeBookDao : BookDao {
        override suspend fun deleteByOpenLibraryKey(openLibraryKey: String) {
            books.value = books.value.filter { it.openLibraryKey != openLibraryKey }
        }

        override suspend fun findByOpenLibraryKey(openLibraryKey: String): Book? =
            books.value.find { it.openLibraryKey == openLibraryKey }

        override suspend fun insert(book: Book) {
            books.value = books.value.filter { it.openLibraryKey != book.openLibraryKey } + book
        }

        override fun streamAll(): Flow<List<Book>> = books
    }
}
