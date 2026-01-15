package com.cwalcott.mylibrary.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cwalcott.mylibrary.util.inMemoryDatabase
import org.junit.After
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomBookDaoTest : BookDaoTest() {
    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    override val database by lazy { inMemoryDatabase() }

    @After fun closeDb() = database.close()
}
