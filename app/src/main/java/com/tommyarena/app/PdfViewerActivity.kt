package com.tommyarena.app

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class PdfViewerActivity : AppCompatActivity() {
    private var renderer: PdfRenderer? = null
    private var fileDescriptor: ParcelFileDescriptor? = null
    private var currentPage = 0

    private val openLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) openPdf(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        findViewById<Button>(R.id.btnOpenPdf).setOnClickListener {
            openLauncher.launch(arrayOf("application/pdf"))
        }
        findViewById<Button>(R.id.btnPrevPage).setOnClickListener {
            if (currentPage > 0) { currentPage--; showPage() }
        }
        findViewById<Button>(R.id.btnNextPage).setOnClickListener {
            renderer?.let { if (currentPage < it.pageCount - 1) { currentPage++; showPage() } }
        }
    }

    private fun openPdf(uri: Uri) {
        try {
            fileDescriptor?.close()
            renderer?.close()
            fileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            renderer = PdfRenderer(fileDescriptor!!)
            currentPage = 0
            showPage()
        } catch (e: Exception) {
            Toast.makeText(this, "Couldn't open PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPage() {
        val r = renderer ?: return
        val page = r.openPage(currentPage)
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        findViewById<ImageView>(R.id.pdfPageImage).setImageBitmap(bitmap)
        findViewById<TextView>(R.id.pdfPageLabel).text = "Page ${currentPage + 1} of ${r.pageCount}"
        page.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        renderer?.close()
        fileDescriptor?.close()
    }
}
