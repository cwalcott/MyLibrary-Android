package com.cwalcott.mylibrary.networking

import java.io.IOException

class FakeOpenLibraryApiClient(private val books: List<OpenLibraryBook> = mockBooks) :
    OpenLibraryApiClient {
    var networkError = false

    override suspend fun performSearch(query: String): SearchResponse {
        if (networkError) {
            throw IOException("Network Error")
        }

        return SearchResponse(
            docs = books.filter {
                if (query.startsWith("key:")) {
                    it.key == query.removePrefix("key:")
                } else {
                    it.title.contains(query, ignoreCase = true) ||
                        it.authorName.orEmpty().any { author ->
                            author.contains(query, ignoreCase = true)
                        }
                }
            }
        )
    }
}

val mockBooks = listOf(
    OpenLibraryBook(
        authorName = listOf("J.R.R. Tolkien"),
        coverEditionKey = "OL51711263M",
        key = "/works/OL27482W",
        title = "The Hobbit"
    ),
    OpenLibraryBook(
        authorName = listOf("J.R.R. Tolkien"),
        coverEditionKey = "OL51708686M",
        key = "/works/OL27513W",
        title = "The Fellowship of the Ring"
    ),
    OpenLibraryBook(
        authorName = listOf("Isaac Asimov"),
        coverEditionKey = "OL51565403M",
        key = "/works/OL46125W",
        title = "Foundation"
    ),
    OpenLibraryBook(
        authorName = listOf("Frank Herbert"),
        key = "/works/OL893415W",
        title = "Dune"
    )
)
