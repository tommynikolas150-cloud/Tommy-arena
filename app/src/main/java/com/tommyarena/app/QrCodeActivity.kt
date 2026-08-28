package com.tommyarena.app

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class QrCodeActivity : AppCompatActivity() {

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            findViewById<TextView>(R.id.scanResultText).text = "Scanned: ${result.contents}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)

        val input = findViewById<EditText>(R.id.qrInput)
        val image = findViewById<ImageView>(R.id.qrImage)

        findViewById<Button>(R.id.btnGenerate).setOnClickListener {
            val text = input.text.toString()
            if (text.isBlank()) return@setOnClickListener
            try {
                val writer = QRCodeWriter()
                val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
                val bmp = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
                for (x in 0 until 512) {
                    for (y in 0 until 512) {
                        bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
                image.setImageBitmap(bmp)
            } catch (e: Exception) {
                // ignore invalid input
            }
        }

        findViewById<Button>(R.id.btnScan).setOnClickListener {
            val options = ScanOptions()
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            options.setPrompt("Scan a QR code")
            options.setBeepEnabled(true)
            scanLauncher.launch(options)
        }
    }
}
