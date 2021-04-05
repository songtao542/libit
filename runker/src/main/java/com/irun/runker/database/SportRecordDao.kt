package com.irun.runker.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.irun.runker.model.SportRecord

@Dao
interface SportRecordDao {

    @Query("SELECT * FROM SportRecord WHERE `id` = :id")
    fun get(id: Long): SportRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(sportRecord: SportRecord)

    @Query("DELETE FROM SportRecord WHERE `id` = :id")
    fun delete(id: Long)

    @Query("DELETE FROM SportRecord")
    fun deleteAll()
}