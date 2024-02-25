package com.petp.nretr.module

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

const val CHECKED_APPS = "checked_apps"
const val CHECKED_APPS_PACKAGE_NAMES_KEY = "checked_app_package_names"
const val NOTIFICATIONS = "notifications"

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesModule {

    @Provides
    @Singleton
    @Named("CheckedAppsPreferences")
    fun provideCheckedAppsPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(CHECKED_APPS, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @Named("NotificationPreferences")
    fun provideNotificationPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(NOTIFICATIONS, Context.MODE_PRIVATE)
    }

}