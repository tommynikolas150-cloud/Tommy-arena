package com.tommyarena.app

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class ImageToolsActivity : AppCompatActivity() {
    private var currentBitmap: Bitmap? = null

    private val pickLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val stream = contentResolver.openInputStream(uri)
            currentBitmap = BitmapFactory.decodeStream(stream)
            stream?.close()
            findViewById<ImageView>(R.id.imagePreview).setImageBitmap(currentBitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_tools)

        findViewById<Button>(R.id.btnPickImage).setOnClickListener { pickLauncher.launch("image/*") }

        findViewById<Button>(R.id.btnGrayscale).setOnClickListener {
            val src = currentBitmap ?: return@setOnClickListener
            val gray = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(gray)
            val paint = Paint()
            val cm = ColorMatrix()
            cm.setSaturation(0f)
            paint.colorFilter = ColorMatrixColorFilter(cm)
            canvas.drawBitmap(src, 0f, 0f, paint)
            currentBitmap = gray
            findViewById<ImageView>(R.id.imagePreview).setImageBitmap(gray)
        }

        findViewById<Button>(R.id.btnRotate).setOnClickListener {
            val src = currentBitmap ?: return@setOnClickListener
            val matrix = Matrix()
            matrix.postRotate(90f)
            val rotated = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
            currentBitmap = rotated
            findViewById<ImageView>(R.id.imagePreview).setImageBitmap(rotated)
        }

        findViewById<Button>(R.id.btnSaveImage).setOnClickListener {
            val bmp = currentBitmap ?: return@setOnClickListener
            try {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DISPLAY_NAME, "tommy_arena_${System.currentTimeMillis()}.jpg")
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    contentResolver.openOutputStream(it)?.use { out -> bmp.compress(Bitmap.CompressFormat.JPEG, 95, out) }
                    Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Save failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
