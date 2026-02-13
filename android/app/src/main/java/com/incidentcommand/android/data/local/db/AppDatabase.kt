package com.incidentcommand.android.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.incidentcommand.android.data.local.dao.IncidentDao
import com.incidentcommand.android.data.local.entity.IncidentEntity

@Database(entities = [IncidentEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun incidentDao(): IncidentDao
}
