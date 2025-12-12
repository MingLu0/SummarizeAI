package com.nutshell.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class to check server availability with caching.
 * Used to determine which API server (local or fallback) to use for V4 streaming.
 */
@Singleton
class ServerAvailabilityChecker @Inject constructor(
    private val okHttpClient: OkHttpClient,
) {
    private val cache = mutableMapOf<String, CachedAvailability>()
    private val cacheTtlMs = 5 * 60 * 1000L // 5 minutes

    companion object {
        private const val TAG = "ServerAvailabilityChecker"
        private const val DEFAULT_TIMEOUT_MS = 3000L
        private const val HEALTH_ENDPOINT = "/health"
    }

    data class CachedAvailability(
        val isAvailable: Boolean,
        val timestamp: Long,
    )

    /**
     * Checks if a server is available by sending a HEAD request to its health endpoint.
     * Results are cached for 5 minutes to avoid repeated network calls.
     *
     * @param baseUrl The base URL of the server to check (e.g., "http://192.168.88.12:7860")
     * @param timeoutMs Timeout in milliseconds for the health check (default: 3000ms)
     * @return true if server is available, false otherwise
     */
    suspend fun checkServerAvailability(
        baseUrl: String,
        timeoutMs: Long = DEFAULT_TIMEOUT_MS,
    ): Boolean {
        // Check cache first
        cache[baseUrl]?.let { cached ->
            if (System.currentTimeMillis() - cached.timestamp < cacheTtlMs) {
                Log.d(TAG, "Using cached availability for $baseUrl: ${cached.isAvailable}")
                return cached.isAvailable
            }
        }

        // Perform actual health check
        return try {
            val isAvailable = performHealthCheck(baseUrl, timeoutMs)
            cache[baseUrl] = CachedAvailability(isAvailable, System.currentTimeMillis())
            Log.d(TAG, "Server $baseUrl availability: $isAvailable")
            isAvailable
        } catch (e: Exception) {
            Log.e(TAG, "Server check failed for $baseUrl: ${e.message}")
            val isAvailable = false
            cache[baseUrl] = CachedAvailability(isAvailable, System.currentTimeMillis())
            isAvailable
        }
    }

    /**
     * Performs the actual health check by sending a GET request.
     */
    private suspend fun performHealthCheck(baseUrl: String, timeoutMs: Long): Boolean {
        return withContext(Dispatchers.IO) {
            val client = okHttpClient.newBuilder()
                .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .build()

            val request = Request.Builder()
                .url("$baseUrl$HEALTH_ENDPOINT")
                .get()
                .build()

            val response = client.newCall(request).execute()
            response.use { it.isSuccessful }
        }
    }

    /**
     * Clears the availability cache for a specific server.
     * Useful for forcing a re-check.
     */
    fun clearCache(baseUrl: String) {
        cache.remove(baseUrl)
        Log.d(TAG, "Cleared cache for $baseUrl")
    }

    /**
     * Clears the entire availability cache.
     */
    fun clearAllCache() {
        cache.clear()
        Log.d(TAG, "Cleared all cache")
    }
}
