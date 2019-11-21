package de.floriandootz.volleyball.request

import android.support.annotation.RawRes
import com.android.volley.Response
import de.floriandootz.volleyball.parse.Parser
import com.android.volley.Request as VolleyRequest

class RequestBuilder<T>(
        private val requester: Requester,
        public val url: String,
        public val parser: Parser<T>
) {

    var method: Int = VolleyRequest.Method.GET
        private set
    var body: String? = null
        private set
    var resId: Int? = null
        private set
    var doReloadIfOnline: Boolean = true
        private set
    var listener: Response.Listener<T>? = null
        private set
    var errorListener: Response.ErrorListener? = null
        private set

    /**
     * Default: VolleyRequest.Method.GET
     */
    fun setMethod(method: Int): RequestBuilder<T> {
        this.method = method
        return this
    }

    fun setBody(body: String): RequestBuilder<T> {
        this.body = body
        return this
    }

    fun setRawAndroidResource(@RawRes resId: Int): RequestBuilder<T> {
        this.resId = resId
        return this
    }

    fun setDoReloadIfOnline(doReloadIfOnline: Boolean): RequestBuilder<T> {
        this.doReloadIfOnline = doReloadIfOnline
        return this
    }

    fun setListener(listener: Response.Listener<T>): RequestBuilder<T> {
        this.listener = listener
        return this
    }

    fun setErrorListener(errorListener: Response.ErrorListener): RequestBuilder<T> {
        this.errorListener = errorListener
        return this
    }

    fun send() {
        requester.send(this)
    }

}
