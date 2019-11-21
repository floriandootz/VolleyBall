package de.floriandootz.volleyball.parse

import com.google.gson.GsonBuilder

class ClassParser<T> : Parser<T> {

    private val clazz: Class<T>

    constructor(clazz: Class<T>) {
        this.clazz = clazz
    }

    override fun parse(jsonString: String, headers: Map<String, String>?): T {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
        return gson.fromJson(jsonString, clazz)
    }

}
