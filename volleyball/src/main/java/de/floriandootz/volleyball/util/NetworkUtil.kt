package de.floriandootz.volleyball.util

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtil {

    /**
     * http://stackoverflow.com/a/4239019/192533
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
     */
    fun isNetworkAvailable(ctx: Context?): Boolean {
        if (ctx == null) return false
        val connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}
