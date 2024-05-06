package com.MartinBrnak.mediahub

import android.content.Context
import androidx.room.Room
import com.MartinBrnak.mediahub.database.MediaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

private const val DATABASE_NAME = "media-database"

class MediaRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
) {

    private val database: MediaDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            MediaDatabase::class.java,
            DATABASE_NAME
        ).build()

    fun getMediaAll(): Flow<List<Media>> = database.mediaDao().getMediaAll()

    suspend fun getMedia(id: UUID): Media = database.mediaDao().getMedia(id)

    fun updateMedia(media: Media){
        coroutineScope.launch {
            database.mediaDao().updateMedia(media)
        }
    }

    suspend fun addMedia(media: Media){
        database.mediaDao().addMedia(media)
    }

    companion object{
        private var INSTANCE: MediaRepository? = null

        fun initialize(context: Context){
            if (INSTANCE == null){
                INSTANCE = MediaRepository(context)
            }
        }

        fun get(): MediaRepository{
            return INSTANCE
                ?: throw IllegalStateException("MediaRepository must be initialized")
        }
    }
}