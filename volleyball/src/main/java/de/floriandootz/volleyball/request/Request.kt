package de.floriandootz.volleyball.request

import android.util.Log
import com.android.volley.*
import com.android.volley.Request
import com.android.volley.toolbox.HttpHeaderParser
import de.floriandootz.volleyball.parse.Parser
import java.io.UnsupportedEncodingException

class Request<T> : Request<T> {

    private val parser: Parser<T>
    private val listener: Response.Listener<T>
    private val headers: Map<String, String>?
    private val body: String?
    private var responseJsonString: String? = null
    private var errorOnHold : VolleyError? = null // Only throw the error when cache can't solve it

    constructor(
        method: Int,
        url: String,
        parser: Parser<T>,
        body: String?,
        headers: Map<String, String>?,
        responseListener: Response.Listener<T>,
        errorListener: Response.ErrorListener
    ) : super(
        method,
        url,
        errorListener
    ) {
        this.parser = parser
        this.listener = responseListener
        this.headers = headers
        this.body = body
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<T> {
        val responseString = String(response.data, charset("utf-8"))
        responseJsonString = responseString

        val parsedData: T = parser.parse(responseString, response.headers)

        return Response.success(parsedData, HttpHeaderParser.parseCacheHeaders(response))
    }

    public override fun deliverResponse(response: T) {
        listener.onResponse(response)
    }

    override fun getHeaders(): Map<String, String> {
        return headers ?: super.getHeaders()
    }

    override fun deliverError(error: VolleyError?) {
        errorOnHold = error
    }

    override fun getBodyContentType(): String? {
        return if (body != null) "application/json" else super.getBodyContentType()
    }

    @Throws(AuthFailureError::class)
    override fun getBody(): ByteArray? {
        if (body != null) {
            try {
                return body.toByteArray(charset(paramsEncoding))
            } catch (e: UnsupportedEncodingException) {
                Log.d(this.javaClass.simpleName, e.localizedMessage)
            }
        }
        return super.getBody()
    }

    fun deliverErrorForReal() {
        super.deliverError(errorOnHold)
    }

    fun getResponseJsonString(): String? {
        return responseJsonString
    }

    fun getCustomParser(): Parser<T> {
        return parser
    }

    fun hasError(): Boolean {
        return errorOnHold != null
    }

}