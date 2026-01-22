package com.cwalcott.mylibrary.ui.searchbooks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.cwalcott.mylibrary.R
import com.cwalcott.mylibrary.model.Book
import com.cwalcott.mylibrary.model.Fixtures
import com.cwalcott.mylibrary.ui.shared.ContentUnavailableView
import com.cwalcott.mylibrary.ui.theme.MyLibraryTheme
import com.cwalcott.mylibrary.ui.util.WithAsyncImagePreviewHandler
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SearchBooksScreen(
    onBack: () -> Unit,
    onViewBook: (String) -> Unit,
    viewModel: SearchBooksViewModel = viewModel(factory = SearchBooksViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SearchBooksScreen(
        state = state,
        onBack = onBack,
        onViewBook = onViewBook,
        onRetrySearchQuery = viewModel::retrySearch,
        onUpdateSearchQuery = viewModel::updateSearchQuery
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBooksScreen(
    state: SearchBookUiState,
    onBack: () -> Unit,
    onViewBook: (String) -> Unit,
    onRetrySearchQuery: () -> Unit,
    onUpdateSearchQuery: (String) -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        if (state.results is SearchBookUiState.ResultsState.NetworkError) {
            ContentUnavailableView(
                iconPainter = painterResource(id = R.drawable.warning),
                subheadlineText = "Unable to search. Check your connection.",
                bottomContent = {
                    Button(onClick = onRetrySearchQuery) {
                        Text("Retry")
                    }
                },
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        } else {
            val searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Expanded)

            val textFieldState = rememberTextFieldState()
            LaunchedEffect(textFieldState) {
                snapshotFlow { textFieldState.text.toString() }
                    .collectLatest { onUpdateSearchQuery(it) }
            }

            val inputField =
                @Composable {
                    SearchBarDefaults.InputField(
                        textFieldState = textFieldState,
                        searchBarState = searchBarState,
                        onSearch = {},
                        placeholder = {
                            Text(modifier = Modifier.clearAndSetSemantics {}, text = "Search")
                        },
                        leadingIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_back),
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                }

            Row(modifier = Modifier.padding(innerPadding)) {
                SearchBar(
                    state = searchBarState,
                    inputField = inputField,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ExpandedFullScreenSearchBar(state = searchBarState, inputField = inputField) {
                when (state.results) {
                    SearchBookUiState.ResultsState.Empty -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                        }
                    }

                    SearchBookUiState.ResultsState.NoResults -> {
                        ContentUnavailableView(
                            iconPainter = painterResource(R.drawable.search),
                            headlineText = "No Results Found",
                            subheadlineText = "Check the spelling or try a new search.",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    is SearchBookUiState.ResultsState.Results -> {
                        SearchResults(
                            books = state.results.books,
                            onBookClick = { onViewBook(it.openLibraryKey) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResults(
    books: List<Book>,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(items = books, key = { it.openLibraryKey }) { book ->
            ListItem(
                headlineContent = { Text(book.title) },
                supportingContent = {
                    if (book.authorNames != null) {
                        Text(book.authorNames)
                    }
                },
                leadingContent = {
                    AsyncImage(
                        model = book.coverImageUrl,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                },
                modifier = Modifier.clickable { onBookClick(book) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBooksScreenPreview() {
    MyLibraryTheme {
        WithAsyncImagePreviewHandler {
            SearchBooksScreen(
                state = SearchBookUiState(
                    query = "",
                    results = SearchBookUiState.ResultsState.Results(
                        books = listOf(Fixtures.book(), Fixtures.book2())
                    )
                ),
                onBack = {},
                onViewBook = {},
                onRetrySearchQuery = {},
                onUpdateSearchQuery = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBooksScreenNoResultsFoundPreview() {
    MyLibraryTheme {
        WithAsyncImagePreviewHandler {
            SearchBooksScreen(
                state = SearchBookUiState(
                    query = "",
                    results = SearchBookUiState.ResultsState.NoResults
                ),
                onBack = {},
                onViewBook = {},
                onRetrySearchQuery = {},
                onUpdateSearchQuery = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBooksScreenEmptyPreview() {
    MyLibraryTheme {
        WithAsyncImagePreviewHandler {
            SearchBooksScreen(
                state = SearchBookUiState(
                    query = "",
                    results = SearchBookUiState.ResultsState.Empty
                ),
                onBack = {},
                onViewBook = {},
                onRetrySearchQuery = {},
                onUpdateSearchQuery = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBooksScreenErrorPreview() {
    MyLibraryTheme {
        SearchBooksScreen(
            state = SearchBookUiState(
                query = "",
                results = SearchBookUiState.ResultsState.NetworkError
            ),
            onBack = {},
            onViewBook = {},
            onRetrySearchQuery = {},
            onUpdateSearchQuery = {}
        )
    }
}
