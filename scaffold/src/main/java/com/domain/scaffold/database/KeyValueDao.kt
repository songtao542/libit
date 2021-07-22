package com.domain.scaffold.database

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Dao
interface KeyValueDao {

    @Query("SELECT * FROM KeyValue WHERE `key` = :key")
    fun get(key: String): KeyValue?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun put(kv: KeyValue)

    @Query("DELETE FROM KeyValue WHERE `key` = :key")
    fun remove(key: String)

    @Query("DELETE FROM KeyValue")
    fun removeAll()
}

fun KeyValueDao.put(key: String, value: String) {
    put(KeyValue(key, value))
}

@Serializable
@Entity(tableName = "KeyValue")
@Parcelize
data class KeyValue(
    @PrimaryKey val key: String,
    val value: String? = null
) : Parcelable