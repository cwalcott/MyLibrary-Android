package com.cwalcott.mylibrary.ui.bookdetails

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cwalcott.mylibrary.model.Fixtures
import com.cwalcott.mylibrary.networking.FakeOpenLibraryApiClient
import com.cwalcott.mylibrary.ui.theme.MyLibraryTheme
import com.cwalcott.mylibrary.util.getApplicationComposer
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookDetailsScreenTest {
    @get:Rule val composeTestRule = createComposeRule()

    private val book = Fixtures.book()
    private val book2 = Fixtures.book2()

    private val database = getApplicationComposer().database
    private val openLibraryApiClient =
        getApplicationComposer().openLibraryApiClient as FakeOpenLibraryApiClient

    @Before fun setUp() = runTest {
        database.books().insert(book)
    }

    @After fun tearDown() = runTest {
        database.reset()
        openLibraryApiClient.networkError = false
    }

    @Test fun rendersBookDetails() {
        with(composeTestRule) {
            setContent()

            onNodeWithText(book.title).assertIsDisplayed()
            onNodeWithText(book.authorNames.orEmpty()).assertIsDisplayed()
        }
    }

    @Test fun handlesNetworkErrors() {
        openLibraryApiClient.networkError = true

        with(composeTestRule) {
            setContent(book2.openLibraryKey)

            onNodeWithText(book.title).assertDoesNotExist()
            onNodeWithText("Error").assertIsDisplayed()
        }
    }

    @Test fun addToFavorites() = runTest {
        with(composeTestRule) {
            setContent(book2.openLibraryKey)
            assertThat(database.books().findByOpenLibraryKey(book2.openLibraryKey)).isNull()
            onNodeWithText("Add to Favorites").assertExists()
            onNodeWithText("Remove from Favorites").assertDoesNotExist()

            onNodeWithText("Add to Favorites").performClick()

            assertThat(database.books().findByOpenLibraryKey(book2.openLibraryKey)).isNotNull()
            onNodeWithText("Add to Favorites").assertDoesNotExist()
            onNodeWithText("Remove from Favorites").assertExists()
        }
    }

    @Test fun removeFromFavorites() = runTest {
        with(composeTestRule) {
            setContent(book.openLibraryKey)
            assertThat(database.books().findByOpenLibraryKey(book.openLibraryKey)).isNotNull()
            onNodeWithText("Add to Favorites").assertDoesNotExist()
            onNodeWithText("Remove from Favorites").assertExists()

            onNodeWithText("Remove from Favorites").performClick()

            assertThat(database.books().findByOpenLibraryKey(book.openLibraryKey)).isNull()
            onNodeWithText("Add to Favorites").assertExists()
            onNodeWithText("Remove from Favorites").assertDoesNotExist()
        }
    }

    private fun ComposeContentTestRule.setContent(openLibraryKey: String = book.openLibraryKey) {
        setContent {
            MyLibraryTheme {
                BookDetailsScreen(openLibraryKey = openLibraryKey, onBack = {})
            }
        }
    }
}
