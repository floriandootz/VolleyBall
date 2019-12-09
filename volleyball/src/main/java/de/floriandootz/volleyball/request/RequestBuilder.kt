package de.floriandootz.volleyball.request

import android.support.annotation.RawRes
import com.android.volley.Response
import de.floriandootz.volleyball.parse.Parser
import com.android.volley.Request as VolleyRequest

/**
 * Builds a request and executes it with the @Requester when .send() is called.
 *
 * @param url The url to request
 * @param listener The callback for the parsed result data.
 * @param errorListener The callback for any (http, parsing, ...) error.
 * @param rawFallbackRes The resource contained in the APK to be loaded if offline and no cache available. Useful for first starts of the application.
 * @param customParser A custom parser instead of @clazz.
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
    var listener: Response.Listener<T>? = null
        private set
    var errorListener: Response.ErrorListener? = null
        private set
    var requestStrategy: RequestStrategy = RequestStrategy.ONLINE_FALLBACK_CACHE_FALLBACK_RESOURCE
        private set
    var rawAndroidResource: Int? = null
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

    fun setListener(listener: Response.Listener<T>): RequestBuilder<T> {
        this.listener = listener
        return this
    }

    fun setErrorListener(errorListener: Response.ErrorListener): RequestBuilder<T> {
        this.errorListener = errorListener
        return this
    }

    /**
     * Default: RequestStrategy.ONLINE_FALLBACK_CACHE_FALLBACK_RESOURCE
     */
    fun setRequestStrategy(requestStrategy: RequestStrategy): RequestBuilder<T> {
        this.requestStrategy = requestStrategy
        return this
    }

    fun setRawAndroidResource(@RawRes rawAndroidResource: Int): RequestBuilder<T> {
        this.rawAndroidResource = rawAndroidResource
        return this
    }

    fun send() {
        requester.send(this)
    }

}
