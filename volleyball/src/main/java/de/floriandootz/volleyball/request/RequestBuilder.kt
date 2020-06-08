package de.floriandootz.volleyball.request

import android.net.Uri
import android.support.annotation.RawRes
import com.android.volley.Response
import de.floriandootz.volleyball.parse.Parser
import com.android.volley.Request as VolleyRequest

/**
 * Don't construct yourself. Instead use: Requester.build()
 * Builds a request and executes it with the Requester when .send() is called.
 */
class RequestBuilder<T> {

    private val requester: Requester
    private val uri: Uri.Builder

    val parser: Parser<T>
    val url: String
        get() = uri.build().toString()

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
     * Don't construct yourself. Instead use: Requester.build()
     * Builds a request and executes it with the Requester when .send() is called.
     *
     * @requester The requester this request was build and will be sent with.
     * @param url The url to request
     * @param parser Either a custom parser by implementing {@link Parser} or a parser from the volleyball-library:
     * - ClassParser
     * - ArrayParser
     * - StringParser
     * - VoidParser
     *
     * @see Requester
     */
    constructor(requester: Requester, url: String, parser: Parser<T>) {
        this.requester = requester
        this.uri = Uri.parse(url).buildUpon()
        this.parser = parser
    }

    /**
     * Default: VolleyRequest.Method.GET
     */
    fun setMethod(method: Int): RequestBuilder<T> {
        this.method = method
        return this
    }

    /**
     * The body added to the request.
     */
    fun setBody(body: String?): RequestBuilder<T> {
        this.body = body
        return this
    }

    /**
     * The callback for the parsed result data.
     */
    fun setListener(listener: Response.Listener<T>?): RequestBuilder<T> {
        this.listener = listener
        return this
    }

    /**
     * The callback for any (http, parsing, ...) error.
     */
    fun setErrorListener(errorListener: Response.ErrorListener?): RequestBuilder<T> {
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

    /**
     * The resource contained in the APK to be loaded if offline and no cache available. Useful for first starts of the application.
     */
    fun setRawAndroidResource(@RawRes rawAndroidResource: Int): RequestBuilder<T> {
        this.rawAndroidResource = rawAndroidResource
        return this
    }

    /**
     * Appends a query parameter to the url.
     */
    fun appendQueryParameter(key: String, value: String): RequestBuilder<T> {
        uri.appendQueryParameter(key, value)
        return this
    }

    /**
     * Sends the built request via the requester.
     */
    fun send() {
        requester.send(this)
    }

}
