package com.biw.mpvremot

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = "Settings"
        val pref = this.getSharedPreferences("default", android.content.Context.MODE_PRIVATE)
        editText.setText(pref.getString("IP", "127.0.0.1"))
        editText2.setText(pref.getInt("PORT", 755).toString())
        editText3.setText(pref.getInt("INPUTPORT", 756).toString())
        button.setOnClickListener(View.OnClickListener {


            val ed = pref.edit()
            try {
                ed.putString("IP", editText.text.toString())
                ed.putInt("PORT", editText2.text.toString().toInt())
                ed.putInt("INPUTPORT", editText3.text.toString().toInt())

            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Wrong input!", Toast.LENGTH_LONG).show()
            }
            ed.commit()
            finish()

        })
    }
}
