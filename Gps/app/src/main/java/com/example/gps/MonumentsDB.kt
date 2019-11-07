package com.example.gps


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MonumentsDB(context : Context) {

    private val dbName = "MonumentsDB"
    private val dbVersion = 1
    private val TABLE_NAME = "monuments"
    private val id = "Id"
    // Monument values in table
    private val mon = "Monument"
    private val x = "X";
    private val y = "Y";

    private var db: SQLiteDatabase? = null

    fun onCreate(db: SQLiteDatabase?) {
        // Create the table
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME " +
                "($mon Monument PRIMARY KEY, $x X, $y Y)"

        db?.execSQL(CREATE_TABLE)
        // Vars for the monuments
        val m1 = "Palacio de Carlos V, 37.176996, -3.589986"
        val m2 = "Torre de la vela,37.177277, -3.592177"
        // Insert monuments
        db?.execSQL("INSERT INTO $TABLE_NAME " + "(mon,X,Y) " + " VALUES (" + m1 +");")
        db?.execSQL("INSERT INTO $TABLE_NAME " + "(mon,X,Y) " + " VALUES (" + m2 +");")
    }

    fun queryAll(): Cursor {

        return db!!.rawQuery("select * from $TABLE_NAME", null)
    }
}