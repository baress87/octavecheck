package kr.co.baress.octavecheck.utility

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [FrequencyModel::class], version = 1)
abstract class FrequencyDB : RoomDatabase() {
    abstract fun FrequencyDao(): FrequencyDao

    companion object {
        private var INSTANCE: FrequencyDB? = null

        fun getInstance(context: Context): FrequencyDB? {
            if (INSTANCE == null) {
                synchronized(FrequencyDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, FrequencyDB::class.java, "Frequency.db")
                        .fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE
        }
    }

    fun destroyInstance() {
        INSTANCE = null
    }
}