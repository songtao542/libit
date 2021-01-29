package com.irun.runker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.irun.runker.model.SportRecord

@Database(entities = [KeyValue::class, SportRecord::class], exportSchema = false, version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun keyValueDao(): KeyValueDao
    abstract fun sportRecordDao(): SportRecordDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "app.db"
                ).build().also { INSTANCE = it }
            }
    }
}