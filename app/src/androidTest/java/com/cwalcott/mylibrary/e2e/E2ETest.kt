package com.cwalcott.mylibrary.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cwalcott.mylibrary.model.Fixtures
import com.cwalcott.mylibrary.ui.MainActivity
import com.cwalcott.mylibrary.util.getApplicationComposer
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class E2ETest {
    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val database = getApplicationComposer().database

    @After fun tearDown() = runTest {
        database.reset()
    }

    @Test fun addFavoriteBook() {
        with(composeTestRule) {
            onNodeWithText("The Hobbit").assertDoesNotExist()

            onNodeWithContentDescription("Add Book").performClick()

            onAllNodesWithContentDescription("Search").onFirst().performTextInput("Tolkien")
            waitUntilAtLeastOneExists(hasText("The Hobbit"))
            onNodeWithText("The Hobbit").performClick()

            onNodeWithText("Add to Favorites").performClick()

            repeat(2) {
                onAllNodesWithContentDescription("Back").onFirst().performClick()
            }

            onNodeWithText("The Hobbit").assertExists()
        }
    }

    @Test fun removeFavoriteBook() = runTest {
        val book = Fixtures.book()
        database.books().insert(book)

        with(composeTestRule) {
            onNodeWithText(book.title).performClick()

            onNodeWithText("Remove from Favorites").performClick()

            onAllNodesWithContentDescription("Back").onFirst().performClick()

            onNodeWithText(book.title).assertDoesNotExist()
        }
    }
}
