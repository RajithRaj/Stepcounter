package com.mynewapp.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.mynewapp.R
import kotlin.math.log


class WatchconnectAct : AppCompatActivity(), MessageClient.OnMessageReceivedListener,
    SensorEventListener {
    private lateinit var context: Context
    private lateinit var putDataMapRequest:PutDataMapRequest
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var heartRateSensor: Sensor? = null

    private val PERMISSION_REQUEST_CODE =100
    private  lateinit var stepTextView :TextView
    private  lateinit var heartTextView :TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        context = this
        setContentView(R.layout.activity_watchconnect)
        stepTextView = findViewById<TextView>(R.id.step)
        heartTextView = findViewById<TextView>(R.id.heart)
        if (supportActionBar != null) {
            supportActionBar?.hide();
        }
        registerMsgListner()
        connectionInfo()
        startListener()
        initializeDataClient()
        checkAndRequestPermissions()
    }

    fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionsToRequest: MutableList<String> = ArrayList()
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
            }


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS_BACKGROUND)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.BODY_SENSORS_BACKGROUND)
            }




            if (!permissionsToRequest.isEmpty()) {
                val permissionsArray = permissionsToRequest.toTypedArray<String>()
                ActivityCompat.requestPermissions(this, permissionsArray, PERMISSION_REQUEST_CODE)
            } else {

            }


        }
    }

    override fun onResume() {
        super.onResume()
        heartRateSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        heartRateSensor?.let {
            sensorManager?.unregisterListener(this)
        }
        stepSensor?.let {
            sensorManager?.unregisterListener(this)
        }
    }
    private fun initializeDataClient(){
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        putDataMapRequest = PutDataMapRequest.create("/heath_data/provider")
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    }

    private fun registerMsgListner(){
        Wearable.getMessageClient(this).addListener(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val message = String(messageEvent.data)
        Toast.makeText(this,"Message from app:$message",Toast.LENGTH_LONG).show()
    }
    private fun connectionInfo(){
        val nodeListTask: Task<List<Node?>> = Wearable.getNodeClient(this).connectedNodes
        nodeListTask.addOnSuccessListener { nodes: List<Node?> ->

            var nodeId: String? = ""
            var displayName: String? = ""
            for (node in nodes) {
                nodeId = node?.id
                displayName = node?.displayName
                Log.e("NODE_IDS",""+nodeId)
            }
            Toast.makeText(this, "Connected node :$nodeId : $displayName", Toast.LENGTH_LONG).show()
        }
    }

    private fun startListener(){
        val intent = Intent(context, MessageListener::class.java)
        startService(intent)

    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_HEART_RATE) {
                val heartRateValue = it.values[0]
                if (heartRateValue.toInt() == 0) return
                heartTextView.text = "$heartRateValue"
                putDataMapRequest.dataMap.putString("heartrate", "$heartRateValue")
                val request = putDataMapRequest.asPutDataRequest()
                Wearable.getDataClient(this).putDataItem(request)

            }
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {

                val stepValue = it.values[0]
                stepTextView.text = "$stepValue"
                putDataMapRequest.dataMap.putString("steps", "$stepValue")
                val request = putDataMapRequest.asPutDataRequest()
                Wearable.getDataClient(this).putDataItem(request)
            }
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.e("DATA","$accuracy")
    }
}