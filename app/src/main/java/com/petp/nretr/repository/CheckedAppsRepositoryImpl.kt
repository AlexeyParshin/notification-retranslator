package com.petp.nretr.com.petp.nretr.repository

import android.content.SharedPreferences
import com.petp.nretr.module.CHECKED_APPS_PACKAGE_NAMES_KEY
import javax.inject.Inject
import javax.inject.Named

class CheckedAppsRepositoryImpl @Inject constructor(
    @Named("CheckedAppsPreferences") private val checkedAppsPreferences: SharedPreferences
) : CheckedAppsRepository {
    override fun loadCheckedApps(): MutableSet<String> {
        return checkedAppsPreferences.getStringSet(CHECKED_APPS_PACKAGE_NAMES_KEY, mutableSetOf())?.toMutableSet()
            ?: mutableSetOf()
    }

    override fun saveCheckedApps(checkedApps: Set<String>) {
        checkedAppsPreferences.edit().putStringSet(CHECKED_APPS_PACKAGE_NAMES_KEY, checkedApps.toSet()).apply()
    }
}