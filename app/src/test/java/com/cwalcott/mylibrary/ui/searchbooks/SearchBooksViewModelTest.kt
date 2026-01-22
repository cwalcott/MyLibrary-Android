package com.cwalcott.mylibrary.ui.searchbooks

import com.cwalcott.mylibrary.networking.FakeOpenLibraryApiClient
import com.cwalcott.mylibrary.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
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
            SearchBookUiState(query = "", results = SearchBookUiState.ResultsState.Empty)
        )
    }

    @Test fun searchBooks() = runTest {
        val viewModel = createViewModel()

        viewModel.updateSearchQuery("tolkien")
        advanceTimeBy(1.seconds)

        assertThat(viewModel.uiState.value.query).isEqualTo("tolkien")
        val results = viewModel.uiState.value.results as? SearchBookUiState.ResultsState.Results
        assertThat(results).isNotNull()
        assertThat(results?.books?.all { it.authorNames?.contains("Tolkien") == true }).isTrue()
    }

    @Test fun searchBooks_error() = runTest {
        val viewModel = createViewModel()
        openLibraryApiClient.networkError = true

        viewModel.updateSearchQuery("Tolkien")
        advanceTimeBy(1.seconds)

        var state = viewModel.uiState.value
        assertThat(state.results).isEqualTo(SearchBookUiState.ResultsState.NetworkError)

        openLibraryApiClient.networkError = false
        viewModel.retrySearch()
        advanceTimeBy(1.seconds)

        state = viewModel.uiState.value
        assertThat(state.results).isInstanceOf(SearchBookUiState.ResultsState.Results::class.java)
    }

    @Test fun searchBooks_debouncesQuery() = runTest {
        val viewModel = createViewModel()

        assertThat(viewModel.uiState.value.results).isEqualTo(SearchBookUiState.ResultsState.Empty)

        viewModel.updateSearchQuery("Asimov")
        assertThat(viewModel.uiState.value.results).isEqualTo(SearchBookUiState.ResultsState.Empty)

        advanceTimeBy(250.milliseconds)
        assertThat(viewModel.uiState.value.results).isEqualTo(SearchBookUiState.ResultsState.Empty)

        advanceTimeBy(500.milliseconds)
        assertThat(viewModel.uiState.value.results)
            .isInstanceOf(SearchBookUiState.ResultsState.Results::class.java)
    }

    @Test fun searchBooks_noResultsFound() = runTest {
        val viewModel = createViewModel()

        viewModel.updateSearchQuery("asdf")
        advanceTimeBy(500.milliseconds)
        runCurrent()
        assertThat(viewModel.uiState.value.results)
            .isEqualTo(SearchBookUiState.ResultsState.NoResults)

        viewModel.updateSearchQuery("tolkien")
        advanceTimeBy(500.milliseconds)
        runCurrent()
        assertThat(viewModel.uiState.value.results)
            .isInstanceOf(SearchBookUiState.ResultsState.Results::class.java)
    }

    private fun createViewModel() = SearchBooksViewModel(openLibraryApiClient)
}
