package com.example.ralhambra

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.*
import android.webkit.URLUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_camara.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener,
GestureDetector.OnDoubleTapListener{


    private lateinit var appBarConfiguration: AppBarConfiguration

    // Camera vars
    private val MY_PERMISSIONS_REQUEST_CAMERA = 1
    private var token = ""
    private var tokenanterior = ""

    //Gesture detector
    private var gestureDetector: GestureDetector? = null

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        println("LETSGO\n\n\n")
        //camera things
        var cameraView = findViewById<View>(R.id.camera_view) as SurfaceView
        var vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        initQR(cameraView,vibrator)




        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery,
                R.id.nav_tools, R.id.nav_share
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        //gesture detector
        this.gestureDetector = GestureDetector(this, this)
        gestureDetector!!.setOnDoubleTapListener(this)


    }

    //Function for getting touch events
    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.gestureDetector!!.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    // used for picture
    override fun onDoubleTap(motionEvent: MotionEvent): Boolean {
       // mTextView!!.text = "onDoubleTap"
        //test_view.text = "onsingletap"
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.openDrawer(0)
        return false
    }

    //used for menu
    override fun onLongPress(motionEvent: MotionEvent) {
        //mTextView!!.text = "onLongPress"

    }

    //required
    override fun onShowPress(motionEvent: MotionEvent) {
        //mTextView!!.text = "onShowPress"

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun initQR(cameraView: SurfaceView,vibrator: Vibrator) {

        // creo el detector qr
        val barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        // creo la camara
        var cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1600, 1024)
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
                    if (token != tokenanterior) {

                        // guardamos el ultimo token procesado
                        tokenanterior = token
                        Log.i("token", token)

                        if (URLUtil.isValidUrl(token)) {
                            // si es una URL valida abre el navegador
                            vibrator.vibrate(200)
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(token))
                            startActivity(browserIntent)
                        }

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

                    }
                }
            }
        })

    }
    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDown(p0: MotionEvent?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDoubleTapEvent(p0: MotionEvent?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSingleTapConfirmed(p0: MotionEvent?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
