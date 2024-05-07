package com.MartinBrnak.mediahub

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Media(
    @PrimaryKey val id: UUID,
    var title: String,
    var description: String,
    val format: String,
    val url: String
)
