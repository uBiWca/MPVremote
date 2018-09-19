package com.biw.mpvremot

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.util.*
import kotlin.concurrent.thread
import kotlin.text.Charsets.UTF_8


class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {
    private var ip: String = ""
    private var prt: Int = 0
    private var rcv_prt: Int = 0
    var media_length: Int = 0
    var chnl = DatagramChannel.open()
    var blockview = false
    fun send_command(command: String) {
        /*
        This function simply sends command to MPV through UDP
         */
        Log.d("send_command", command)
        thread {
            try {
                var dtgt = DatagramSocket()
                var address = InetAddress.getByName(ip)
                var lngth = command.length
                var pckt = DatagramPacket(command.toByteArray(), lngth, address, prt)
                dtgt.send(pckt)
                dtgt.close()
            } catch (e: Exception) {
                Log.d("In send_command", e.message)
            }
        }
    }

    fun convertSecsToFullTime(time: String): String {
        /*
        Since MPV returns duration and position in seconds, we need to split them
        to hours and minutes

         */
        try {
            val hr = time.toInt() / 3600
            val min = (time.toInt() % 3600) / 60
            val sec = (time.toInt() % 3600) % 60
            return check4Zerro(hr.toString()) + ":" + check4Zerro(min.toString()) + ":" + check4Zerro(sec.toString())
        } catch (e: NumberFormatException) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            return "00:00:00"
        }
    }

    fun check4Zerro(str: String): String {
        if (str.length == 1) return "0" + str else return str
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        when (item?.itemId) {
            R.id.action_settings -> {
                var intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.action_exit -> finish()
        }
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        if (chnl.isOpen) chnl.close()
    }


    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if (p2) textView5.text = convertSecsToFullTime(p1.toString())
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        blockview = true // blocking seekbar from updating by another thread
    }


    override fun onStopTrackingTouch(p0: SeekBar?) {
        blockview = false
        send_command("seek " + seekBar3.progress.toString())
    }

    override fun onResume() {
        super.onResume()
        var pref = this.getSharedPreferences("default", android.content.Context.MODE_PRIVATE)
        ip = pref.getString("IP", "127.0.0.1")
        prt = pref.getInt("PORT", 755)
        rcv_prt = pref.getInt("INPUTPORT", 5050)
        Log.d("MPV", "settin port to " + rcv_prt.toString())
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putString("position", textView5.text.toString())
        outState?.putString("length", textView6.text.toString())
        outState?.putString("filename", textView4.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        textView5.text = savedInstanceState?.getString("position")
        textView6.text = savedInstanceState?.getString("length")
        textView4.text = savedInstanceState?.getString("filename")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "MPV remote"
        seekBar3.progress = 0
        textView4.isSelected = true
        thread {
            // this thread receives incoming massages from MPV and updates views accordingly to received info

            try {
                chnl.socket().bind(InetSocketAddress(5050))
                Log.d("MPV", "reopening port " + chnl.socket().port.toString())
                chnl.configureBlocking(false)
                var timerStarted: Boolean = false
                var timer = Timer()
                while (true) {
                    var buf = ByteBuffer.allocate(1024)
                    buf.clear()
                    var last_pos = ""
                    if (!timerStarted) {
                        timer = Timer()
                        timer.schedule(myTimerTask(textView5, textView6, textView4, seekBar3), 2000, 1000)
                        //this timer will clear views, if no info received in last 2 seconds
                        timerStarted = true
                    }
                    if (chnl.isOpen) {
                        if (chnl.receive(buf) != null) {
                            if (timerStarted) {
                                timer.cancel()
                                timerStarted = false
                            }
                            var data_length = buf.position()
                            buf.flip()
                            var str = String(buf.array(), 0, data_length, UTF_8)
                            Log.d("received data", str)
                            val arr = str.split("$")
                            if (arr.size >= 3) {
                                media_length = arr[1].toInt()
                                runOnUiThread {
                                    if (textView4.text != arr[0]) textView4.text = arr[0]
                                    if (last_pos != arr[1]) {
                                        last_pos = arr[1]
                                        textView6.text = convertSecsToFullTime(last_pos)
                                    }
                                    if (!blockview) {
                                        textView5.text = convertSecsToFullTime(arr[2])
                                    seekBar3.max = arr[1].toInt()
                                        seekBar3.progress = arr[2].toInt()
                                    }

                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
                Log.d("In MPVremote", e.message.toString())
            }
        }
        seekBar3.setOnSeekBarChangeListener(this)
        floatingActionButton.setOnClickListener { send_command("PLAY") }
        floatingActionButton2.setOnClickListener { send_command("PAUSE") }
        floatingActionButton3.setOnClickListener {
            send_command("QUIT")
        }
    }
}
