package de.floriandootz.volleyball.request

import android.support.annotation.RawRes
import com.android.volley.Response
import de.floriandootz.volleyball.parse.Parser
import com.android.volley.Request as VolleyRequest

/**
 * Builds a request and executes it with the @Requester when .send() is called.
 *
 * @param url The url to request
 * @param clazz The class to parse the answer into. Alternatively you can supply a @customParser.
 * @param forceReloadIfOnline If true, the cached data will be returned if online.
 * @param listener The callback for the parsed result data.
 * @param errorListener The callback for any (http, parsing, ...) error.
 * @param rawFallbackRes The resource contained in the APK to be loaded if offline and no cache available. Useful for first starts of the application.
 * @param customParser A custom parser instead of @clazz.
 * @param <T> The class you are parsing to.
 */
class RequestBuilder<T>(
        private val requester: Requester,
        public val url: String,
        public val parser: Parser<T>
) {

    var method: Int = VolleyRequest.Method.GET
        private set
    var body: String? = null
        private set
    var rawAndroidResource: Int? = null
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

    fun setRawAndroidResource(@RawRes rawAndroidResource: Int): RequestBuilder<T> {
        this.rawAndroidResource = rawAndroidResource
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
