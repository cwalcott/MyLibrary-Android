package com.cwalcott.mylibrary.networking

import com.cwalcott.mylibrary.model.Book
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

@Serializable
data class SearchResponse(val docs: List<OpenLibraryBook>)

@Serializable
data class OpenLibraryBook(
    @SerialName("author_name") val authorName: List<String>? = null,
    @SerialName("cover_edition_key") val coverEditionKey: String? = null,
    val key: String,
    val title: String
) {
    fun asBook(): Book = Book(
        authorNames = authorName.orEmpty().joinToString(", "),
        coverEditionKey = coverEditionKey,
        openLibraryKey = key,
        title = title
    )
}

interface OpenLibraryApiClient {
    suspend fun getBook(key: String): OpenLibraryBook? =
        performSearch("key:$key").docs.firstOrNull()

    suspend fun search(query: String): List<OpenLibraryBook> = performSearch(query).docs

    @GET("/search.json")
    suspend fun performSearch(@Query("q") query: String): SearchResponse
}
