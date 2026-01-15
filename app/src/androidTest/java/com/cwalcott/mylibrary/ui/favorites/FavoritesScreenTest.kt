package com.cwalcott.mylibrary.ui.favorites

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cwalcott.mylibrary.model.Fixtures
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
class FavoritesScreenTest {
    @get:Rule val composeTestRule = createComposeRule()

    private val book1 = Fixtures.book().copy(title = "Test Book 1")
    private val book2 = Fixtures.book().copy(title = "Test Book 2")

    private val database = getApplicationComposer().database

    @Before fun setUp() = runTest {
        database.books().insert(book1)
        database.books().insert(book2)
    }

    @After fun tearDown() = runTest {
        database.reset()
    }

    @Test fun rendersBooks() {
        with(composeTestRule) {
            setContent()

            onNodeWithText("Test Book 1").assertIsDisplayed()
            onNodeWithText("Test Book 2").assertIsDisplayed()
        }
    }

    @Test fun onAddBook() {
        with(composeTestRule) {
            var addBookClicked = false
            setContent(onAddBook = { addBookClicked = true })

            assertThat(addBookClicked).isFalse()

            onNodeWithContentDescription("Add Book").performClick()
            assertThat(addBookClicked).isTrue()
        }
    }

    @Test fun onViewBook() {
        with(composeTestRule) {
            var viewBookClicked: String? = null
            setContent(onViewBook = { viewBookClicked = it })

            assertThat(viewBookClicked).isNull()

            onNodeWithText("Test Book 2").performClick()
            assertThat(viewBookClicked).isEqualTo(book2.uuid)
        }
    }

    private fun ComposeContentTestRule.setContent(
        onAddBook: () -> Unit = {},
        onViewBook: (String) -> Unit = {}
    ) {
        setContent {
            MyLibraryTheme {
                FavoritesScreen(onAddBook = onAddBook, onViewBook = onViewBook)
            }
        }
    }
}
