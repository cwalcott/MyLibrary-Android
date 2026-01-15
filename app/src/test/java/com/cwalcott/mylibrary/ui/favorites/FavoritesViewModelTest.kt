package com.cwalcott.mylibrary.ui.favorites

import com.cwalcott.mylibrary.database.FakeInMemoryAppDatabase
import com.cwalcott.mylibrary.model.Fixtures
import com.cwalcott.mylibrary.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val database = FakeInMemoryAppDatabase()

    @Test fun initialState() {
        assertThat(createViewModel().books.value).isNull()
    }

    @Test fun state() = runTest {
        val book = Fixtures.book()
        database.books().insert(book)

        assertThat(createViewModel().books.first()).isEqualTo(listOf(book))
    }

    private fun createViewModel() = FavoritesViewModel(database)
}
