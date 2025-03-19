import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Measurement",
    foreignKeys = [
        ForeignKey(entity = Network::class, parentColumns = ["bssid"], childColumns = ["networkBSSID"]),
        ForeignKey(entity = Coordinate::class, parentColumns = ["id"], childColumns = ["coordinateID"])
    ]
)
data class Measurement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val networkBSSID: String,  // Связь с Networks
    val coordinateID: Int,  // Связь с Coordinates
    val signalLevel: Int  // Уровень сигнала
)
