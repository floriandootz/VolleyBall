package de.floriandootz.volleyball.parse

/**
 * Does no parsing but returns null. Useful when the response must succeed, but the content does not matter.
 */
class VoidParser : Parser<Void?> {

    override fun parse(jsonString: String, headers: Map<String, String>?): Void? {
        return null
    }

}
