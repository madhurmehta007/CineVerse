package org.android.cineverse

import android.app.Application
import org.android.cineverse.di.appModule
import org.android.cineverse.shared.di.initKoin
import org.koin.android.ext.koin.androidContext

class CineVerseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@CineVerseApplication)
            modules(appModule)
        }
    }
}
