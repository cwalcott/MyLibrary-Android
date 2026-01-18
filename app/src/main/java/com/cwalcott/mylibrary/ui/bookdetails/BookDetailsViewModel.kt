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

    private val _favoritesState = MutableStateFlow<FavoritesState>(FavoritesState.HIDDEN)
    val favoritesState: StateFlow<FavoritesState>
        get() = _favoritesState.asStateFlow()

    init {
        viewModelScope.launch {
            val localBook = database.books().findByOpenLibraryKey(openLibraryKey)
            if (localBook != null) {
                _book.value = localBook
                _favoritesState.value = FavoritesState.FAVORITE
                return@launch
            }

            _book.value = openLibraryApiClient.getBook(openLibraryKey)?.asBook()
            _favoritesState.value = FavoritesState.NOT_FAVORITE
        }
    }

    fun addToFavorites() {
        viewModelScope.launch {
            val book = _book.value
            if (book == null || _favoritesState.value != FavoritesState.NOT_FAVORITE) {
                return@launch
            }

            database.books().insert(book)
            _favoritesState.value = FavoritesState.FAVORITE
        }
    }

    fun removeFromFavorites() {
        viewModelScope.launch {
            if (_favoritesState.value != FavoritesState.FAVORITE) {
                return@launch
            }

            database.books().deleteByOpenLibraryKey(openLibraryKey)
            _favoritesState.value = FavoritesState.NOT_FAVORITE
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
