package com.cwalcott.mylibrary.ui.searchbooks

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cwalcott.mylibrary.ui.theme.MyLibraryTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class SearchBooksScreenTest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun searchesBooks() {
        with(composeTestRule) {
            setContent {
                MyLibraryTheme {
                    SearchBooksScreen(onBack = {}, onViewBook = {})
                }
            }

            onAllNodesWithContentDescription("Search").onFirst().performTextInput("Asimov")
            waitUntilAtLeastOneExists(hasText("Foundation"))

            onNodeWithText("Foundation").assertExists()
        }
    }

    @Test fun onViewBook() {
        with(composeTestRule) {
            var viewBook: String? = null
            setContent {
                MyLibraryTheme {
                    SearchBooksScreen(onBack = {}, onViewBook = { viewBook = it })
                }
            }

            onAllNodesWithContentDescription("Search").onFirst().performTextInput("Asimov")
            waitUntilAtLeastOneExists(hasText("Foundation"))
            onNodeWithText("Foundation").performClick()

            assertThat(viewBook).isEqualTo("/works/OL46125W")
        }
    }
}
