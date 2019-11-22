package de.floriandootz.volleyball.request

enum class RequestStrategy {
    /**
     * Fallback chain: online -> cache -> raw-android-resource
     */
    ONLINE_FALLBACK_CACHE_FALLBACK_RESOURCE,

    /**
     * Fallback chain offline: cache -> raw-android-resource
     */
    CACHE_FALLBACK_RESOURCE,

    /**
     * Only load online, no fallbacks
     */
    ONLINE,

    /**
     * Combines online and offline for a fast response time in the UI.
     * First returns offline-data via fallback chain: cache -> raw-android-resource
     * Then loads and returns online-data
     */
    CACHE_FALLBACK_RESOURCE_AFTERWARDS_ONLINE
}
