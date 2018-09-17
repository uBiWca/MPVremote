package com.biw.mpvremot

import java.util.*

/**
 * Created by Пользователь on 06.09.2018.
 */
class mTimer : Timer() {
    private var isStarted: Boolean = false
    fun isStarted(): Boolean {
        return this.isStarted
    }

    fun isStarted(isstarted: Boolean) {
        this.isStarted = isstarted
    }
}