package com.MartinBrnak.mediahub.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.MartinBrnak.mediahub.Media
import kotlinx.coroutines.flow.Flow
import java.util.UUID


@Dao
interface MediaDao {
    @Query("SELECT * from media")
    fun getMediaAll(): Flow<List<Media>>

    @Query("SELECT * FROM media WHERE id=(:id)")
    suspend fun getMedia(id: UUID): Media

    @Update
    suspend fun updateMedia(media: Media)

    @Insert
    suspend fun addMedia(media: Media)
}