package com.example.gps

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlin.math.sqrt
import android.util.Log
import android.widget.Button;
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.R.attr.name
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.R.attr.name
import android.app.Activity
import android.database.Cursor
import android.os.Build
import android.widget.ImageView
import java.io.IOException


private val TAG: String = MainActivity::class.java.simpleName

class MainActivity : AppCompatActivity() {

    //Vals
    val PERMISSION_ID = 42
    private val GALLERY = 1
    private val SHARE   = 2
    private val RESULT_LOAD_IMG = 1;
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var bluetoothAdapter: BluetoothAdapter

    //button: to delete



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(!checkPermissionsData())
            requestPermissionsData()

        //Activate location services
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // get button. will be deleted
        val button = findViewById<Button>(R.id.galBut)
        button?.setOnClickListener {

            choosePhotoFromGallery()

        }


        //Activate bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            val REQUEST_ENABLE_BT = 1
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }


        //Get location
        getLastLocation()
    }

    private fun choosePhotoFromGallery() {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, GALLERY)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode ==  GALLERY){

            val image = data?.data
            val sharingIntent = Intent(Intent.ACTION_SEND);
            sharingIntent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET))
		    sharingIntent.setType("image/*");
		    sharingIntent.putExtra(Intent.EXTRA_STREAM, image);
		    startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));

        }

    }



    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        findViewById<TextView>(R.id.latTextView).text = location.latitude.toString()
                        findViewById<TextView>(R.id.lonTextView).text = location.longitude.toString()
                        // Find nearest monument
                        val nm = findMonuments(location.latitude,location.longitude)
                        findViewById<TextView>(R.id.minDTextView).text = nm.name
                    }

                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 100
        mLocationRequest.fastestInterval = 50
        //mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            findViewById<TextView>(R.id.latTextView).text = mLastLocation.latitude.toString()
            findViewById<TextView>(R.id.lonTextView).text = mLastLocation.longitude.toString()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun checkPermissionsData(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }
    private fun requestPermissionsData() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_ID
        )
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    private fun findMonuments(locX : Double, locY : Double) : Monument {


        //Auxiliar vars
        val monuments : MutableList<Monument> = ArrayList()
        val delimiter = "\t"

        // Read text
        try {

            val file_name = "coordenadas.txt"
            val string = application.assets.open(file_name).bufferedReader().use{
                it.readText()
            }
            val splitted = string.split("\n")
            for(line in splitted){
                if(line.isNotEmpty()) {
                    val sp = line.split(delimiter)
                    monuments.add(Monument(sp[0], sp[1].toDouble(), sp[1].toDouble()))
                }
            }

            val near : MutableList<Monument> = ArrayList()
            for (item in monuments){
                val dist = sqrt((locX - item.x)*(locX-item.x) + (locY - item.y)*(locY - item.y))
                if (dist < 10000)
                    near.add(item)
            }
            Log.d(TAG,file_name)
            return near[0]

        } catch (e:Exception){
            Log.d(TAG, e.toString())
        }

        return Monument("No monument",0.0,0.0)
    }



}