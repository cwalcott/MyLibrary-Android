package com.cwalcott.mylibrary.ui.searchbooks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
fun SearchBooksScreen(onBack: (() -> Unit)? = null) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Books") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_back),
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Search Books Placeholder")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBooksScreenPreview() {
    MyLibraryTheme {
        SearchBooksScreen(onBack = {})
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBooksScreenNoBackPreview() {
    MyLibraryTheme {
        SearchBooksScreen()
    }
}
