import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(
    tableName = "Measurement",
    foreignKeys = [
        ForeignKey(entity = Network::class, parentColumns = ["id"], childColumns = ["networkID"]),
        ForeignKey(entity = Coordinate::class, parentColumns = ["id"], childColumns = ["coordinateID"])
    ]
)
data class Measurement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "networkID") val networkID: Long,  // Связь с Networks
    @ColumnInfo(name = "coordinateID") val coordinateID: Long,  // Связь с Coordinates
    @ColumnInfo(name = "signalLevel") val signalLevel: Int  // Уровень сигнала
)
