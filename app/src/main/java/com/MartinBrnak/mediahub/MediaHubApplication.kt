package com.MartinBrnak.mediahub

import android.app.Application

class MediaHubApplication : Application() {
    override fun onCreate(){
        super.onCreate()
        MediaRepository.initialize(this)
    }
}