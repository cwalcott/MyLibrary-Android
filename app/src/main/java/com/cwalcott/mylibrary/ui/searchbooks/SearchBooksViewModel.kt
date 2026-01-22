package com.cwalcott.mylibrary.ui.searchbooks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cwalcott.mylibrary.MyLibraryApp
import com.cwalcott.mylibrary.model.Book
import com.cwalcott.mylibrary.networking.OpenLibraryApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

data class SearchBookUiState(val query: String, val results: ResultsState) {
    sealed interface ResultsState {
        data object Empty : ResultsState

        data object NetworkError : ResultsState

        data class Results(val books: List<Book>) : ResultsState

        data object NoResults : ResultsState
    }
}

class SearchBooksViewModel(private val openLibraryApiClient: OpenLibraryApiClient) : ViewModel() {
    private val _uiState = MutableStateFlow(
        SearchBookUiState(
            query = "",
            results = SearchBookUiState.ResultsState.Empty
        )
    )
    val uiState: StateFlow<SearchBookUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState
                .distinctUntilChangedBy { it.query }
                .debounce(500.milliseconds)
                .collectLatest {
                    _uiState.update { uiState ->
                        uiState.copy(results = performSearch(uiState.query))
                    }
                }
        }
    }

    fun retrySearch() {
        viewModelScope.launch {
            _uiState.update { it.copy(results = performSearch(it.query)) }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    private suspend fun performSearch(query: String): SearchBookUiState.ResultsState {
        if (query.length < 3) {
            return SearchBookUiState.ResultsState.Empty
        }

        return try {
            val books = openLibraryApiClient.search(query).map { it.asBook() }
            if (books.isEmpty()) {
                SearchBookUiState.ResultsState.NoResults
            } else {
                SearchBookUiState.ResultsState.Results(books = books)
            }
        } catch (_: HttpException) {
            SearchBookUiState.ResultsState.NetworkError
        } catch (_: IOException) {
            SearchBookUiState.ResultsState.NetworkError
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                (this[APPLICATION_KEY] as MyLibraryApp).composer.makeSearchBooksViewModel()
            }
        }
    }
}
