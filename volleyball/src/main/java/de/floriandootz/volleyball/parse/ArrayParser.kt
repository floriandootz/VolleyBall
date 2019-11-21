package de.floriandootz.volleyball.parse

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class ArrayParser<T> : Parser<List<T>> {

    private val clazz: Class<T>

    constructor(clazz: Class<T>) {
        this.clazz = clazz
    }

    override fun parse(jsonString: String, headers: Map<String, String>?): List<T> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
        return gson.fromJson<List<T>>(
            jsonString, TypeToken.getParameterized(
                MutableList::class.java,
                clazz
            ).type
        )
    }

}
