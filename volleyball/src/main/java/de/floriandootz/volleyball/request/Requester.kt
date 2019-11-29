package de.floriandootz.volleyball.request

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.Request as VolleyRequest
import com.android.volley.toolbox.BaseHttpStack
import com.android.volley.toolbox.Volley
import de.floriandootz.volleyball.parse.Parser
import de.floriandootz.volleyball.util.CachingUtil
import de.floriandootz.volleyball.util.CachingUtil.writeCache
import de.floriandootz.volleyball.util.LogUtil
import de.floriandootz.volleyball.util.NetworkUtil

class Requester : RequestQueue.RequestFinishedListener<Any> {

    private val ctx: Context
    private var requestQueue: RequestQueue
    private var headers: Map<String, String>? = null

    constructor(ctx: Context, headers: Map<String, String>?, httpStack: BaseHttpStack?) {
        this.ctx = ctx
        this.headers = headers
        requestQueue = Volley.newRequestQueue(ctx, httpStack)
        requestQueue.addRequestFinishedListener<Any>(this)
    }

    fun <T> build(url: String, parser: Parser<T>): RequestBuilder<T> {
        return RequestBuilder(this, url, parser)
    }

    fun <T> send(builder: RequestBuilder<T>) {
        // Load fallback within apk
        if (builder.requestStrategy != RequestStrategy.ONLINE && !NetworkUtil.isNetworkAvailable(ctx) && !CachingUtil.cacheExists(ctx, builder.url) && builder.rawAndroidResource != null) {
            val fallbackResponse: T = CachingUtil.readRawAndroidResource(ctx, builder.rawAndroidResource!!, builder.parser)
            LogUtil.d("Loaded raw-res-fallback: ${builder.url}")
            builder.listener?.onResponse(fallbackResponse)
        }
        // Load from cache
        else if (builder.requestStrategy != RequestStrategy.ONLINE && (!builder.doReloadIfOnline || !NetworkUtil.isNetworkAvailable(ctx)) && CachingUtil.cacheExists(ctx, builder.url)) {
            val cachedResponse: T? = CachingUtil.readCache(ctx, builder.url, builder.parser)
            LogUtil.d("Loaded from cache: ${builder.url}")
            builder.listener?.onResponse(cachedResponse)
        }
        // Load from API
        else {
            LogUtil.v("Loading from interwebz: ${builder.url}")
            val request: Request<T> = Request(
                builder.method,
                builder.url,
                builder.parser,
                builder.body,
                headers,
                builder.listener,
                builder.errorListener,
                builder.requestStrategy,
                builder.rawAndroidResource
            )
            if (builder.requestStrategy == RequestStrategy.ONLINE)
                request.setShouldCache(false)
            requestQueue.add(request)
        }
    }

    override fun onRequestFinished(request: VolleyRequest<Any?>) {
        // Should always be true
        if (request is Request<Any?>) {
            val responseJsonString: String? = request.responseJsonString
            // Request was successful
            if (responseJsonString != null && !request.hasError()) {
                LogUtil.d("Loaded from interwebz: ${request.url}")
                writeCache(ctx, responseJsonString, request.url)
                //SharedPreferencesManager.writeFileTimestamp(ctx, request.getUrl(), System.currentTimeMillis());
            // No connection, timeout, server offline etc...
            } else {
                // Fake successful loading by providing cache if possible
                if (request.requestStrategy != RequestStrategy.ONLINE && CachingUtil.cacheExists(ctx, request.url)) {
                    LogUtil.w("Loading from interwebz failed; Loaded from cache: ${request.url}")
                    request.deliverResponse(CachingUtil.readCache(ctx, request.url, request.parser))
                }
                // Fallback to raw-android-resource if set
                else if (request.requestStrategy != RequestStrategy.ONLINE && request.rawAndroidResource != null) {
                    LogUtil.w("Loading from interwebz failed; Using raw-android-resource for: ${request.url}")
                    val parsedRawRes = CachingUtil.readRawAndroidResource(ctx, request.rawAndroidResource, request.parser)
                    request.deliverResponse(parsedRawRes)
                }
                // Out of options, request fails
                else {
                    LogUtil.w("Loading from interwebz failed; No cache or raw-android-resource available for: ${request.url}")
                    request.deliverErrorForReal()
                }
            }
        }
    }

}
