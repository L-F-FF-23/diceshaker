package com.example.diceshaker


import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.sqrt
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var container: GridLayout
    private lateinit var sensorManager: SensorManager
    private lateinit var containerList: MutableList<Pair<Int,TextView>>
    private var accelerometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        containerList = mutableListOf<Pair<Int,TextView>>()
        container = findViewById<GridLayout>(R.id.dice_container)
        val addDiceButton: Button = findViewById<Button>(R.id.add_dice_button)
        val sidesinput: EditText = findViewById<EditText>(R.id.dicesidesinput)
        val resetButton: Button = findViewById<Button>(R.id.reset_Button)

        addDiceButton.setOnClickListener {
            if (!sidesinput.text.isEmpty() && sidesinput.text.toString() != "0"){
                val sides = sidesinput.text.toString().toInt()
                addDice(sides)
            }
        }
        resetButton.setOnClickListener {
            container.removeAllViews()
            containerList.removeAll(containerList)
        }
    }

    private val sensorListener = object : SensorEventListener {

        override fun onSensorChanged(event: SensorEvent) {

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val acceleration = sqrt((x * x + y * y + z * z).toDouble())

            if (acceleration > 15) {
                rollDice()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    fun rollDice(){
        for (dice in containerList) {
            dice.second.text = (1..dice.first).random().toString()
        }
    }

    fun addDice(sidesamount: Int) {
        val newDice = TextView(this)

        newDice.text = sidesamount.toString()
        newDice.textSize = 32f
        newDice.setPadding(20, 5, 20, 5)

        containerList.add(Pair(sidesamount,newDice))
        updateContainerBox()
    }

    fun updateContainerBox() {
        container.removeAllViews()
        for (dice in containerList) {
            container.addView(dice.second)
        }
    }
}