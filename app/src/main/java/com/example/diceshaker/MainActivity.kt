package com.example.diceshaker


import android.graphics.Typeface.MONOSPACE
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
import androidx.constraintlayout.helper.widget.Grid

class MainActivity : AppCompatActivity() {
    private lateinit var container: GridLayout
    private lateinit var sensorManager: SensorManager
    private lateinit var containerList: MutableList<Pair<Int,TextView>> //This is like a dict, I use this and update the gridlayout after it

    //Both container and containerList is here due to the program needing to access both multiple times throughout the code, could be done without them here but then I'm just needlessly fetching them by id over and over again
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
            containerList.removeAll(containerList)
            updateContainerBox()
        }
        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL) //The actual thing that checks the sensor
    }

    private val sensorListener = object : SensorEventListener {

        override fun onSensorChanged(event: SensorEvent) {

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val acceleration = sqrt((x * x + y * y + z * z).toDouble()) //x,y,z are all different directions the phone can sense then you do physics math here

            if (acceleration > 15) {
                rollDice()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        } //this needs to be here to avoid errors but I don't have to use it
    }

    fun rollDice(){
        for (dice in containerList) {
            dice.second.text = (1..dice.first).random().toString()
        }
        updateContainerBox()
    }

    fun addDice(sidesamount: Int) {

        val newDice = TextView(this)
        val gridLayoutParams = GridLayout.LayoutParams().apply {
            width = 0 //tells it to use the columSpec weight, I think
            height = dpToPx(80)
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f) //these would be use to specify where in row and column the object should be, but by using weight I can make it become dynamic changing postion to fit better
            rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
        } //this is so I can tell the new textviews how they should form themselves in the grid
        newDice.layoutParams = gridLayoutParams
        newDice.text = sidesamount.toString()
        newDice.setTypeface(MONOSPACE) //all digits take up the same space
        newDice.setPadding(20, 5, 20, 5)

        containerList.add(Pair(sidesamount,newDice))
        updateContainerBox()
    }

    fun updateContainerBox() {
        val counter: TextView = findViewById<TextView>(R.id.totalCount)
        var count = 0
        container.removeAllViews()
        for (dice in containerList) {
            dice.second.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM) //changes the font size to fit in the TextView size, had to bump up the minimum api by 2 version, the alternative game me a headache
            count += dice.second.text.toString().toInt()
            container.addView(dice.second)
        }
        counter.text = count.toString()
    }

    fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    } //this is pretty simple and neat, allows you to use dp programmatically when adding or modifying objects, will use again in the future
}