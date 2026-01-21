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
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SearchBooksViewModel(private val openLibraryApiClient: OpenLibraryApiClient) : ViewModel() {
    private val _books = MutableStateFlow(emptyList<Book>())
    val books: StateFlow<List<Book>>
        get() = _books.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?>
        get() = _errorMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String>
        get() = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            searchQuery
                .debounce(500.milliseconds)
                .collectLatest { _books.value = performSearch(it) }
        }
    }

    fun retrySearch() {
        _errorMessage.value = null
        viewModelScope.launch {
            _books.value = performSearch(searchQuery.value)
        }
    }

    fun updateSearchQuery(searchQuery: String) {
        _searchQuery.value = searchQuery
    }

    private suspend fun performSearch(query: String): List<Book> {
        if (query.length < 3) {
            return emptyList()
        }

        return try {
            openLibraryApiClient.search(query).map { it.asBook() }
        } catch (_: Exception) {
            _errorMessage.value = "Unable to search. Check your connection."
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
