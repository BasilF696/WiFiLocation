import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Networks")
data class Network(
    @PrimaryKey val bssid: String,  // Уникальный идентификатор сети
    val ssid: String  // Имя Wi-Fi сети
)
