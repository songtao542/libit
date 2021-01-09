package com.liabit.citypicker

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import java.io.*
import java.util.*

class CityProvider(private val context: Context) {

    companion object {
        const val LATEST_DB_NAME = "china_cities_v2.db"
        const val TABLE_NAME = "cities"
        const val COLUMN_C_NAME = "c_name"
        const val COLUMN_C_PROVINCE = "c_province"
        const val COLUMN_C_PINYIN = "c_pinyin"
        const val COLUMN_C_CODE = "c_code"
        private const val BUFFER_SIZE = 1024
    }

    private val mDbPath: String = (File.separator + "data"
            + Environment.getDataDirectory().absolutePath + File.separator
            + context.packageName + File.separator + "databases" + File.separator)

    init {
        val dir = File(mDbPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val dbFile = File(mDbPath + LATEST_DB_NAME)
        if (!dbFile.exists()) {
            val inputStream: InputStream
            val os: OutputStream
            try {
                inputStream = context.resources.assets.open(LATEST_DB_NAME)
                os = FileOutputStream(dbFile)
                val buffer = ByteArray(BUFFER_SIZE)
                var length: Int
                while (inputStream.read(buffer, 0, buffer.size).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
                os.close()
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    val allCities: List<City>
        get() {
            val db = SQLiteDatabase.openOrCreateDatabase(mDbPath + LATEST_DB_NAME, null)
            val cursor = db.rawQuery("select * from $TABLE_NAME", null)
            val result: MutableList<City> = ArrayList()
            var city: City
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_C_NAME))
                val province = cursor.getString(cursor.getColumnIndex(COLUMN_C_PROVINCE))
                val pinyin = cursor.getString(cursor.getColumnIndex(COLUMN_C_PINYIN))
                val code = cursor.getString(cursor.getColumnIndex(COLUMN_C_CODE))
                city = City(name, province, code, pinyin)
                result.add(city)
            }
            cursor.close()
            db.close()
            Collections.sort(result, CityComparator())
            return result
        }

    fun searchCity(keyword: String): List<City> {
        val sql = ("select * from " + TABLE_NAME + " where "
                + COLUMN_C_NAME + " like ? " + "or "
                + COLUMN_C_PINYIN + " like ? ")
        val db = SQLiteDatabase.openOrCreateDatabase(mDbPath + LATEST_DB_NAME, null)
        val cursor = db.rawQuery(sql, arrayOf("%$keyword%", "$keyword%"))
        val result: MutableList<City> = ArrayList()
        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndex(COLUMN_C_NAME))
            val province = cursor.getString(cursor.getColumnIndex(COLUMN_C_PROVINCE))
            val pinyin = cursor.getString(cursor.getColumnIndex(COLUMN_C_PINYIN))
            val code = cursor.getString(cursor.getColumnIndex(COLUMN_C_CODE))
            val city = City(name, province, pinyin, code)
            result.add(city)
        }
        cursor.close()
        db.close()
        val comparator = CityComparator()
        Collections.sort(result, comparator)
        return result
    }

    /**
     * sort by a-z
     */
    private class CityComparator : Comparator<City> {
        override fun compare(lhs: City, rhs: City): Int {
            val a = lhs.getItemPinyin().substring(0, 1)
            val b = rhs.getItemPinyin().substring(0, 1)
            return a.compareTo(b)
        }
    }
}