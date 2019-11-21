package de.floriandootz.volleyballexample

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.VolleyError
import de.floriandootz.volleyball.parse.Parser
import de.floriandootz.volleyball.parse.StringParser
import de.floriandootz.volleyball.request.Requester

class MainActivity : Activity(), Parser<String>, Response.Listener<String>, Response.ErrorListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val requester = Requester(this, true, null, null)

        requester
            .build(
                "https://fonts.googleapis.com/css?family=Roboto:100,100i,300,300i,400,400i,500,500i,700,700i,900,900i&display=swap",
                StringParser())
            .setListener(this)
            .send()
    }

    override fun parse(jsonString: String, headers: Map<String, String>?): String {
        return jsonString
    }

    override fun onResponse(response: String) {
        findViewById<TextView>(R.id.text).text = response
    }

    override fun onErrorResponse(error: VolleyError?) {

    }

}
