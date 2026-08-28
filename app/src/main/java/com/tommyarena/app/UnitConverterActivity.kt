package com.tommyarena.app

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class UnitConverterActivity : AppCompatActivity() {

    // category -> unit -> factor to base unit
    private val categories = linkedMapOf(
        "Length" to linkedMapOf(
            "Meters" to 1.0,
            "Kilometers" to 1000.0,
            "Centimeters" to 0.01,
            "Miles" to 1609.34,
            "Feet" to 0.3048,
            "Inches" to 0.0254
        ),
        "Weight" to linkedMapOf(
            "Kilograms" to 1.0,
            "Grams" to 0.001,
            "Pounds" to 0.453592,
            "Ounces" to 0.0283495
        ),
        "Temperature" to linkedMapOf(
            "Celsius" to 0.0,
            "Fahrenheit" to 0.0,
            "Kelvin" to 0.0
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_converter)

        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        val fromUnitSpinner = findViewById<Spinner>(R.id.fromUnitSpinner)
        val toUnitSpinner = findViewById<Spinner>(R.id.toUnitSpinner)
        val fromValue = findViewById<EditText>(R.id.fromValue)
        val resultText = findViewById<TextView>(R.id.resultText)

        categorySpinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, categories.keys.toList()
        )

        fun refreshUnits() {
            val cat = categorySpinner.selectedItem as String
            val units = categories[cat]!!.keys.toList()
            fromUnitSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, units)
            toUnitSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, units)
        }
        refreshUnits()

        categorySpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: android.widget.AdapterView<*>?, v: android.view.View?, pos: Int, id: Long) = refreshUnits()
            override fun onNothingSelected(p: android.widget.AdapterView<*>?) {}
        }

        findViewById<Button>(R.id.btnConvert).setOnClickListener {
            val value = fromValue.text.toString().toDoubleOrNull()
            if (value == null) {
                resultText.text = "Enter a valid number"
                return@setOnClickListener
            }
            val cat = categorySpinner.selectedItem as String
            val fromUnit = fromUnitSpinner.selectedItem as String
            val toUnit = toUnitSpinner.selectedItem as String

            val converted = if (cat == "Temperature") {
                convertTemperature(value, fromUnit, toUnit)
            } else {
                val fromFactor = categories[cat]!![fromUnit]!!
                val toFactor = categories[cat]!![toUnit]!!
                value * fromFactor / toFactor
            }
            resultText.text = "%.4f %s".format(converted, toUnit)
        }
    }

    private fun convertTemperature(value: Double, from: String, to: String): Double {
        val celsius = when (from) {
            "Celsius" -> value
            "Fahrenheit" -> (value - 32) * 5 / 9
            "Kelvin" -> value - 273.15
            else -> value
        }
        return when (to) {
            "Celsius" -> celsius
            "Fahrenheit" -> celsius * 9 / 5 + 32
            "Kelvin" -> celsius + 273.15
            else -> celsius
        }
    }
}
