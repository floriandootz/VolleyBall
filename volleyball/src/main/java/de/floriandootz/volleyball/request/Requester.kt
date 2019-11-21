package de.floriandootz.volleyball.request

import android.content.Context
import android.support.annotation.RawRes
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BaseHttpStack
import com.android.volley.toolbox.Volley
import de.floriandootz.volleyball.parse.Parser
import de.floriandootz.volleyball.util.CachingUtil
import de.floriandootz.volleyball.util.CachingUtil.writeCache
import de.floriandootz.volleyball.util.NetworkUtil
import com.android.volley.Request as VolleyRequest

class Requester : RequestQueue.RequestFinishedListener<Any> {

    private val ctx: Context
    private var requestQueue: RequestQueue
    private var cachingIsActive = false
    private var headers: Map<String, String>? = null

    constructor(ctx: Context, activateCaching: Boolean = false, headers: Map<String, String>?, httpStack: BaseHttpStack?) {
        this.ctx = ctx
        this.headers = headers
        cachingIsActive = activateCaching
        requestQueue = Volley.newRequestQueue(ctx, httpStack)
        requestQueue.addRequestFinishedListener<Any>(this)
    }

    fun <T> build(url: String, parser: Parser<T>): RequestBuilder<T> {
        return RequestBuilder(this, url, parser)
    }

    fun <T> send(builder: RequestBuilder<T>) {
        this.request(
            builder.method,
            builder.url,
            builder.body,
            builder.doReloadIfOnline,
            builder.listener,
            builder.errorListener,
            builder.resId,
            builder.parser
        )
    }

    /**
     * Executes request and returns the parsed answer within the callback function.
     *
     * @param url The url to request
     * @param clazz The class to parse the answer into. Alternatively you can supply a @customParser.
     * @param forceReloadIfOnline If true, the cached data will be returned if online.
     * @param listener The callback for the parsed result data.
     * @param errorListener The callback for any (http, parsing, ...) error.
     * @param rawFallbackRes The resource contained in the APK to be loaded if offline and no cache available. Useful for first starts of the application.
     * @param customParser A custom parser instead of @clazz.
     * @param <T> The class you are parsing to.
    </T> */
    private fun <T> request(
        method: Int,
        url: String,
        body: String?,
        forceReloadIfOnline: Boolean,
        listener: Response.Listener<T>?,
        errorListener: Response.ErrorListener?,
        @RawRes rawFallbackRes: Int?,
        customParser: Parser<T>
    ) {
        Log.d(this.javaClass.simpleName, "Loading: $url")
        if (!NetworkUtil.isNetworkAvailable(ctx) && !CachingUtil.cacheExists(ctx, url) && rawFallbackRes != null) { // load fallback within apk
            val fallbackResponse: T = CachingUtil.readRawAndroidResource(ctx, rawFallbackRes, customParser)
            Log.d(this.javaClass.simpleName, "Loaded apk-fallback!")
            listener?.onResponse(fallbackResponse)
        } else if (cachingIsActive && (!forceReloadIfOnline || !NetworkUtil.isNetworkAvailable(ctx)) && CachingUtil.cacheExists(ctx, url)) { // load from cache
            val cachedResponse: T? = CachingUtil.readCache(ctx, url, customParser)
            Log.d(this.javaClass.simpleName, "Loaded from cache!")
            listener?.onResponse(cachedResponse)
        } else { // load from API
            val request: Request<T> = Request(method, url, customParser, body, headers, listener, errorListener)
            requestQueue.add(request)
        }
    }

    override fun onRequestFinished(volleyRequest: VolleyRequest<Any?>) {
        if (volleyRequest is Request) { // should always be true
            val request: Request<Any?> = volleyRequest as Request
            val responseJsonString: String? = request.getResponseJsonString()
            //Log.d(this.getClass().getSimpleName(), "received: " + responseJsonString);
            if (responseJsonString != null && !request.hasError()) {
                Log.d(this.javaClass.simpleName, "Loaded: " + request.url)
                if (cachingIsActive) {
                    writeCache(ctx, responseJsonString, request.url)
                    //SharedPreferencesManager.writeFileTimestamp(ctx, request.getUrl(), System.currentTimeMillis());
                }
            } else {
                Log.d(this.javaClass.simpleName, "Loading failed: " + request.url)
                // no connection, timeout, server offline etc...
                // fake successful loading by providing cache if possible
                if (cachingIsActive && CachingUtil.cacheExists(ctx, request.url)) {
                    request.deliverResponse(CachingUtil.readCache(ctx, request.url, request.getCustomParser()))
                } else { // no more options
                    request.deliverErrorForReal()
                }
            }
        }
    }

}
