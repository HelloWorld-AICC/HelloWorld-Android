package com.hello.world

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HelloWorldApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}