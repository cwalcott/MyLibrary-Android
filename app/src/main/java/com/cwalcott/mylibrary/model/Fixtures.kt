package com.cwalcott.mylibrary.model

object Fixtures {
    fun book(): Book = Book(
        authorNames = "J.R.R. Tolkien",
        openLibraryKey = "/works/OL27482W",
        title = "The Hobbit"
    )
}
