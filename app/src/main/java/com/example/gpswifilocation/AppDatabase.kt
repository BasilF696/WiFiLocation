import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Network::class, Coordinate::class, Measurement::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun networkDao(): NetworkDao
    abstract fun coordinateDao(): CoordinateDao
    abstract fun measurementDao(): MeasurementDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app-database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
