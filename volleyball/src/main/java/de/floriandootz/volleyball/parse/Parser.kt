package de.floriandootz.volleyball.parse

interface Parser<T> {
    /**
     * Parses the request answer from a json-string to a model instance
     * @param jsonString The json-string
     * @param headers The headers. Warning, this is null if the data comes from the cache!
     * @return The model instance
     */
    fun parse(jsonString: String, headers: Map<String, String>?): T
}
