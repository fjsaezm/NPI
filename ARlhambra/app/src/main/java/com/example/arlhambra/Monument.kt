package com.example.arlhambra

class Monument(n : String, X : Double, Y: Double) {

    var name: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var dist : Double = 10000.0

    init {

        name = n;
        latitude = X;
        longitude = Y;
    }

    fun setdist(d : Double){
        this.dist = d
    }

}