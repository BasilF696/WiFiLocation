import androidx.room.Dao
import androidx.room.Insert

@Dao
interface CoordinateDao {
    @Insert
    fun insert(coordinate: Coordinate): Long  // Возвращает ID новой координаты
}
