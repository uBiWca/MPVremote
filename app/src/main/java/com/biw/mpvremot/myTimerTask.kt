package com.biw.mpvremot

import android.widget.SeekBar
import android.widget.TextView
import java.util.*

/**
 * Created by Пользователь on 05.09.2018.
 */
class myTimerTask(var _textView5: TextView, var _textView6: TextView, var _textView4: TextView, var _seekBar3: SeekBar /*var _context: Context*/) : TimerTask() {
    private val textView5 = _textView5
    private val textView6 = _textView6
    private val textView4 = _textView4
    private val seekBar3 = _seekBar3


    override fun run() {
        seekBar3.post({ seekBar3.progress = 0 })
        textView4.post({ textView4.text = "nothing" })
        textView5.post({ textView5.text = "00:00:00" })
        textView6.post({ textView6.text = "00:00:00" })

    }
}