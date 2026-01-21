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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookDetailsUiState(
    val book: Book?,
    val errorMessage: String? = null,
    val favoritesState: FavoritesState
) {
    enum class FavoritesState {
        FAVORITE,
        NOT_FAVORITE,
        HIDDEN
    }
}

class BookDetailsViewModel(
    private val database: AppDatabase,
    private val openLibraryApiClient: OpenLibraryApiClient,
    private val openLibraryKey: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        BookDetailsUiState(
            book = null,
            errorMessage = null,
            favoritesState = BookDetailsUiState.FavoritesState.HIDDEN
        )
    )
    val uiState: StateFlow<BookDetailsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val localBook = try {
                database.books().findByOpenLibraryKey(openLibraryKey)
            } catch (_: Exception) {
                null
            }

            if (localBook != null) {
                _uiState.value = BookDetailsUiState(
                    book = localBook,
                    favoritesState = BookDetailsUiState.FavoritesState.FAVORITE
                )
                return@launch
            }

            try {
                val book = openLibraryApiClient.getBook(openLibraryKey)?.asBook()
                if (book != null) {
                    _uiState.value = BookDetailsUiState(
                        book = book,
                        favoritesState = BookDetailsUiState.FavoritesState.NOT_FAVORITE
                    )
                } else {
                    _uiState.value = BookDetailsUiState(
                        book = null,
                        errorMessage = "Book not found",
                        favoritesState = BookDetailsUiState.FavoritesState.HIDDEN
                    )
                }
            } catch (_: Exception) {
                _uiState.value = BookDetailsUiState(
                    book = null,
                    errorMessage = "Unable to load book. Check your connection.",
                    favoritesState = BookDetailsUiState.FavoritesState.HIDDEN
                )
            }
        }
    }

    fun addToFavorites() {
        val book = _uiState.value.book
        if (
            book == null ||
            _uiState.value.favoritesState != BookDetailsUiState.FavoritesState.NOT_FAVORITE
        ) {
            return
        }

        viewModelScope.launch {
            try {
                database.books().insert(book)
                _uiState.update {
                    it.copy(favoritesState = BookDetailsUiState.FavoritesState.FAVORITE)
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to add to favorites")
                }
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun removeFromFavorites() {
        if (_uiState.value.favoritesState != BookDetailsUiState.FavoritesState.FAVORITE) {
            return
        }

        viewModelScope.launch {
            try {
                database.books().deleteByOpenLibraryKey(openLibraryKey)
                _uiState.update {
                    it.copy(favoritesState = BookDetailsUiState.FavoritesState.NOT_FAVORITE)
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to remove from favorites") }
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
