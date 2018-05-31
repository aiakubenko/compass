package com.ai.ally.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.RotateAnimation
import kotlinx.android.synthetic.main.activity_main.*
import android.view.animation.Animation
import android.widget.TextView
import android.content.DialogInterface
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.content.DialogInterface.OnShowListener

class MainActivity : AppCompatActivity(), SensorEventListener {
    val I_LOG_TAG = "COMPASS"
    lateinit var sensorManager: SensorManager
    var currentDegree: Float = 0F
    private lateinit var calibrateAlertView:View
    lateinit var alertText: TextView
    lateinit var sp: SharedPreferences
    lateinit var alertDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)
        this.layout.setBackgroundResource(R.drawable.old_coffe_paper_texture)
        this.image_view_compass_rotating.setImageResource(R.drawable.compass_0)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        calibrateAlertView = View.inflate(this, R.layout.calibrate_sensor_alert, null)
        alertText = calibrateAlertView.findViewById(R.id.alert_text)
        sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }

    override fun onStart() {
        super.onStart()
                if (sp.getString(resources.getString(R.string.extras_calibrtion_value),"") != "") {
                    Log.i(I_LOG_TAG, "Calibration status: "+sp.getString(resources.getString(R.string.extras_calibrtion_value),""))
                } else {
                    Log.i(I_LOG_TAG, "Calibration status NOT TRUE: "+sp.getString(resources.getString(R.string.extras_calibrtion_value),""))
                    this.showAlert()
                }
    }
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME)
    }
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    override fun onSensorChanged(event: SensorEvent?) {
        var degree = Math.round(event!!.values[0])
        this.text_view_degree_value.text = degree.toString()+ getString(R.string.direction_degrees)
        var rotateAnimation =  RotateAnimation(
                        currentDegree,
                        -degree.toFloat(),
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF,
                0.5f)
        rotateAnimation.duration = 500
        rotateAnimation.fillAfter = true
        this.image_view_compass_rotating.startAnimation(rotateAnimation)
        currentDegree = -degree.toFloat()
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //not in use
    }

    private fun showAlert() {
        val calibrationInstructionAlertBuilder = AlertDialog.Builder(this)
        calibrationInstructionAlertBuilder.setView(calibrateAlertView)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.alert_ok_button), DialogInterface.OnClickListener { dialog, id ->
                    sp.edit().putString(getString(R.string.extras_calibrtion_value), resources.getString(R.string.boolean_true)).apply()
                    Log.i(I_LOG_TAG, "Calibration status IS : "+sp.getString(getString(R.string.extras_calibrtion_value),""))
                    dialog.cancel()
                })

        alertDialog = calibrationInstructionAlertBuilder.create()
        alertDialog.setOnShowListener(OnShowListener { alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.color_button_text)) })
        alertDialog.show()
        alertDialog.window.setBackgroundDrawableResource(R.drawable.old_coffe_paper_texture)
        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val positiveButtonLayout = positiveButton.layoutParams as LinearLayout.LayoutParams
        positiveButtonLayout.gravity = Gravity.CENTER
        positiveButtonLayout.width = ViewGroup.LayoutParams.MATCH_PARENT
        positiveButton.layoutParams = positiveButtonLayout
       // positiveButton.setBackgroundColor(resources.getColor(R.color.color_alert_button_back))

    }
}
