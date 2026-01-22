package com.cwalcott.mylibrary.ui.bookdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.cwalcott.mylibrary.R
import com.cwalcott.mylibrary.model.Book
import com.cwalcott.mylibrary.model.Fixtures
import com.cwalcott.mylibrary.ui.theme.MyLibraryTheme
import com.cwalcott.mylibrary.ui.util.WithAsyncImagePreviewHandler

@Composable
fun BookDetailsScreen(
    openLibraryKey: String,
    onBack: () -> Unit,
    viewModel: BookDetailsViewModel = viewModel(
        factory = BookDetailsViewModel.factory(openLibraryKey),
        key = openLibraryKey
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BookDetailsScreen(
        state = state,
        onBack = onBack,
        onAddToFavorites = viewModel::addToFavorites,
        onErrorAcknowledged = viewModel::clearErrorMessage,
        onRemoveFromFavorites = viewModel::removeFromFavorites
    )
}

@Composable
private fun BookDetailsScreen(
    state: BookDetailsUiState,
    onBack: () -> Unit,
    onAddToFavorites: () -> Unit,
    onErrorAcknowledged: () -> Unit,
    onRemoveFromFavorites: () -> Unit
) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        if (state.errorMessage != null) {
            AlertDialog(
                title = { Text(text = "Error") },
                text = { Text(text = state.errorMessage) },
                onDismissRequest = onErrorAcknowledged,
                confirmButton = {
                    TextButton(onClick = onErrorAcknowledged) {
                        Text("OK")
                    }
                }
            )
        }

        if (state.book != null) {
            BookContent(
                book = state.book,
                favoritesState = state.favoritesState,
                onAddToFavorites = onAddToFavorites,
                onRemoveFromFavorites = onRemoveFromFavorites,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            BookContentLoading()
        }
    }
}

@Composable
private fun BookContent(
    book: Book,
    favoritesState: BookDetailsUiState.FavoritesState,
    onAddToFavorites: () -> Unit,
    onRemoveFromFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = book.coverImageUrl,
                contentDescription = null,
                placeholder = ColorPainter(Color.Gray.copy(alpha = 0.5f)),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(width = 200.dp, height = 300.dp)
            )

            Text(
                text = book.title,
                style = MaterialTheme.typography.headlineLarge
            )

            if (book.authorNames != null) {
                Text(
                    text = book.authorNames,
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        when (favoritesState) {
            BookDetailsUiState.FavoritesState.FAVORITE -> {
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Button(onClick = onRemoveFromFavorites) {
                        Text(text = "Remove from Favorites")
                    }
                }
            }

            BookDetailsUiState.FavoritesState.NOT_FAVORITE -> {
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Button(onClick = onAddToFavorites) {
                        Text(text = "Add to Favorites")
                    }
                }
            }

            BookDetailsUiState.FavoritesState.HIDDEN -> {}
        }
    }
}

@Composable
private fun BookContentLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(width = 200.dp, height = 300.dp)
                    .background(color = Color.LightGray)
            )

            Text(
                text = "Loading Title",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.placeholder()
            )

            Text(
                text = "Loading Book Authors",
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.placeholder()
            )
        }
    }
}

private fun Modifier.placeholder(color: Color = Color.LightGray): Modifier = then(
    Modifier.drawWithContent {
        drawContent()
        drawRect(color = color)
    }
)

@Preview(showBackground = true)
@Composable
fun BookDetailsScreenPreview() {
    MyLibraryTheme {
        WithAsyncImagePreviewHandler {
            BookDetailsScreen(
                state = BookDetailsUiState(
                    book = Fixtures.book(),
                    favoritesState = BookDetailsUiState.FavoritesState.FAVORITE
                ),
                onBack = {},
                onAddToFavorites = {},
                onErrorAcknowledged = {},
                onRemoveFromFavorites = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookDetailsScreenErrorMessagePreview() {
    MyLibraryTheme {
        WithAsyncImagePreviewHandler {
            BookDetailsScreen(
                state = BookDetailsUiState(
                    book = Fixtures.book(),
                    errorMessage = "Error while loading book",
                    favoritesState = BookDetailsUiState.FavoritesState.HIDDEN
                ),
                onBack = {},
                onAddToFavorites = {},
                onErrorAcknowledged = {},
                onRemoveFromFavorites = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookDetailsScreenLoadingPreview() {
    MyLibraryTheme {
        WithAsyncImagePreviewHandler {
            BookDetailsScreen(
                state = BookDetailsUiState(
                    book = null,
                    favoritesState = BookDetailsUiState.FavoritesState.HIDDEN
                ),
                onBack = {},
                onAddToFavorites = {},
                onErrorAcknowledged = {},
                onRemoveFromFavorites = {}
            )
        }
    }
}
