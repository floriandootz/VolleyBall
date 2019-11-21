package de.floriandootz.volleyball.parse

/**
 * Just returns the received request-body as string.
 */
class StringParser : Parser<String> {

    override fun parse(jsonString: String, headers: Map<String, String>?): String {
        return jsonString
    }

}
