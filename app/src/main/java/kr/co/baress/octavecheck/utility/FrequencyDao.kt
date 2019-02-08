package kr.co.baress.octavecheck.utility

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface FrequencyDao {
    @Query("SELECT COUNT(id) FROM Frequency")
    fun getCount(): Int

    @Query("SELECT * FROM Frequency")
    fun getAll(): List<FrequencyModel>

    @Query("SELECT * FROM Frequency WHERE frequency >= :frequency LIMIT 1")
    fun getScale(frequency: Double): List<FrequencyModel>

    @Insert(onConflict = REPLACE)
    fun insert(frequency: FrequencyModel)

    @Query("DELETE FROM Frequency")
    fun deleteAll()
}