import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(
    tableName = "Measurement",
    foreignKeys = [
        ForeignKey(entity = Network::class, parentColumns = ["bssid"], childColumns = ["networkBSSID"]),
        ForeignKey(entity = Coordinate::class, parentColumns = ["id"], childColumns = ["coordinateID"])
    ]
)
data class Measurement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "networkBSSID") val networkBSSID: String,  // Связь с Networks
    @ColumnInfo(name = "coordinateID") val coordinateID: Int,  // Связь с Coordinates
    @ColumnInfo(name = "signalLevel") val signalLevel: Int  // Уровень сигнала
)
