package com.cwalcott.mylibrary.ui.bookdetails

import com.cwalcott.mylibrary.database.FakeInMemoryAppDatabase
import com.cwalcott.mylibrary.model.Fixtures
import com.cwalcott.mylibrary.networking.FakeOpenLibraryApiClient
import com.cwalcott.mylibrary.networking.mockBooks
import com.cwalcott.mylibrary.util.MainDispatcherRule
import com.cwalcott.mylibrary.util.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BookDetailsViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val database = FakeInMemoryAppDatabase()
    private val openLibraryApiClient = FakeOpenLibraryApiClient()

    private val book = Fixtures.book()

    @Before fun setUp() = runTest {
        database.books().insert(book)
    }

    @Test fun initialState_notFavorite() = runTest {
        val openLibraryKey = mockBooks.last().key
        val viewModel = createViewModel(openLibraryKey)
        assertThat(viewModel.book.first()?.openLibraryKey).isEqualTo(openLibraryKey)
        assertThat(viewModel.favoritesState.first())
            .isEqualTo(BookDetailsViewModel.FavoritesState.NOT_FAVORITE)
    }

    @Test fun initialState_favorite() = runTest {
        val viewModel = createViewModel(book.openLibraryKey)
        assertThat(viewModel.book.first()).isEqualTo(book)
        assertThat(viewModel.favoritesState.first())
            .isEqualTo(BookDetailsViewModel.FavoritesState.FAVORITE)
    }

    @Test fun addToFavorites() = runTest {
        val openLibraryKey = mockBooks.last().key
        val viewModel = createViewModel(openLibraryKey)

        viewModel.favoritesState.test {
            assertThat(last()).isEqualTo(BookDetailsViewModel.FavoritesState.NOT_FAVORITE)
            assertThat(database.books().findByOpenLibraryKey(openLibraryKey)).isNull()

            viewModel.addToFavorites()

            assertThat(last()).isEqualTo(BookDetailsViewModel.FavoritesState.FAVORITE)
            assertThat(database.books().findByOpenLibraryKey(openLibraryKey)).isNotNull()
        }
    }

    @Test fun removeFromFavorites() = runTest {
        val viewModel = createViewModel(book.openLibraryKey)

        viewModel.favoritesState.test {
            assertThat(last()).isEqualTo(BookDetailsViewModel.FavoritesState.FAVORITE)
            assertThat(database.books().findByOpenLibraryKey(book.openLibraryKey)).isNotNull()

            viewModel.removeFromFavorites()

            assertThat(last()).isEqualTo(BookDetailsViewModel.FavoritesState.NOT_FAVORITE)
            assertThat(database.books().findByOpenLibraryKey(book.openLibraryKey)).isNull()
        }
    }

    private fun createViewModel(openLibraryKey: String) = BookDetailsViewModel(
        database = database,
        openLibraryApiClient = openLibraryApiClient,
        openLibraryKey = openLibraryKey
    )
}
