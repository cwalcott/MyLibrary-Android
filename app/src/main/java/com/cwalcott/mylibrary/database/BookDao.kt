package com.cwalcott.mylibrary.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cwalcott.mylibrary.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("DELETE FROM books WHERE openLibraryKey = :openLibraryKey")
    suspend fun deleteByOpenLibraryKey(openLibraryKey: String)

    @Query("SELECT * FROM books WHERE openLibraryKey = :openLibraryKey")
    suspend fun findByOpenLibraryKey(openLibraryKey: String): Book?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book)

    @Query("SELECT * FROM books ORDER BY title ASC")
    fun streamAll(): Flow<List<Book>>
}
