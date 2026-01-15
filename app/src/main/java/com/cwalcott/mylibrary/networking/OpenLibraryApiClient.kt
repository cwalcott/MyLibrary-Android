package com.cwalcott.mylibrary.networking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

/* Example usage:
        val retroJson = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl("https://openlibrary.org/")
            .addConverterFactory(
                retroJson
                    .asConverterFactory("application/json; charset=utf-8".toMediaType())
            )
            .build()

        val client = retrofit.create(OpenLibraryApiClient::class.java)

        client.search("Hobbit")
 */

@Serializable
data class SearchResponse(val docs: List<OpenLibraryBook>)

@Serializable
data class OpenLibraryBook(
    @SerialName("author_name") val authorName: List<String>? = null,
    @SerialName("cover_edition_key") val coverEditionKey: String? = null,
    val key: String,
    val title: String
)

interface OpenLibraryApiClient {
    suspend fun getBook(key: String): OpenLibraryBook? =
        performSearch("key:$key").docs.firstOrNull()

    suspend fun search(query: String): List<OpenLibraryBook> = performSearch(query).docs

    @GET("/search.json")
    suspend fun performSearch(@Query("q") query: String): SearchResponse
}
