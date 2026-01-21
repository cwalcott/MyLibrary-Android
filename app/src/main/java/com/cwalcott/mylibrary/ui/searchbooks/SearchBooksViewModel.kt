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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class SearchBookUiState(
    val books: List<Book>,
    val errorMessage: String? = null,
    val searchQuery: String
)

class SearchBooksViewModel(private val openLibraryApiClient: OpenLibraryApiClient) : ViewModel() {
    private val _uiState =
        MutableStateFlow(SearchBookUiState(books = emptyList(), searchQuery = ""))
    val uiState: StateFlow<SearchBookUiState>
        get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState
                .map { it.searchQuery }
                .distinctUntilChanged()
                .debounce(500.milliseconds)
                .collectLatest { query ->
                    _uiState.update { it.copy(books = performSearch(query)) }
                }
        }
    }

    fun retrySearch() {
        _uiState.update { it.copy(errorMessage = null) }

        viewModelScope.launch {
            _uiState.update { it.copy(books = performSearch(it.searchQuery)) }
        }
    }

    fun updateSearchQuery(searchQuery: String) {
        _uiState.update { it.copy(searchQuery = searchQuery) }
    }

    private suspend fun performSearch(query: String): List<Book> {
        if (query.length < 3) {
            return emptyList()
        }

        return try {
            openLibraryApiClient.search(query).map { it.asBook() }
        } catch (_: Exception) {
            _uiState.update { it.copy(errorMessage = "Unable to search. Check your connection.") }
            emptyList()
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
