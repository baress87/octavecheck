package kr.co.baress.octavecheck.utility

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Frequency")
data class FrequencyModel(@PrimaryKey var id: String,
                          @ColumnInfo(name = "scale") var scale: String,
                          @ColumnInfo(name = "frequency") var frequency: Double)