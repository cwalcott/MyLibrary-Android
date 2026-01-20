package com.cwalcott.mylibrary.ui

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.cwalcott.mylibrary.ui.bookdetails.BookDetailsScreen
import com.cwalcott.mylibrary.ui.favorites.FavoritesScreen
import com.cwalcott.mylibrary.ui.searchbooks.SearchBooksScreen
import kotlinx.serialization.Serializable

sealed interface NavRoute : NavKey {
    @Serializable
    data class BookDetails(val openLibraryKey: String) : NavRoute

    @Serializable
    data object Favorites : NavRoute

    @Serializable
    data object SearchBooks : NavRoute
}

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(NavRoute.Favorites)

    val onBack: () -> Unit = { backStack.removeLastOrNull() }

    NavDisplay(
        backStack = backStack,
        onBack = onBack,
        entryProvider = entryProvider {
            entry<NavRoute.BookDetails> { key ->
                BookDetailsScreen(openLibraryKey = key.openLibraryKey, onBack = onBack)
            }

            entry<NavRoute.Favorites> {
                FavoritesScreen(
                    onAddBook = { backStack.add(NavRoute.SearchBooks) },
                    onViewBook = { backStack.add(NavRoute.BookDetails(it)) }
                )
            }

            entry<NavRoute.SearchBooks> {
                SearchBooksScreen(
                    onBack = onBack,
                    onViewBook = { backStack.add(NavRoute.BookDetails(it)) }
                )
            }
        }
    )
}
