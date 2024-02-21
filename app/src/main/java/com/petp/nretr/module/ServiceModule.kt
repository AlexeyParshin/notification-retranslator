package com.petp.nretr.module

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.petp.nretr.service.MainFrameService
import com.petp.nretr.service.NotificationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
object ServiceModule {

    @Provides
    fun provideMainFrameService(context: Context): MainFrameService {
        return MainFrameService(context)
    }

    @Provides
    fun provideNotificationService(context: Context): NotificationService {
        return NotificationService(context)
    }
}