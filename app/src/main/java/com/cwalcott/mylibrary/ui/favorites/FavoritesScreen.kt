package com.cwalcott.mylibrary.ui.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cwalcott.mylibrary.R
import com.cwalcott.mylibrary.model.Book
import com.cwalcott.mylibrary.model.Fixtures
import com.cwalcott.mylibrary.ui.theme.MyLibraryTheme

@Composable
fun FavoritesScreen(
    onAddBook: () -> Unit,
    onViewBook: (String) -> Unit,
    viewModel: FavoritesViewModel = viewModel(factory = FavoritesViewModel.Factory)
) {
    val books by viewModel.books.collectAsStateWithLifecycle()

    books?.let { books ->
        FavoritesContent(books = books, onAddBook = onAddBook, onViewBook = onViewBook)
    }
}

@Composable
private fun FavoritesContent(
    books: List<Book>,
    onAddBook: () -> Unit,
    onViewBook: (String) -> Unit
) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Favorite Books") },
                actions = {
                    IconButton(onClick = onAddBook) {
                        Icon(
                            painter = painterResource(id = R.drawable.add),
                            contentDescription = "Add Book"
                        )
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(items = books, key = { it.uuid }) { book ->
                HorizontalDivider()

                ListItem(
                    headlineContent = { Text(book.title) },
                    supportingContent = {
                        if (book.authorNames != null) {
                            Text(book.authorNames)
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.White),
                    modifier = Modifier.clickable { onViewBook(book.openLibraryKey) }
                )

                HorizontalDivider()
            }
        }
    }
}

@Preview
@Composable
fun FavoritesScreenPreview() {
    MyLibraryTheme {
        FavoritesContent(books = listOf(Fixtures.book()), onAddBook = {}, onViewBook = {})
    }
}
