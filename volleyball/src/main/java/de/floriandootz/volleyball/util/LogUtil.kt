package de.floriandootz.volleyball.util

import android.util.Log

object LogUtil {

    private const val TAG = "VolleyBall"

    fun v(text: String) {
        Log.v(TAG, text);
    }

    fun d(text: String) {
        Log.d(TAG, text);
    }

    fun w(text: String) {
        Log.w(TAG, text);
    }

    fun e(text: String) {
        Log.e(TAG, text);
    }

}
