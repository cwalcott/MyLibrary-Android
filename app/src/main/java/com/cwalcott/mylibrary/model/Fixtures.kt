package com.cwalcott.mylibrary.model

object Fixtures {
    fun book(): Book = Book(
        authorNames = "J.R.R. Tolkien",
        coverEditionKey = "OL51711263M",
        openLibraryKey = "/works/OL27482W",
        title = "The Hobbit"
    )

    fun book2(): Book = Book(
        authorNames = "Frank Herbert",
        openLibraryKey = "/works/OL893415W",
        title = "Dune"
    )
}
