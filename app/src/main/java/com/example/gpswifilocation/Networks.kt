import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "Networks")
data class Network(
    @PrimaryKey val bssid: String,  // Уникальный идентификатор сети
    @ColumnInfo(name = "ssid") val ssid: String  // Имя Wi-Fi сети
)
