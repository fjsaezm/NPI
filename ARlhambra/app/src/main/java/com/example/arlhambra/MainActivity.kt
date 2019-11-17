package com.example.arlhambra

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.*
import android.webkit.URLUtil
import android.widget.TextView
import androidx.core.app.ActivityCompat
import java.io.IOException
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MotionEventCompat
import kotlinx.android.synthetic.main.activity_compass.*
import kotlin.math.PI
import kotlin.math.sqrt

private val TAG: String = MainActivity::class.java.simpleName

class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener, LocationListener, SensorEventListener {

    private val MY_PERMISSIONS_REQUEST_CAMERA = 1
    private var token = ""
    private var tokenanterior = ""
    private var gestureDetector: GestureDetector? = null
    private var dosDedos: Boolean = false
    private var QRdetectado : Boolean = false
    private var mActivePointerId: Int = 0
    private var xPosIni: Float? = null
    private var yPosIni: Float? = null

    private var text_view_distance: TextView? = null
    private var text_view_location: TextView? = null
    private var text_view_degree: TextView? = null
    private var compass_image: ImageView? = null
    private var aguja_image: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set full screen
        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        setContentView(R.layout.activity_main)

        //Hide top bar
        supportActionBar!!.hide()

        //Camera and vibrator on QR detection
        var cameraView = findViewById<View>(R.id.camera_view) as SurfaceView
        var vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        initQR(cameraView,vibrator)


        //Gesture detection
        this.gestureDetector = GestureDetector(this, this)
        gestureDetector!!.setOnDoubleTapListener(this)


        //Brújula (usando GPS)


        text_view_distance = findViewById(R.id.text_view_distance)
        text_view_location = findViewById(R.id.text_view_location)
        text_view_degree = findViewById(R.id.text_view_degree)
        compass_image = findViewById(R.id.compass_image)
        aguja_image = findViewById(R.id.aguja_image)

        setLocation()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        startCompass()
    }

    fun initQR(cameraView: SurfaceView,vibrator: Vibrator) {

        // creo el detector qr
        val barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        // creo la camara
        var cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(2560, 1440)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()

        // listener de ciclo de vida de la camara
        cameraView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {

                // verifico si el usuario dio los permisos para la camara
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        android.Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // verificamos la version de ANdroid que sea al menos la M para mostrar
                        // el dialog de la solicitud de la camara
                        if (shouldShowRequestPermissionRationale(
                                android.Manifest.permission.CAMERA
                            )
                        )
                        ;
                        requestPermissions(
                            arrayOf(android.Manifest.permission.CAMERA),
                            MY_PERMISSIONS_REQUEST_CAMERA
                        )
                    }
                    return
                } else {
                    try {
                        cameraSource!!.start(cameraView.holder)
                    } catch (ie: IOException) {
                        Log.e("CAMERA SOURCE", ie.message)
                    }

                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })

        // preparo el detector de QR
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}


            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.getDetectedItems()

                if (barcodes.size() > 0) {

                    // obtenemos el token
                    token = barcodes.valueAt(0).displayValue.toString()

                    // verificamos que el token anterior no se igual al actual
                    // esto es util para evitar multiples llamadas empleando el mismo token
                    if (token != tokenanterior && !QRdetectado) {

                        // guardamos el ultimo token procesado
                        tokenanterior = token
                        Log.i("token", token)

                        if (URLUtil.isValidUrl(token)) {
                            // si es una URL valida abre el navegador
                            QRdetectado = true
                            vibrator.vibrate(200)
                        }
                    }
                }
            }
        })

    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.gestureDetector!!.onTouchEvent(event)
        val action: Int = MotionEventCompat.getActionMasked(event)
        mActivePointerId = event.getPointerId(0)


        if(event.pointerCount == 1) {
            val (xPos: Float, yPos: Float) = event.findPointerIndex(mActivePointerId).let { pointerIndex ->
                event.getX(pointerIndex) to event.getY(pointerIndex)
            }
            if (dosDedos && QRdetectado) {
                dosDedos = false
                if (xPos > this!!.xPosIni!!) {
                    findViewById<TextView>(R.id.textView)!!.text = "->"
                    QRdetectado = false
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(token))
                    startActivity(browserIntent)

                    Thread(object : Runnable {
                        override fun run() {
                            try {
                                synchronized(this) {
                                    Thread.sleep(5_000)
                                    // limpiamos el token
                                    tokenanterior = ""
                                }
                            } catch (e: InterruptedException) {
                                // TODO Auto-generated catch block
                                Log.e("Error", "Waiting didnt work!!")
                                e.printStackTrace()
                            }

                        }
                    }).start()
                }else {
                    QRdetectado = false
                    findViewById<TextView>(R.id.textView)!!.text = "<-"
                }
            }else {
                xPosIni = xPos
                yPosIni = yPos
            }
        }

        if (event.pointerCount == 2){
            findViewById<TextView>(R.id.textView)!!.text = ""
            dosDedos = true
            mActivePointerId = event.getPointerId(1)
            val (xPos2: Float, yPos2: Float) = event.findPointerIndex(mActivePointerId).let { pointerIndex ->
                event.getX(pointerIndex) to event.getY(pointerIndex)
            }
        }
        return super.onTouchEvent(event)
    }


    override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {
        val intent =  Intent(this, Menu::class.java)
        startActivity(intent)
        return true
    }

    override fun onDoubleTap(motionEvent: MotionEvent): Boolean {
        if (text_view_distance!!.getVisibility() == View.VISIBLE){
            text_view_distance!!.setVisibility(View.GONE)
            compass_image!!.setVisibility(View.GONE)
            aguja_image!!.setVisibility(View.GONE)
        }else{
            text_view_distance!!.setVisibility(View.VISIBLE)
            compass_image!!.setVisibility(View.VISIBLE)
            aguja_image!!.setVisibility(View.VISIBLE)
        }
        return false
    }

    override fun onDoubleTapEvent(motionEvent: MotionEvent): Boolean {
        findViewById<TextView>(R.id.textView)!!.text = "onDoubleTapEvent"
        return false
    }

    override fun onDown(motionEvent: MotionEvent): Boolean {
        findViewById<TextView>(R.id.textView)!!.text = "onDown"
        return false
    }

    override fun onShowPress(motionEvent: MotionEvent) {
        findViewById<TextView>(R.id.textView)!!.text = "onShowPress"

    }

    override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
        findViewById<TextView>(R.id.textView)!!.text = "onSingleTapUp"
        return false
    }

    override fun onScroll(
        motionEvent: MotionEvent,
        motionEvent1: MotionEvent,
        v: Float,
        v1: Float
    ): Boolean {
        findViewById<TextView>(R.id.textView)!!.text = "onScroll"
        println("onScroll")
        return false
    }

    override fun onLongPress(motionEvent: MotionEvent) {
        findViewById<TextView>(R.id.textView)!!.text = "onLongPress"

    }

    override fun onFling(
        motionEvent: MotionEvent,
        motionEvent1: MotionEvent,
        v: Float,
        v1: Float
    ): Boolean {
        findViewById<TextView>(R.id.textView)!!.text = "onFling"
        return false
    }







    //***************************************
    // Brújula y el GPS
    //***************************************





    val REQUEST_LOCATION = 2

    private var sensorManager: SensorManager? = null

    private var northLatitude = 84.03
    private var northLongitude =  -5.49
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private var targetLatitude =  85.03
    private var targetLongitude = -5.49
    //Facultad de Ciencias
    //private var targetLatitude = 37.179740
    //private var targetLongitude = -3.609679
    //Facultad de Bellas Artes
    //private var targetLatitude = 37.195484
    //private var targetLongitude = -3.626593
    //Mercadona
    //private var targetLatitude = 37.196587
    //private var targetLongitude = -3.6222805
    //CEIP San Juan de Dios
    //private var targetLatitude = 37.201550
    //private var targetLongitude = -3.625004

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

    private fun distance(latitude: Double, longitude: Double): Double {
        val R = 6371000
        val phi1 = currentLatitude* PI /180.0
        val phi2 = latitude* PI /180.0
        val phi = phi2 - phi1
        val lambda = (longitude - currentLongitude)* PI /180.0

        val a = Math.sin(phi/2.0)*Math.sin(phi/2.0)+Math.cos(phi1)*
                Math.cos(phi2)*Math.sin(lambda/2)*Math.sin(lambda/2)
        val c = 2*Math.atan2(Math.sqrt(a),Math.sqrt(1.0-a))
        val distance = R*c

        return distance
    }

    private fun setDistance(){

        //Calculamos la distancia usando la fórmula 'harsevine'

        text_view_distance?.setText("Distancia: " + Math.round(distance(targetLatitude,targetLongitude)).toString() + " m")
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
                val m = findMonuments(currentLatitude,currentLongitude)
                if (m.dist < 20000){
                    findViewById<TextView>(R.id.textView)!!.text = "Tienes el monumento: " + m.name + " cerca de ti"
                    targetLatitude = m.latitude
                    targetLongitude = m.longitude
                    setAngle()
                }
                setAngle()
                setDistance()
            }
            else{
                Toast.makeText(this,"Localización no disponible", Toast.LENGTH_SHORT).show()
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
        val finalAngle = (Math.toDegrees(angle).toInt()+azimuth)%360
        compass_image?.rotation = (-azimuth).toFloat()
        aguja_image?.rotation = (-finalAngle).toFloat()
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

                haveSensorAccelerometer = sensorManager!!.registerListener(this,accelerometer,
                    SensorManager.SENSOR_DELAY_UI)
                haveSensorMagenotemeter = sensorManager!!.registerListener(this,magnetometer,
                    SensorManager.SENSOR_DELAY_UI)
            }
        }else{
            rotationVector = sensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            haveSensorRotationVector = sensorManager!!.registerListener(this,rotationVector,
                SensorManager.SENSOR_DELAY_UI)
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

    private fun findMonuments(locX : Double, locY : Double) : Monument {


        //Auxiliar vars
        val monuments : MutableList<Monument> = ArrayList()
        val delimiter = "\t"

        // Read text
        try{

            val file_name = "coordenadas.txt"
            val string = application.assets.open(file_name).bufferedReader().use{
                it.readText()
            }
            val splitted = string.split("\n")
            for(line in splitted){
                if(line.isNotEmpty()) {
                    val sp = line.split(delimiter)
                    monuments.add(Monument(sp[0], sp[1].toDouble(), sp[2].toDouble()))
                }
            }

            val near : MutableList<Monument> = ArrayList()


            for (item in monuments){
                val dist = distance(item.latitude, item.longitude)
                item.setdist(dist)
                if (dist < 500)
                    near.add(item)
            }

            Log.d(TAG,file_name)
            val sorted : List<Monument> = near.sortedBy { Monument-> Monument.dist }
            return sorted[0]

        } catch (e:Exception){
            Log.d(TAG, e.toString())
        }

        return Monument("No monument",0.0,0.0)
    }

}
