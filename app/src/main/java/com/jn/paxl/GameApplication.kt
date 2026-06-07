package com.jn.paxl

import android.app.Application
import com.jn.paxl.di.AppContainer
import com.jn.paxl.di.DefaultAppContainer

class GameApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
