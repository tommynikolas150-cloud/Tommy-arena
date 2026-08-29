package com.tommyarena.app

import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MarkdownActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markdown)

        val input = findViewById<EditText>(R.id.markdownInput)
        val preview = findViewById<TextView>(R.id.markdownPreview)

        fun render() {
            val html = markdownToHtml(input.text.toString())
            preview.text = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        }

        input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = render()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun markdownToHtml(md: String): String {
        val lines = md.split("\n")
        val sb = StringBuilder()
        for (line in lines) {
            var l = line
            l = l.replace(Regex("^### (.*)"), "<h3>$1</h3>")
            l = l.replace(Regex("^## (.*)"), "<h2>$1</h2>")
            l = l.replace(Regex("^# (.*)"), "<h1>$1</h1>")
            l = l.replace(Regex("\\*\\*(.*?)\\*\\*"), "<b>$1</b>")
            l = l.replace(Regex("_(.*?)_"), "<i>$1</i>")
            l = l.replace(Regex("`(.*?)`"), "<font face='monospace' color='#7B2FF7'>$1</font>")
            l = l.replace(Regex("^- (.*)"), "&bull; $1")
            sb.append(l).append("<br>")
        }
        return sb.toString()
    }
}
