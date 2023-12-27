package com.savent.restaurant

import android.app.Application
import coil.Coil
import coil.ImageLoader
import com.mazenrashed.printooth.Printooth

import org.koin.android.BuildConfig
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@MyApplication)
            modules(appModule)
        }
        val imageLoader by inject<ImageLoader>()
        Coil.setImageLoader(imageLoader)
        Printooth.init(this)
    }


}