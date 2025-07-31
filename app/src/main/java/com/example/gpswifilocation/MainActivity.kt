package com.example.gpswifilocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import Coordinate
import Measurement
import Network
import AppDatabase
import NetworkDao
import CoordinateDao
import MeasurementDao
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager
    private lateinit var wifiManager: WifiManager
    private lateinit var resultTextView: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var fetchButton: Button
    private val handler = Handler(Looper.getMainLooper())
    private var isScanning = false

    private lateinit var db: AppDatabase
    private lateinit var networkDao: NetworkDao
    private lateinit var coordinateDao: CoordinateDao
    private lateinit var measurementDao: MeasurementDao

    companion object {
        private const val LOCATION_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            db = AppDatabase.getDatabase(this)
            networkDao = db.networkDao()
            coordinateDao = db.coordinateDao()
            measurementDao = db.measurementDao()
        } catch (e: Exception) {
            Log.e("DatabaseError", "Ошибка при создании БД", e)
        }

        resultTextView = findViewById(R.id.resultTextView)
        scrollView = findViewById(R.id.scrollView)
        fetchButton = findViewById(R.id.fetchButton)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        fetchButton.setOnClickListener {
            if (isScanning) {
                stopScanning()
            } else {
                startScanning()
            }
        }
    }

    private fun startScanning() {
        if (!checkPermissions()) {
            requestPermissions()
            return
        }

        isScanning = true
        fetchButton.text = "Остановить сканирование"
        updateLocationAndWifi()
    }

    private fun stopScanning() {
        isScanning = false
        fetchButton.text = "Начать сканирование"
        handler.removeCallbacksAndMessages(null)
    }

    private fun checkPermissions(): Boolean {
        val locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return locationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE
        ), LOCATION_PERMISSION_CODE)
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationAndWifi() {

        if (!isScanning) return

        // Получение GPS-локации
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude
                // Получение списка Wi-Fi сетей
                val wifiScanResults: List<ScanResult> = wifiManager.scanResults

                var coordinateId: Long = -1
                try {
                    coordinateId = coordinateDao.insert(Coordinate(latitude = latitude, longitude = longitude))
                } catch (e: Exception) {
                    Log.e("DatabaseError", "Ошибка при вставке координат", e)
                }
                for (wifi in wifiScanResults) {
                    var bssidId: Long = -1
                    try {
                        // Сохранение сети Wi-Fi (BSSID уникальный)
                        bssidId = networkDao.insert(Network(bssid = wifi.BSSID, ssid = wifi.SSID))
                    } catch (e: Exception) {
                        Log.e("DatabaseError", "Ошибка при вставке сети", e)
                    }
                    try {
                        measurementDao.insert(
                            Measurement(
                                networkID = bssidId,
                                coordinateID = coordinateId,
                                signalLevel = wifi.level
                            )
                        )
                    } catch (e: Exception) {
                        Log.e("DatabaseError", "Ошибка при вставке измерения", e)
                    }
                }

                val wifiInfo = wifiManager.connectionInfo
                val wifiList = wifiScanResults.sortedByDescending { it.level }.joinToString("\n") { "${it.SSID} [${it.BSSID}]:  ${it.level} dBm" }
                val resultText = """
                    📍 GPS-координаты:
                    Широта: $latitude
                    Долгота: $longitude

                    📡 Уровень сигнала текущей Wi-Fi сети:
                    ${wifiInfo.ssid} - ${wifiInfo.rssi} dBm

                    📶 Доступные сети Wi-Fi:
                    $wifiList
                """.trimIndent()

                resultTextView.text = resultText
                scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }, null)

        // Повторение запроса каждые 3 секунды
        handler.postDelayed({ updateLocationAndWifi() }, 3000)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScanning()
        } else {
            resultTextView.text = "❌ Разрешения не получены!"
        }
    }
}
