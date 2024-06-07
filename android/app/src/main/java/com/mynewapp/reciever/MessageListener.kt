package com.mynewapp.reciever

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.mynewapp.preference.AppDataPref


class MessageListener : WearableListenerService() {
    private lateinit var appDataPref: AppDataPref


    override fun onCreate() {
        super.onCreate()
        appDataPref = AppDataPref(baseContext)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onMessageReceived(messageEvent: MessageEvent) {

//        Toast.makeText(this,"Hello form Rajith Rajan",Toast.LENGTH_LONG).show()
//        if (messageEvent.path == "/message_path") {
//            val message = String(messageEvent.data)
//            Toast.makeText(this,"New message :$message",Toast.LENGTH_LONG).show()
//            // Handle received message
//        }
    }

    override fun onDataChanged(newData: DataEventBuffer) {
        super.onDataChanged(newData)

        for (event in newData) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val item = event.dataItem
                if ("/heath_data/provider" == item.uri.path) {
                    val dataMap = DataMapItem.fromDataItem(item).dataMap
                    val stepData = dataMap.getString("steps")
                    val heartData = dataMap.getString("heartrate")
                    if(stepData!=null){
                        handleReceivedStepData(stepData)
                    }
                    if (heartData!=null){
                        handleReceivedHeartRateData(heartData)
                    }
                    // Handle the received data
                    // For example, send a broadcast or update a database

                }
            }
        }
    }

    private fun handleReceivedStepData(data: String?) {
        // Your logic to handle the received data
        Log.d("MyWearableListenerService", "Data received: $data")
        appDataPref.updateStep(data)
        //Toast.makeText(baseContext,"New data from watch : $data",Toast.LENGTH_SHORT).show()

    }
    private fun handleReceivedHeartRateData(data: String?) {
        // Your logic to handle the received data
        Log.d("MyWearableListenerService", "Data received: $data")
        appDataPref.updateHeartRate(data)
        //Toast.makeText(baseContext,"New data from watch : $data",Toast.LENGTH_SHORT).show()
    }

}