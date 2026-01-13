package com.cwalcott.mylibrary.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.cwalcott.mylibrary.R
import com.cwalcott.mylibrary.ui.theme.MyLibraryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(onAddBook: () -> Unit, onViewBook: (String) -> Unit) {
    Scaffold(
        topBar = {
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text("Favorites Placeholder")

            Button(onClick = { onViewBook("1") }) {
                Text("View Book 1")
            }

            Button(onClick = { onViewBook("2") }) {
                Text("View Book 2")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    MyLibraryTheme {
        FavoritesScreen(onAddBook = {}, onViewBook = {})
    }
}
