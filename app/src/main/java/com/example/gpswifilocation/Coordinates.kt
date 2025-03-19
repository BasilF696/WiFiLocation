import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Coordinates")
data class Coordinate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val latitude: Double,
    val longitude: Double
)
