package com.MartinBrnak.mediahub.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.MartinBrnak.mediahub.Media


@Database(entities = [Media::class], version = 1)
abstract class MediaDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
}