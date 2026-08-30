package com.tommyarena.app

import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class FileEncryptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_encryption)

        val input = findViewById<EditText>(R.id.encryptInput)
        val password = findViewById<EditText>(R.id.passwordInput)
        val result = findViewById<TextView>(R.id.encryptResult)

        findViewById<Button>(R.id.btnEncrypt).setOnClickListener {
            try {
                val key = deriveKey(password.text.toString())
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                val iv = ByteArray(16)
                cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
                val encrypted = cipher.doFinal(input.text.toString().toByteArray())
                result.text = Base64.encodeToString(encrypted, Base64.DEFAULT)
            } catch (e: Exception) {
                result.text = "Encryption failed: ${e.message}"
            }
        }

        findViewById<Button>(R.id.btnDecrypt).setOnClickListener {
            try {
                val key = deriveKey(password.text.toString())
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                val iv = ByteArray(16)
                cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
                val decoded = Base64.decode(input.text.toString().trim(), Base64.DEFAULT)
                val decrypted = cipher.doFinal(decoded)
                result.text = String(decrypted)
            } catch (e: Exception) {
                result.text = "Decryption failed. Wrong password or invalid text."
            }
        }
    }

    private fun deriveKey(password: String): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(password.toByteArray())
        return SecretKeySpec(keyBytes, "AES")
    }
}
