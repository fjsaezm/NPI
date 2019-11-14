package com.example.multitouch

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.TextView
import androidx.core.view.MotionEventCompat

class MainActivity : Activity() {

    private val DEBUG_TAG: String = "MainActivity"
    private var mTextView: TextView? = null
    private var dosDedos: Boolean = false
    private var mActivePointerId: Int = 0
    private var xPosIni: Float? = null
    private var yPosIni: Float? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTextView = findViewById(R.id.myTextView) as TextView
    }

    // This example shows an Activity, but you would use the same approach if
    // you were subclassing a View.
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val action: Int = MotionEventCompat.getActionMasked(event)
        mActivePointerId = event.getPointerId(0)


        if(event.pointerCount == 1) {
            val (xPos: Float, yPos: Float) = event.findPointerIndex(mActivePointerId).let { pointerIndex ->
                event.getX(pointerIndex) to event.getY(pointerIndex)
            }
            if (dosDedos) {
                dosDedos = false
                if (yPos > this!!.yPosIni!!)
                    mTextView!!.text = "Desplazamiento hacia abajo"
                else
                    mTextView!!.text = "Desplazamiento hacia arriba"
            }else {
                xPosIni = xPos
                yPosIni = yPos
            }
        }

        if (event.pointerCount == 2){
            mTextView!!.text = ""
            dosDedos = true
            mActivePointerId = event.getPointerId(1)
            val (xPos2: Float, yPos2: Float) = event.findPointerIndex(mActivePointerId).let { pointerIndex ->
                event.getX(pointerIndex) to event.getY(pointerIndex)
            }
        }



       /* if (event.pointerCount == 2 && !dosDedos) {
            mTextView!!.text = "2 dedos" + mTextView!!.text
            dosDedos = true
            //val (x: Float, y: Float) = event.findPointerIndex(mActivePointerId).let { pointerIndex -> event.getX(pointerIndex) to event.getY(pointerIndex)}

        } else if (event.pointerCount == 1 && dosDedos ){
            // Single touch event
            mTextView!!.text = "Quito un dedo de dos" + mTextView!!.text
            dosDedos = false
        } else if(dosDedos){

        }else{
            dosDedos = false
            mTextView!!.text = "Otra cosa"
        }
*/
        return when (action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(DEBUG_TAG, "Action was DOWN")
                true
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(DEBUG_TAG, "Action was MOVE")
                true
            }
            MotionEvent.ACTION_UP -> {
                Log.d(DEBUG_TAG, "Action was UP")
                true
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.d(DEBUG_TAG, "Action was CANCEL")
                true
            }
            MotionEvent.ACTION_OUTSIDE -> {
                Log.d(DEBUG_TAG, "Movement occurred outside bounds of current screen element")
                true
            }

            else -> super.onTouchEvent(event)
        }
    }

    fun actionToString(action: Int): String {
        return when (action) {
            MotionEvent.ACTION_DOWN -> "Down"
            MotionEvent.ACTION_MOVE -> "Move"
            MotionEvent.ACTION_POINTER_DOWN -> "Pointer Down"
            MotionEvent.ACTION_UP -> "Up"
            MotionEvent.ACTION_POINTER_UP -> "Pointer Up"
            MotionEvent.ACTION_OUTSIDE -> "Outside"
            MotionEvent.ACTION_CANCEL -> "Cancel"
            else -> ""
        }
    }

}
