package com.cwalcott.mylibrary.ui.searchbooks

import com.cwalcott.mylibrary.networking.FakeOpenLibraryApiClient
import com.cwalcott.mylibrary.util.MainDispatcherRule
import com.cwalcott.mylibrary.util.test
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
        assertThat(createViewModel().books.value).isEmpty()
    }

    @Test fun searchBooks() = runTest {
        val viewModel = createViewModel()
        viewModel.books.test {
            viewModel.updateSearchQuery("Tolkien")

            advanceTimeBy(1.seconds)
            assertThat(last().all { it.authorNames?.contains("Tolkien") == true })
        }
    }

    @Test fun searchBooks_debouncesQuery() = runTest {
        val viewModel = createViewModel()
        viewModel.books.test {
            assertThat(last()).isEmpty()

            viewModel.updateSearchQuery("Asimov")
            assertThat(last()).isEmpty()

            advanceTimeBy(250.milliseconds)
            assertThat(last()).isEmpty()

            advanceTimeBy(500.milliseconds)
            assertThat(last()).hasSize(1)
        }
    }

    private fun createViewModel() = SearchBooksViewModel(openLibraryApiClient)
}
