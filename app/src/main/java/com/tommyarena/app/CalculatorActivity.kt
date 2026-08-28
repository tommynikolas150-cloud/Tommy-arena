package com.tommyarena.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CalculatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        val input = findViewById<EditText>(R.id.inputExpression)
        val result = findViewById<TextView>(R.id.resultText)

        findViewById<Button>(R.id.btnCalculate).setOnClickListener {
            val expr = input.text.toString()
            try {
                val value = ExpressionEvaluator(expr).evaluate()
                result.text = "= $value"
            } catch (e: Exception) {
                result.text = "Invalid expression"
            }
        }
    }
}

/**
 * Minimal recursive-descent evaluator supporting + - * / ( ) and decimals.
 * Avoids any use of a scripting engine.
 */
class ExpressionEvaluator(private val expr: String) {
    private var pos = -1
    private var ch: Char = ' '

    private fun nextChar() {
        pos++
        ch = if (pos < expr.length) expr[pos] else ' '
    }

    private fun eat(charToEat: Char): Boolean {
        while (ch == ' ') nextChar()
        if (ch == charToEat) {
            nextChar()
            return true
        }
        return false
    }

    fun evaluate(): Double {
        nextChar()
        val x = parseExpression()
        if (pos < expr.length) throw RuntimeException("Unexpected character")
        return x
    }

    private fun parseExpression(): Double {
        var x = parseTerm()
        while (true) {
            when {
                eat('+') -> x += parseTerm()
                eat('-') -> x -= parseTerm()
                else -> return x
            }
        }
    }

    private fun parseTerm(): Double {
        var x = parseFactor()
        while (true) {
            when {
                eat('*') -> x *= parseFactor()
                eat('/') -> x /= parseFactor()
                else -> return x
            }
        }
    }

    private fun parseFactor(): Double {
        if (eat('+')) return parseFactor()
        if (eat('-')) return -parseFactor()

        var x: Double
        val startPos = pos
        if (eat('(')) {
            x = parseExpression()
            eat(')')
        } else if (ch.isDigit() || ch == '.') {
            while (ch.isDigit() || ch == '.') nextChar()
            x = expr.substring(startPos, pos).toDouble()
        } else {
            throw RuntimeException("Unexpected character: $ch")
        }
        return x
    }
}
