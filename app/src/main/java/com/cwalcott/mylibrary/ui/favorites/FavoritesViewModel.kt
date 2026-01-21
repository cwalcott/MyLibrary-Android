package com.cwalcott.mylibrary.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cwalcott.mylibrary.MyLibraryApp
import com.cwalcott.mylibrary.database.AppDatabase
import com.cwalcott.mylibrary.model.Book
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(database: AppDatabase) : ViewModel() {
    val books: StateFlow<List<Book>?> = database.books().streamAll()
        .map { it as List<Book>? }
        .catch { emit(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), null)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                (this[APPLICATION_KEY] as MyLibraryApp).composer.makeFavoritesViewModel()
            }
        }
    }
}
