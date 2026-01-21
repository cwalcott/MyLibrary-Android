package com.cwalcott.mylibrary.ui.searchbooks

import com.cwalcott.mylibrary.networking.FakeOpenLibraryApiClient
import com.cwalcott.mylibrary.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class SearchBooksViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val openLibraryApiClient = FakeOpenLibraryApiClient()

    @Test fun initialState() {
        assertThat(createViewModel().uiState.value).isEqualTo(
            SearchBookUiState(books = emptyList(), errorMessage = null, searchQuery = "")
        )
    }

    @Test fun searchBooks() = runTest {
        val viewModel = createViewModel()

        viewModel.updateSearchQuery("Tolkien")
        advanceTimeBy(1.seconds)

        assertThat(
            viewModel.uiState.value.books.all { it.authorNames?.contains("Tolkien") == true }
        )
    }

    @Test fun searchBooks_error() = runTest {
        val viewModel = createViewModel()
        openLibraryApiClient.networkError = true

        assertThat(viewModel.uiState.value.errorMessage).isNull()
        viewModel.updateSearchQuery("Tolkien")
        advanceTimeBy(1.seconds)

        var state = viewModel.uiState.value
        assertThat(state.errorMessage).isNotNull()
        assertThat(state.books).isEmpty()

        openLibraryApiClient.networkError = false
        viewModel.retrySearch()
        advanceTimeBy(1.seconds)

        state = viewModel.uiState.value
        assertThat(state.errorMessage).isNull()
        assertThat(state.books).isNotEmpty()
    }

    @Test fun searchBooks_debouncesQuery() = runTest {
        val viewModel = createViewModel()

        assertThat(viewModel.uiState.value.books).isEmpty()

        viewModel.updateSearchQuery("Asimov")
        assertThat(viewModel.uiState.value.books).isEmpty()

        advanceTimeBy(250.milliseconds)
        assertThat(viewModel.uiState.value.books).isEmpty()

        advanceTimeBy(500.milliseconds)
        assertThat(viewModel.uiState.value.books).hasSize(1)
    }

    private fun createViewModel() = SearchBooksViewModel(openLibraryApiClient)
}
