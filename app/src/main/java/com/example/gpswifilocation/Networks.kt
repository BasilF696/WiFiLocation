import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "Networks", indices = [Index(value = ["bssid"], unique = true)])
data class Network(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "bssid") val bssid: String,  // Идентификатор сети (MAC-address)
    @ColumnInfo(name = "ssid") val ssid: String  // Имя Wi-Fi сети
)
