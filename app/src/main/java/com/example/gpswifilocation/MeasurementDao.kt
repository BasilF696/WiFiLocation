import androidx.room.Dao
import androidx.room.Insert

@Dao
interface MeasurementDao {
    @Insert
    fun insert(measurement: Measurement)
}
