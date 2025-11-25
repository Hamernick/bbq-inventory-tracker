package com.bbqreset.data.api

import android.content.Context

class SecureTokenProvider(private val context: Context) : TokenProvider {
    override fun getToken(): String? =
        SecurePrefs.of(context).getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? =
        SecurePrefs.of(context).getString(KEY_REFRESH_TOKEN, null)

    fun setToken(token: String?) {
        SecurePrefs.of(context).edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    fun setRefreshToken(token: String?) {
        SecurePrefs.of(context).edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }

    companion object {
        const val KEY_ACCESS_TOKEN = "auth.accessToken"
        const val KEY_REFRESH_TOKEN = "auth.refreshToken"
    }
}

class SecureMerchantIdProvider(private val context: Context) : MerchantIdProvider {
    override fun getMerchantId(): String? =
        SecurePrefs.of(context).getString(KEY_MERCHANT_ID, null)

    fun setMerchantId(id: String?) {
        SecurePrefs.of(context).edit().putString(KEY_MERCHANT_ID, id).apply()
    }

    companion object { const val KEY_MERCHANT_ID = "merchant.id" }
}
