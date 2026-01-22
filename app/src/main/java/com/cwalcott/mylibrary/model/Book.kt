package com.cwalcott.mylibrary.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "books")
data class Book(
    val authorNames: String? = null,
    val coverEditionKey: String? = null,
    val openLibraryKey: String,
    val title: String,
    @PrimaryKey val uuid: String = UUID.randomUUID().toString().uppercase()
) {
    @Ignore var coverImageUrl: String? = if (coverEditionKey != null) {
        "https://covers.openlibrary.org/b/olid/$coverEditionKey-M.jpg"
    } else {
        null
    }
}
