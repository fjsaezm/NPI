package com.example.gps

class Monument(n : String, X : Double, Y: Double) {

    var name: String = ""
    var x: Double = 0.0
    var y: Double = 0.0
    var dist : Double = 10000.0

    init {

        name = n;
        x = X;
        y = Y;
    }

    fun setdist(d : Double){
        this.dist = d
    }

}