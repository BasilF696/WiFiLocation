import androidx.room.Dao
import androidx.room.Insert

@Dao
interface NetworkDao {
    @Insert
    fun insert(network: Network): Long
}
