package com.scaffold.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        KeyValue::class,
    ],
    exportSchema = false,
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun keyValueDao(): KeyValueDao


    // adb shell setprop log.tag.SQLiteStatements VERBOSE
    // adb shell getprop log.tag.SQLiteStatements
    // adb shell stop
    // adb shell start
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