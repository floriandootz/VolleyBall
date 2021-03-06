package de.floriandootz.volleyball.request

enum class RequestStrategy {
    /**
     * Fallback chain: online -> cache -> raw-android-resource
     */
    ONLINE_FALLBACK_CACHE_FALLBACK_RESOURCE,

    /**
     * Fallback chain offline only: cache -> raw-android-resource
     */
    CACHE_FALLBACK_RESOURCE,

    /**
     * Only load online, no fallbacks, disable volley-caching.
     */
    ONLINE,

    /**
     * Use volley-caching as is. Do not disable it and don't do own caching.
     */
    VOLLEY_DEFAULT,

    /**
     * Combines online and offline for a fast response time in the UI.
     * First returns offline-data via fallback chain: cache -> raw-android-resource
     * Then loads and returns online-data
     */
    CACHE_FALLBACK_RESOURCE_AFTERWARDS_ONLINE;

    fun allowCache(): Boolean {
        return this != ONLINE
    }

    fun allowVolleyballCache(): Boolean {
        return this != ONLINE && this != VOLLEY_DEFAULT
    }

    fun allowResource(): Boolean {
        return this != ONLINE
    }

    fun allowOnline(): Boolean {
        return this != CACHE_FALLBACK_RESOURCE
    }

}
