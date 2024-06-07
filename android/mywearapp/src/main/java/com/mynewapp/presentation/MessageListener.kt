package com.mynewapp.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.WearableListenerService

class MessageListener : WearableListenerService() {


    @SuppressLint("SuspiciousIndentation")
    override fun onMessageReceived(messageEvent: MessageEvent) {






//        Toast.makeText(this,"Hello form Rajith Rajan",Toast.LENGTH_LONG).show()
//        if (messageEvent.path == "/message_path") {
//            val message = String(messageEvent.data)
//            Toast.makeText(this,"New message :$message",Toast.LENGTH_LONG).show()
//            // Handle received message
//        }
    }

}