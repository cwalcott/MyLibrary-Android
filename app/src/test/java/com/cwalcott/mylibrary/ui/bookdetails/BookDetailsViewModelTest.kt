package com.cwalcott.mylibrary.ui.bookdetails

import com.cwalcott.mylibrary.database.FakeInMemoryAppDatabase
import com.cwalcott.mylibrary.model.Fixtures
import com.cwalcott.mylibrary.networking.FakeOpenLibraryApiClient
import com.cwalcott.mylibrary.networking.mockBooks
import com.cwalcott.mylibrary.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
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

        val state = viewModel.uiState.value
        assertThat(state.book?.openLibraryKey).isEqualTo(openLibraryKey)
        assertThat(state.favoritesState).isEqualTo(BookDetailsUiState.FavoritesState.NOT_FAVORITE)
    }

    @Test fun initialState_favorite() = runTest {
        val viewModel = createViewModel(book.openLibraryKey)

        val state = viewModel.uiState.value
        assertThat(state.book).isEqualTo(book)
        assertThat(state.favoritesState).isEqualTo(BookDetailsUiState.FavoritesState.FAVORITE)
    }

    @Test fun initialState_networkError() = runTest {
        val openLibraryKey = mockBooks.last().key
        openLibraryApiClient.networkError = true
        val viewModel = createViewModel(openLibraryKey)

        val state = viewModel.uiState.value
        assertThat(state.book).isNull()
        assertThat(state.favoritesState).isEqualTo(BookDetailsUiState.FavoritesState.HIDDEN)
        assertThat(state.errorMessage).isNotNull()

        viewModel.clearErrorMessage()
        assertThat(viewModel.uiState.value.errorMessage).isNull()
    }

    @Test fun addToFavorites() = runTest {
        val openLibraryKey = mockBooks.last().key
        val viewModel = createViewModel(openLibraryKey)

        assertThat(viewModel.uiState.value.favoritesState)
            .isEqualTo(BookDetailsUiState.FavoritesState.NOT_FAVORITE)
        assertThat(database.books().findByOpenLibraryKey(openLibraryKey)).isNull()

        viewModel.addToFavorites()

        assertThat(viewModel.uiState.value.favoritesState)
            .isEqualTo(BookDetailsUiState.FavoritesState.FAVORITE)
        assertThat(database.books().findByOpenLibraryKey(openLibraryKey)).isNotNull()
    }

    @Test fun addToFavorites_error() = runTest {
        val openLibraryKey = mockBooks.last().key
        val viewModel = createViewModel(openLibraryKey)
        database.databaseError = true

        assertThat(viewModel.uiState.value.errorMessage).isNull()

        viewModel.addToFavorites()

        assertThat(viewModel.uiState.value.errorMessage).isNotNull()
        assertThat(viewModel.uiState.value.favoritesState)
            .isEqualTo(BookDetailsUiState.FavoritesState.NOT_FAVORITE)
    }

    @Test fun removeFromFavorites() = runTest {
        val viewModel = createViewModel(book.openLibraryKey)

        assertThat(viewModel.uiState.value.favoritesState)
            .isEqualTo(BookDetailsUiState.FavoritesState.FAVORITE)
        assertThat(database.books().findByOpenLibraryKey(book.openLibraryKey)).isNotNull()

        viewModel.removeFromFavorites()

        assertThat(viewModel.uiState.value.favoritesState)
            .isEqualTo(BookDetailsUiState.FavoritesState.NOT_FAVORITE)
        assertThat(database.books().findByOpenLibraryKey(book.openLibraryKey)).isNull()
    }

    @Test fun removeFromFavorites_error() = runTest {
        val viewModel = createViewModel(book.openLibraryKey)
        database.databaseError = true

        assertThat(viewModel.uiState.value.errorMessage).isNull()

        viewModel.removeFromFavorites()

        assertThat(viewModel.uiState.value.errorMessage).isNotNull()
        assertThat(viewModel.uiState.value.favoritesState)
            .isEqualTo(BookDetailsUiState.FavoritesState.FAVORITE)
    }

    private fun createViewModel(openLibraryKey: String) = BookDetailsViewModel(
        database = database,
        openLibraryApiClient = openLibraryApiClient,
        openLibraryKey = openLibraryKey
    )
}
