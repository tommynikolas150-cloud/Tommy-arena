package com.tommyarena.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class FileCompressorActivity : AppCompatActivity() {

    private val pickFolderLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            compressFolder(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_compressor)

        findViewById<Button>(R.id.btnChooseFolder).setOnClickListener {
            pickFolderLauncher.launch(null)
        }
    }

    private fun compressFolder(treeUri: Uri) {
        val statusText = findViewById<TextView>(R.id.compressStatusText)
        statusText.text = "Compressing..."
        Thread {
            try {
                val zipFile = File(cacheDir, "compressed_${System.currentTimeMillis()}.zip")
                ZipOutputStream(zipFile.outputStream()).use { zos ->
                    val docId = DocumentsContract.getTreeDocumentId(treeUri)
                    val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, docId)
                    val cursor = contentResolver.query(
                        childrenUri, arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID, DocumentsContract.Document.COLUMN_DISPLAY_NAME),
                        null, null, null
                    )
                    cursor?.use {
                        while (it.moveToNext()) {
                            val childId = it.getString(0)
                            val name = it.getString(1)
                            val childUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, childId)
                            contentResolver.openInputStream(childUri)?.use { input ->
                                zos.putNextEntry(ZipEntry(name))
                                input.copyTo(zos)
                                zos.closeEntry()
                            }
                        }
                    }
                }
                runOnUiThread {
                    statusText.text = "Done! Sharing..."
                    val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", zipFile)
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "application/zip"
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(Intent.createChooser(intent, "Share Zip"))
                }
            } catch (e: Exception) {
                runOnUiThread { statusText.text = "Failed: ${e.message}" }
            }
        }.start()
    }
}
