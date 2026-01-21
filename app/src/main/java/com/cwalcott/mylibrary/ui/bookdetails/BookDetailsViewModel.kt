package com.cwalcott.mylibrary.ui.bookdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cwalcott.mylibrary.MyLibraryApp
import com.cwalcott.mylibrary.database.AppDatabase
import com.cwalcott.mylibrary.model.Book
import com.cwalcott.mylibrary.networking.OpenLibraryApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookDetailsViewModel(
    private val database: AppDatabase,
    private val openLibraryApiClient: OpenLibraryApiClient,
    private val openLibraryKey: String
) : ViewModel() {
    enum class FavoritesState {
        FAVORITE,
        NOT_FAVORITE,
        HIDDEN
    }

    private val _book = MutableStateFlow<Book?>(null)
    val book: StateFlow<Book?>
        get() = _book.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?>
        get() = _errorMessage.asStateFlow()

    private val _favoritesState = MutableStateFlow<FavoritesState>(FavoritesState.HIDDEN)
    val favoritesState: StateFlow<FavoritesState>
        get() = _favoritesState.asStateFlow()

    init {
        viewModelScope.launch {
            val localBook = try {
                database.books().findByOpenLibraryKey(openLibraryKey)
            } catch (_: Exception) {
                null
            }

            if (localBook != null) {
                _book.value = localBook
                _favoritesState.value = FavoritesState.FAVORITE
                return@launch
            }

            try {
                val book = openLibraryApiClient.getBook(openLibraryKey)?.asBook()
                if (book != null) {
                    _book.value = book
                    _favoritesState.value = FavoritesState.NOT_FAVORITE
                } else {
                    _book.value = null
                    _favoritesState.value = FavoritesState.HIDDEN
                    _errorMessage.value = "Book not found"
                }
            } catch (_: Exception) {
                _book.value = null
                _favoritesState.value = FavoritesState.HIDDEN
                _errorMessage.value = "Unable to load book. Check your connection."
            }
        }
    }

    fun addToFavorites() {
        viewModelScope.launch {
            val book = _book.value
            if (book == null || _favoritesState.value != FavoritesState.NOT_FAVORITE) {
                return@launch
            }

            try {
                database.books().insert(book)
                _favoritesState.value = FavoritesState.FAVORITE
            } catch (_: Exception) {
                _errorMessage.value = "Failed to add to favorites"
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun removeFromFavorites() {
        viewModelScope.launch {
            if (_favoritesState.value != FavoritesState.FAVORITE) {
                return@launch
            }

            try {
                database.books().deleteByOpenLibraryKey(openLibraryKey)
                _favoritesState.value = FavoritesState.NOT_FAVORITE
            } catch (_: Exception) {
                _errorMessage.value = "Failed to remove from favorites"
            }
        }
    }

    companion object {
        fun factory(openLibraryKey: String) = viewModelFactory {
            initializer {
                (this[APPLICATION_KEY] as MyLibraryApp)
                    .composer.makeBookDetailsViewModel(openLibraryKey)
            }
        }
    }
}
