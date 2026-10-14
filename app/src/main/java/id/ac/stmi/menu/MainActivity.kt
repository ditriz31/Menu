package id.ac.stmi.menu

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    private lateinit var tvQuote: TextView
    private lateinit var tvParagraph: TextView

    private val quotes = listOf(
        "LOREM IPSUM DOLOR SIT AMET",
        "CONSECTETUR ADIPISCING ELIT",
        "SED DO EIUSMOD TEMPOR"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById<MaterialToolbar>(R.id.toolbar))

        tvQuote = findViewById(R.id.tvQuote)
        tvParagraph = findViewById(R.id.tvParagraph)

        // Context Menu: tekan lama paragraf
        registerForContextMenu(tvParagraph)

        // Popup Menu: tombol Action
        findViewById<View>(R.id.btnAction).setOnClickListener { showPopupMenu(it) }

        findViewById<View>(R.id.btnCopy).setOnClickListener { copyText(tvQuote.text) }
        findViewById<View>(R.id.btnSave).setOnClickListener {
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.btnShare).setOnClickListener { shareText(tvQuote.text) }
    }

    // ---- Options Menu ----
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menu_about -> {
            AlertDialog.Builder(this)
                .setTitle(R.string.menu_about)
                .setMessage(R.string.about_message)
                .setPositiveButton(R.string.ok, null)
                .show()
            true
        }
        R.id.menu_settings -> {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }
        R.id.menu_quotes -> {
            AlertDialog.Builder(this)
                .setTitle(R.string.quotes_title)
                .setItems(quotes.toTypedArray()) { _, which -> tvQuote.text = quotes[which] }
                .show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    // ---- Context Menu ----
    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.setHeaderTitle(R.string.context_title)
        menu.add(Menu.NONE, MENU_COPY, Menu.NONE, R.string.action_copy)
        menu.add(Menu.NONE, MENU_EDIT, Menu.NONE, R.string.action_edit)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        MENU_COPY -> {
            copyText(tvParagraph.text)
            true
        }
        MENU_EDIT -> {
            editParagraph()
            true
        }
        else -> super.onContextItemSelected(item)
    }

    // ---- Popup Menu ----
    private fun showPopupMenu(anchor: View) {
        PopupMenu(this, anchor).apply {
            menuInflater.inflate(R.menu.popup_menu, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.popup_download -> {
                        Toast.makeText(this@MainActivity, R.string.downloaded, Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.popup_share -> {
                        shareText(tvQuote.text)
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    private fun copyText(text: CharSequence) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("text", text))
        Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show()
    }

    private fun editParagraph() {
        val input = EditText(this).apply { setText(tvParagraph.text) }
        AlertDialog.Builder(this)
            .setTitle(R.string.edit_title)
            .setView(input)
            .setPositiveButton(R.string.save) { _, _ -> tvParagraph.text = input.text }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun shareText(text: CharSequence) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text.toString())
        }
        startActivity(Intent.createChooser(send, getString(R.string.action_share)))
    }

    private companion object {
        const val MENU_COPY = 1
        const val MENU_EDIT = 2
    }
}
