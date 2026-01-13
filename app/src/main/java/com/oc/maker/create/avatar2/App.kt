package com.oc.maker.create.avatar2

import android.app.Application
import com.oc.maker.create.avatar2.utils.DataHelper
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Module
@InstallIn(SingletonComponent::class)
@HiltAndroidApp
class App : Application()  {
    override fun onCreate() {
        super.onCreate()

    }
}