package com.incidentcommand.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.incidentcommand.android.data.local.entity.IncidentEntity

@Dao
interface IncidentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(incidents: List<IncidentEntity>)

    @Query("SELECT * FROM incidents ORDER BY updatedAt DESC")
    suspend fun getAll(): List<IncidentEntity>

    @Query("SELECT * FROM incidents WHERE id = :id")
    suspend fun getById(id: Long): IncidentEntity?

    @Query("DELETE FROM incidents")
    suspend fun deleteAll()
}
