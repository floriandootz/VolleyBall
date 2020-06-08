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

    /**
     * Primary function for using volleyball. Use this to create and send a new request.
     * @param url The url to request
     * @param parser Either a custom parser by implementing {@link Parser} or a parser from the volleyball-library:
     * - ClassParser
     * - ArrayParser
     * - StringParser
     * - VoidParser
     */
    fun <T> build(url: String, parser: Parser<T>): RequestBuilder<T> {
        return RequestBuilder(this, url, parser)
    }

    /**
     * It is not recommended to use this. Instead use: .build().send()
     */
    fun <T> send(builder: RequestBuilder<T>) {
        var pathFoundForRequestStrategy = false

        // Load fallback within apk
        if (builder.requestStrategy.allowResource() &&
            (!NetworkUtil.isNetworkAvailable(ctx) || !builder.requestStrategy.allowOnline() || builder.requestStrategy == RequestStrategy.CACHE_FALLBACK_RESOURCE_AFTERWARDS_ONLINE) &&
            !CachingUtil.cacheExists(ctx, builder.url) && builder.rawAndroidResource != null) {
            val fallbackResponse: T = CachingUtil.readRawAndroidResource(ctx, builder.rawAndroidResource!!, builder.parser)
            LogUtil.d("Loaded raw-res-fallback: ${builder.url}")
            builder.listener?.onResponse(fallbackResponse)
            pathFoundForRequestStrategy = true
        }
        // Load from cache
        else if (builder.requestStrategy.allowCache() &&
            (!NetworkUtil.isNetworkAvailable(ctx) || !builder.requestStrategy.allowOnline() || builder.requestStrategy == RequestStrategy.CACHE_FALLBACK_RESOURCE_AFTERWARDS_ONLINE) &&
            CachingUtil.cacheExists(ctx, builder.url)) {
            val cachedResponse: T? = CachingUtil.readCache(ctx, builder.url, builder.parser)
            LogUtil.d("Loaded from cache: ${builder.url}")
            builder.listener?.onResponse(cachedResponse)
            pathFoundForRequestStrategy = true
        }

        // Load from API
        if (builder.requestStrategy.allowOnline() &&
            (!pathFoundForRequestStrategy || builder.requestStrategy == RequestStrategy.CACHE_FALLBACK_RESOURCE_AFTERWARDS_ONLINE)) {
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
            if (builder.requestStrategy.allowCache())
                request.setShouldCache(false)
            requestQueue.add(request)
            pathFoundForRequestStrategy = true
        }

        if (!pathFoundForRequestStrategy) {
            LogUtil.d("No way found to retrieve the content for request-strategy. Example: Strategy set to only use cache, but cache not available.")
        }
    }

    /**
     * Internal function. Do not use.
     */
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
                if (request.requestStrategy.allowCache() && CachingUtil.cacheExists(ctx, request.url)) {
                    LogUtil.w("Loading from interwebz failed; Loaded from cache: ${request.url}")
                    request.deliverResponse(CachingUtil.readCache(ctx, request.url, request.parser))
                }
                // Fallback to raw-android-resource if set
                else if (request.requestStrategy.allowResource() && request.rawAndroidResource != null) {
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
