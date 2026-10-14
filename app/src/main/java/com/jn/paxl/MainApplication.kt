package com.jn.paxl

import android.app.Application
import local.paxl.ui.di.AppContainer
import local.paxl.ui.di.AppContainerHost
import local.paxl.ui.di.DefaultAppContainer

class MainApplication : Application(), AppContainerHost {
    override lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this, BuildConfig.DEBUG)
    }
}
