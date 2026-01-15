package com.cwalcott.mylibrary.database

class FakeBookDaoTest : BookDaoTest() {
    override val database: AppDatabase by lazy { FakeInMemoryAppDatabase() }
}
