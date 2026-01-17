package com.cwalcott.mylibrary.database

import com.cwalcott.mylibrary.model.Fixtures
import com.cwalcott.mylibrary.util.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

abstract class BookDaoTest {
    abstract val database: AppDatabase

    private val bookDao: BookDao
        get() = database.books()

    private val book = Fixtures.book()

    @Test fun deleteByOpenLibraryKey() = runTest {
        bookDao.insert(book)

        bookDao.deleteByOpenLibraryKey(book.openLibraryKey)

        assertThat(bookDao.findByOpenLibraryKey(book.openLibraryKey)).isNull()
    }

    @Test fun insert_findByOpenLibraryKey() = runTest {
        bookDao.insert(book)

        assertThat(bookDao.findByOpenLibraryKey(book.openLibraryKey)).isEqualTo(book)
        assertThat(bookDao.findByOpenLibraryKey("unknown")).isNull()
    }

    @Test fun streamAll() = runTest {
        val book2 = Fixtures.book2()

        bookDao.streamAll().test {
            assertThat(last()).isEmpty()

            bookDao.insert(book)
            assertThat(last()).containsExactly(book)

            bookDao.insert(book2)
            assertThat(last()).containsExactly(book2, book)
        }
    }
}
