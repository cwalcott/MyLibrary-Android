package com.cwalcott.mylibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.cwalcott.mylibrary.ui.bookdetails.BookDetailsScreen
import com.cwalcott.mylibrary.ui.favorites.FavoritesScreen
import com.cwalcott.mylibrary.ui.searchbooks.SearchBooksScreen
import com.cwalcott.mylibrary.ui.theme.MyLibraryTheme
import kotlinx.serialization.Serializable

sealed interface NavRoute : NavKey {
    @Serializable
    data class BookDetails(val bookId: String) : NavRoute

    @Serializable
    data object Favorites : NavRoute

    @Serializable
    data object SearchBooks : NavRoute
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MyLibraryTheme {
                val backStack = rememberNavBackStack(NavRoute.Favorites)

                val onBack: () -> Unit = { backStack.removeLastOrNull() }

                NavDisplay(
                    backStack = backStack,
                    onBack = onBack,
                    entryProvider = entryProvider {
                        entry<NavRoute.BookDetails> { key ->
                            BookDetailsScreen(bookId = key.bookId, onBack = onBack)
                        }

                        entry<NavRoute.Favorites> {
                            FavoritesScreen(
                                onAddBook = { backStack.add(NavRoute.SearchBooks) },
                                onViewBook = { backStack.add(NavRoute.BookDetails(it)) }
                            )
                        }

                        entry<NavRoute.SearchBooks> {
                            SearchBooksScreen(onBack = onBack)
                        }
                    }
                )
            }
        }
    }
}
