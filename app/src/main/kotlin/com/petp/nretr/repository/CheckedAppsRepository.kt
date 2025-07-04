package com.petp.nretr.com.petp.nretr.repository

interface CheckedAppsRepository {
    fun loadCheckedApps(): MutableSet<String>
    fun saveCheckedApps(checkedApps: Set<String>)
}