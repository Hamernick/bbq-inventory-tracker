package com.bbqreset.data.api

import android.content.Context

object ApiConfig {
    private const val KEY_BASE_URL = "api.baseUrl"
    const val DEFAULT_BASE_URL = "https://apisandbox.dev.clover.com"
    const val PROD_BASE_URL = "https://api.clover.com"
    const val AUTH_BASE_URL = "https://sandbox.dev.clover.com"
    const val DEFAULT_REDIRECT_URI = "https://hamernick.github.io/bbq-inventory-tracker/oauth/callback"

    fun getBaseUrl(context: Context): String =
        SecurePrefs.of(context).getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL

    fun setBaseUrl(context: Context, url: String?) {
        val value = url?.takeIf { it.isNotBlank() } ?: DEFAULT_BASE_URL
        SecurePrefs.of(context).edit().putString(KEY_BASE_URL, value).apply()
    }
}
