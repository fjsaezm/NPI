package com.example.simplecompass

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), LocationListener, SensorEventListener{

    val REQUEST_LOCATION = 2

    private var sensorManager: SensorManager? = null

    private var northLatitude = 84.03
    //private var northLongitude =  -174.51
    private var northLongitude =  -5.49
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    //Facultad de Ciencias
    //private var targetLatitude = 37.179740
    //private var targetLongitude = -3.609679
    //Facultad de Bellas Artes
    private var targetLatitude = 37.195484
    private var targetLongitude = -3.626593
    //Mercadona
    //private var targetLatitude = 37.196587
    //private var targetLongitude = -3.6222805
    //CEIP San Juan de Dios
    //private var targetLatitude = 37.201550
    //private var targetLongitude = -3.625004


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setLocation()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        startCompass()
    }

    private var angle = 0.0

    private fun setAngle(){
        //La variable u  es el vector PosicionActual_PosicionObjetivo
        var u = DoubleArray(2)
        //La variable v  es el vector PosicionActual_PosicionNorte
        var v = DoubleArray(2)
        //La variable uv es el ángulo que forman u_v
        var uv = 0.0
        u[0] = targetLongitude-currentLongitude
        u[1] = targetLatitude-currentLatitude
        v[0] = northLongitude-currentLongitude
        v[1] = northLatitude-currentLatitude

        uv = Math.acos((u[0]*v[0]+u[1]*v[1])/(Math.sqrt(u[0]*u[0]+u[1]*u[1])*Math.sqrt(v[0]*v[0]+v[1]*v[1])))

        //La variable n es el giro de 90º antihorario de la variable v
        var n = DoubleArray(2)
        n[0] = -v[1]
        n[1] = v[0]

        if(((u[0]*n[0]+u[1]*n[1])<0)){
            uv = -uv
        }

        angle = uv
    }

    private fun setLocation() {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION)
        }
        else{

            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            val provider = locationManager.getBestProvider(criteria,false)
            val location = locationManager.getLastKnownLocation(provider)

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this)
            if(location != null){
                currentLatitude=location.latitude
                currentLongitude=location.longitude
                setAngle()
                text_view_location.setText(convertLocationToString(location.latitude,location.longitude))
            }
            else{
                Toast.makeText(this,"Localización no disponible",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == REQUEST_LOCATION) setLocation()
    }

    private fun convertLocationToString2(latitude: Double, longitude: Double): String {
        val builder = StringBuilder()
        if (latitude < 0)
            builder.append("S ") else builder.append("N ")
        builder.append(latitude.toString())
        builder.append("\n")
        builder.append(longitude.toString())
        builder.append("\"")

        return builder.toString()
    }

    private fun convertLocationToString(latitude: Double, longitude: Double): String {
        val builder = StringBuilder()
        if (latitude < 0)
            builder.append("S ") else builder.append("N ")

        val latitudeDegrees = Location.convert(Math.abs(latitude),Location.FORMAT_SECONDS)
        val latitudeSplit = latitudeDegrees.split((":").toRegex()).dropLastWhile ({ it.isEmpty() }).toTypedArray()
        builder.append(latitudeSplit[0])
        builder.append("º")
        builder.append(latitudeSplit[1])
        builder.append("'")
        builder.append(latitudeSplit[2])
        builder.append("\"")
        builder.append("\n")

        if(longitude < 0) builder.append("W ") else builder.append("E ")
        val longitudeDegrees = Location.convert(Math.abs(longitude),Location.FORMAT_SECONDS)
        val longitudeSplit = longitudeDegrees.split((":").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        builder.append(longitudeSplit[0])
        builder.append("º")
        builder.append(longitudeSplit[1])
        builder.append("'")
        builder.append(longitudeSplit[2])
        builder.append("\"")

        return builder.toString()
    }


    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

    override fun onLocationChanged(location: Location?) {
        setLocation()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private var rotationMatrix = FloatArray(9)
    private var orientation = FloatArray(3)
    private var azimuth: Int = 0
    private var lastAccelerometer = FloatArray(3)
    private var lastAccelerometerSet = false
    private var lastMagnetometer = FloatArray(3)
    private var lastMagnetometerSet = false

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type  == Sensor.TYPE_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(rotationMatrix,event.values)
            azimuth = (Math.toDegrees(SensorManager.getOrientation(rotationMatrix, orientation)[0].toDouble())+360).toInt()%360
        }

        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            System.arraycopy(event.values,0,lastAccelerometer,0,event.values.size)
            lastAccelerometerSet = true
        }else if(event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD){
            System.arraycopy(event.values,0, lastMagnetometer,0,event.values.size)
            lastMagnetometerSet = true
        }

        if(lastAccelerometerSet && lastMagnetometerSet){
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)
            SensorManager.getOrientation(rotationMatrix,orientation)
            azimuth = (Math.toDegrees(SensorManager.getOrientation(rotationMatrix,orientation)[0].toDouble())+360).toInt()%360
        }

        azimuth = Math.round(azimuth.toFloat())
        var finalAngle = (Math.toDegrees(angle).toInt()+azimuth)%360
        compass_image.rotation = (-azimuth).toFloat()
        aguja_image.rotation = (-finalAngle).toFloat()

        val where = when(azimuth){
            in 281..349 -> "NW"
            in 261..280 -> "W"
            in 191..260 -> "SW"
            in 171..190 -> "S"
            in 101..170 -> "SE"
            in 81..100 -> "E"
            in 11..80 -> "NE"
            else -> "N"
        }

        text_view_degree.text = "$azimuth º $where"
    }

    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private var haveSensorAccelerometer = false
    private var haveSensorMagenotemeter = false

    private var rotationVector: Sensor? = null
    private var haveSensorRotationVector = false

    private fun startCompass(){
        if(sensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null){
            if(sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null
                || sensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null){
                noSensorAlert()
            }else{
                accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                magnetometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

                haveSensorAccelerometer = sensorManager!!.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_UI)
                haveSensorMagenotemeter = sensorManager!!.registerListener(this,magnetometer,SensorManager.SENSOR_DELAY_UI)
            }
        }else{
            rotationVector = sensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            haveSensorRotationVector = sensorManager!!.registerListener(this,rotationVector,SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun stopCompass(){
        if(haveSensorRotationVector) sensorManager!!.unregisterListener(this,rotationVector)
        if(haveSensorAccelerometer) sensorManager!!.unregisterListener(this,accelerometer)
        if(haveSensorMagenotemeter) sensorManager!!.unregisterListener(this,magnetometer)
    }

    override fun onResume(){
        super.onResume()
        startCompass()
    }

    override fun onPause(){
        super.onPause()
        stopCompass()
    }

    private fun noSensorAlert(){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setMessage("Tu dispositivo no soporta una brújula")
            .setCancelable(false)
            .setNegativeButton("Cerrar"){_,_ -> finish()}
        alertDialog.show()
    }

}
