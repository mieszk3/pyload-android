package org.pyload.android.client

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import org.pyload.android.client.module.FileChooser
import org.pyload.android.client.utils.NavigationUtil

class AddLinksActivity : AppCompatActivity() {
    private var filename = "uploaded_from_android.dlc"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_links)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_CANCELED)
                NavigationUtil.navigateUp(this)
            }
        }
        return true
    }

    override fun onStart() {
        super.onStart()

        val intent = intent
        val name = intent.getStringExtra("name")
        if (name != null) {
            val nameView = findViewById<EditText>(R.id.new_packname)
            nameView.setText(name)
        }
        val url = intent.getStringExtra("url")
        if (url != null) {
            val urls = StringBuilder()
            val m = Patterns.WEB_URL.matcher(url)
            while (m.find()) {
                urls.append(m.group()).append("\n")
            }
            if (urls.isNotEmpty()) {
                val view = findViewById<EditText>(R.id.links)
                view.setText(urls.toString())
            }
        }
        val path = intent.getStringExtra("dlcpath")
        if (path != null) {
            val view = findViewById<EditText>(R.id.filename)
            view.setText(path)
        }

    }

    fun addPackage(button: View?) {

        val data = Intent()

        var view = findViewById<EditText>(R.id.new_packname)

        data.putExtra("name", view.text.toString())

        view = findViewById(R.id.links)
        data.putExtra("links", view.text.toString())

        view = findViewById(R.id.password)
        data.putExtra("password", view.text.toString())

        view = findViewById(R.id.filename)
        data.putExtra("filepath", view.text.toString().trim { it <= ' ' })
        data.putExtra("filename", filename)

        val spin = findViewById<Spinner>(R.id.destination)

        data.putExtra("dest", spin.selectedItemPosition)

        setResult(Activity.RESULT_OK, data)
        finish()
    }

    fun onCancel(button: View?) {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    fun pickFile(button: View?) {
        val intent = Intent().setClass(this, FileChooser::class.java)
        startActivityForResult(intent, FileChooser.CHOOSE_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FileChooser.CHOOSE_FILE -> when (resultCode) {
                Activity.RESULT_OK -> if (data != null) {
                    val path = data.getStringExtra("filepath")
                    filename = data.getStringExtra("filename") ?: ""
                    val view = findViewById<EditText>(R.id.filename)
                    view.setText(path)
                }
                else -> {
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        const val NEW_PACKAGE = 0
    }
}