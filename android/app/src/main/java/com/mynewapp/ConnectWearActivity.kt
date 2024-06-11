package com.mynewapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.gms.wearable.Wearable
import com.mynewapp.preference.AppDataPref


class ConnectWearActivity : AppCompatActivity() {
    var nodeId =""
    //var editText : EditText?=null
    var stepTextview : TextView?=null
    var heartTextview : TextView?=null
    val handler = Handler(Looper.getMainLooper())
    private lateinit var appDataPref: AppDataPref
    var lineChart: LineChart? = null
    var layoutOne:LinearLayout?=null
    var layoutTwo:LinearLayout?=null
    var layoutThree:LinearLayout?=null
    var graphTitle:TextView?=null
    val times = arrayOf("8:00 am", "9:00 am", "10:00 am", "11:00 am", "12:00 pm", "1:00 pm","2:00 pm","3:00 pm","4:00 pm")

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_wear)
        appDataPref = AppDataPref(this)
        //var btn = findViewById<Button>(R.id.connect)
        //editText = findViewById<EditText>(R.id.editText)
        stepTextview = findViewById(R.id.Txtstep)
        heartTextview = findViewById(R.id.TxtHeart)
        lineChart = findViewById(R.id.lineChart);

        layoutOne = findViewById(R.id.riskLayoutOne);
        layoutTwo = findViewById(R.id.riskLayoutTwo);
        layoutThree = findViewById(R.id.riskLayoutThree);
        graphTitle = findViewById(R.id.graphTitle);
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

        bindViews()
    }

    private fun bindViews() {
        setupLineChart();
        setCompositeRisk()
        layoutOne?.setOnClickListener {
            graphTitle?.text = "Composite"
         setCompositeRisk()

        }
        layoutTwo?.setOnClickListener {
            graphTitle?.text = "Fatigue"
          setFatigueRisk()
        }
        layoutThree?.setOnClickListener {
            graphTitle?.text = "Dehydration"
           setDehyfrationRisk()
        }
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
    private fun setupLineChart() {
        lineChart?.apply {
            setDrawGridBackground(false)
            description.isEnabled = false

            // Customize X-Axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 2f
                valueFormatter = getTimeValueFormatter(times)
            }

            // Customize Y-Axis
            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 70f
                granularity = 20f
                setLabelCount(6, true) // Force specific labels
                setDrawGridLines(true)
            }
            axisRight.apply{
                isEnabled = false
            }
            xAxis.setDrawGridLines(true) // Enable x-axis grid lines


            legend.isEnabled = false
            // Customize Legend
//            legend.form = Legend.LegendForm.LINE
        }
    }

    private fun loadLineChartData(entries:ArrayList<Entry>,title:String) {

        graphTitle?.text = title
        val dataSet = LineDataSet(entries, "Sample Data").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        lineChart?.data = LineData(dataSet)
        lineChart?.invalidate() // Refresh the chart
    }

    fun setCompositeRisk (){
        val entries = ArrayList<Entry>().apply {
            add(Entry(0f, 10f))
            add(Entry(1f, 15f))
            add(Entry(2f, 12f))
            add(Entry(3f, 20f))
            add(Entry(4f, 21f))
            add(Entry(5f, 23f))
            add(Entry(6f, 25f))
            add(Entry(7f, 26f))
            add(Entry(8f, 24f))
        }
        loadLineChartData(entries,"Composite risk")



        val backgroundOne = layoutOne?.background as? GradientDrawable
        backgroundOne?.setStroke(5, Color.parseColor("#DD5746"))
        layoutOne?.background = backgroundOne

        val backgroundTwo = layoutTwo?.background as? GradientDrawable
        backgroundTwo?.setStroke(0, Color.parseColor("#0000ff"))
        layoutTwo?.background = backgroundTwo

        val backgroundThree = layoutThree?.background as? GradientDrawable
        backgroundThree?.setStroke(0, Color.parseColor("#0000ff"))
        layoutThree?.background = backgroundThree



    }
    fun setFatigueRisk(){
        val entries = ArrayList<Entry>().apply {
            add(Entry(0f, 10f))
            add(Entry(1f, 15f))
            add(Entry(2f, 20f))
            add(Entry(3f, 21f))
            add(Entry(4f, 23f))
            add(Entry(5f, 25f))
            add(Entry(6f, 26f))
            add(Entry(7f, 35f))
            add(Entry(8f, 30f))
        }
        loadLineChartData(entries,"Fatigue risk")
        val backgroundOne = layoutOne?.background as? GradientDrawable
        backgroundOne?.setStroke(0, Color.parseColor("#0000ff"))
        layoutOne?.background = backgroundOne

        val backgroundTwo = layoutTwo?.background as? GradientDrawable
        backgroundTwo?.setStroke(5, Color.parseColor("#40A578"))
        layoutTwo?.background = backgroundTwo

        val backgroundThree = layoutThree?.background as? GradientDrawable
        backgroundThree?.setStroke(0, Color.parseColor("#0000ff"))
        layoutThree?.background = backgroundThree
    }
    fun setDehyfrationRisk(){
        val entries = ArrayList<Entry>().apply {
            add(Entry(0f, 5f))
            add(Entry(1f, 10f))
            add(Entry(2f, 12f))
            add(Entry(3f, 14f))
            add(Entry(4f, 20f))
            add(Entry(5f, 25f))
            add(Entry(6f, 28f))
            add(Entry(7f, 35f))
            add(Entry(8f, 40f))
        }
        loadLineChartData(entries,"Dehydration risk")

        val backgroundOne = layoutOne?.background as? GradientDrawable
        backgroundOne?.setStroke(0, Color.parseColor("#0000ff"))
        layoutOne?.background = backgroundOne

        val backgroundTwo = layoutTwo?.background as? GradientDrawable
        backgroundTwo?.setStroke(0, Color.parseColor("#0000ff"))
        layoutTwo?.background = backgroundTwo

        val backgroundThree = layoutThree?.background as? GradientDrawable
        backgroundThree?.setStroke(5, Color.parseColor("#FF7D29"))
        layoutThree?.background = backgroundThree
    }

    fun getTimeValueFormatter(times: Array<String>): ValueFormatter {
        return object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < times.size) {
                    times[index]
                } else {
                    value.toString()
                }
            }
        }
    }

}