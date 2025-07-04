package com.petp.nretr.com.petp.nretr.module

import com.petp.nretr.com.petp.nretr.repository.CheckedAppsRepository
import com.petp.nretr.com.petp.nretr.repository.CheckedAppsRepositoryImpl
import com.petp.nretr.com.petp.nretr.repository.NotificationRepository
import com.petp.nretr.com.petp.nretr.repository.NotificationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BindModule {

    @Binds
    abstract fun bindCheckedAppsRepository(
        checkedAppsRepositoryImpl: CheckedAppsRepositoryImpl
    ): CheckedAppsRepository

    @Binds
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
}