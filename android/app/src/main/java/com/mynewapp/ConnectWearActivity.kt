package com.mynewapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.wearable.Wearable
import com.mynewapp.preference.AppDataPref

class ConnectWearActivity : AppCompatActivity() {
    var nodeId =""
    //var editText : EditText?=null
    var stepTextview : TextView?=null
    var heartTextview : TextView?=null
    val handler = Handler(Looper.getMainLooper())
    private lateinit var appDataPref: AppDataPref
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_wear)
        appDataPref = AppDataPref(this)
        //var btn = findViewById<Button>(R.id.connect)
        //editText = findViewById<EditText>(R.id.editText)
        stepTextview = findViewById(R.id.Txtstep)
        heartTextview = findViewById(R.id.TxtHeart)
//        btn.setOnClickListener {
//            //
//            //detectWearableDevice()
//            connectToWatch()
//        }
//        var sndMsg = findViewById<Button>(R.id.sndMsgBtn)
//        sndMsg.setOnClickListener {
//            //sendDataToWearable("Hello world");
//            val text: String = editText?.getText().toString()
//            sendMessage(nodeId,text)
//        }
        startHealthDateRunnable()
    }
    private fun connectToWatch() {
        val nodeClient = Wearable.getNodeClient(this)
        val connectedNodesTask = nodeClient.connectedNodes


        connectedNodesTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val nodes = task.result

                if (!nodes.isNullOrEmpty()) {
                    for (node in nodes) {
                        Log.d("PhoneActivity", "Connected to node: ${node.displayName}")
                        Toast.makeText(this, "Connected node: ${node.displayName} - ${node.id}", Toast.LENGTH_LONG).show()
                        // Perform further operations with the connected node
                        nodeId = node.id
                    }
                } else {
                    Log.d("PhoneActivity", "No connected nodes found")
                    Toast.makeText(this, "No connected nodes found", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "No connected nodes found", Toast.LENGTH_LONG).show()
                Log.d("PhoneActivity", "Failed to get connected nodes")
            }
        }
    }

    private fun sendMessage(nodeId: String, message: String) {
        val messageClient = Wearable.getMessageClient(this)
        val sendMessageTask = messageClient.sendMessage(nodeId, "/message_path", message.toByteArray())

        sendMessageTask.addOnSuccessListener {

            Log.d("PhoneActivity", "Message sent successfully")
        }.addOnFailureListener {
            Log.d("PhoneActivity", "Failed to send message: ${it.message}")
        }
    }

    private fun startHealthDateRunnable() {
        handler.postDelayed(runnable, 500)
    }

    private val runnable = object : Runnable {
        override fun run() {
            val steps = appDataPref.getSteps()?.toDouble()?.toInt()
            val heart = appDataPref.getHeartRate()?.toDouble()?.toInt()

            stepTextview?.text = "$steps"
            heartTextview?.text = "$heart"

            handler.postDelayed(this, 1000)
        }
    }
}