package com.cwalcott.mylibrary.di

import android.content.Context
import androidx.room.Room
import com.cwalcott.mylibrary.database.AppDatabase
import com.cwalcott.mylibrary.database.RoomAppDatabase
import com.cwalcott.mylibrary.networking.OpenLibraryApiClient
import com.cwalcott.mylibrary.ui.favorites.FavoritesViewModel
import com.cwalcott.mylibrary.ui.searchbooks.SearchBooksViewModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class Composer(val database: AppDatabase, val openLibraryApiClient: OpenLibraryApiClient) {
    fun makeFavoritesViewModel(): FavoritesViewModel = FavoritesViewModel(database = database)

    fun makeSearchBooksViewModel(): SearchBooksViewModel =
        SearchBooksViewModel(openLibraryApiClient)
}

fun createLiveComposer(context: Context): Composer = Composer(
    database = liveRoomAppDatabase(context),
    openLibraryApiClient = liveOpenLibraryApiClient()
)

private fun liveRoomAppDatabase(context: Context): RoomAppDatabase =
    Room.databaseBuilder(context.applicationContext, RoomAppDatabase::class.java, "app.db")
        .build()

private fun liveOpenLibraryApiClient(): OpenLibraryApiClient {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BASIC
    val client = OkHttpClient.Builder().addInterceptor(logging).build()

    val retroJson = Json { ignoreUnknownKeys = true }
    val converterFactory =
        retroJson.asConverterFactory("application/json; charset=utf-8".toMediaType())

    val retrofit = Retrofit.Builder()
        .baseUrl("https://openlibrary.org/")
        .client(client)
        .addConverterFactory(converterFactory)
        .build()

    return retrofit.create(OpenLibraryApiClient::class.java)
}
