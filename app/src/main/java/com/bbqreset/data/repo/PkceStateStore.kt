package com.bbqreset.data.repo

import android.content.Context
import com.bbqreset.data.api.SecurePrefs

class PkceStateStore(private val context: Context) {
    fun saveVerifier(verifier: String) {
        SecurePrefs.of(context).edit().putString(KEY_VERIFIER, verifier).apply()
    }

    fun consumeVerifier(): String? {
        val prefs = SecurePrefs.of(context)
        val value = prefs.getString(KEY_VERIFIER, null)
        prefs.edit().remove(KEY_VERIFIER).apply()
        return value
    }

    companion object {
        private const val KEY_VERIFIER = "pkce.verifier"
    }
}
