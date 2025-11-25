package com.bbqreset.data.repo

import android.net.Uri
import com.bbqreset.core.auth.Pkce
import com.bbqreset.data.api.ApiConfig
import com.bbqreset.data.api.AuthApi
import com.bbqreset.data.api.SecureMerchantIdProvider
import com.bbqreset.data.api.SecureTokenProvider

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenProvider: SecureTokenProvider,
    private val merchantProvider: SecureMerchantIdProvider,
    private val pkceStore: PkceStateStore
    ) {

    fun buildAuthUrl(
        clientId: String,
        redirectUri: String,
        scopes: List<String>
    ): AuthRequest {
        val verifier = Pkce.generateVerifier()
        val challenge = Pkce.challenge(verifier)
        pkceStore.saveVerifier(verifier)
        val uri = Uri.parse("${ApiConfig.AUTH_BASE_URL}/oauth/authorize")
            .buildUpon()
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("redirect_uri", redirectUri)
            .appendQueryParameter("scope", scopes.joinToString(" "))
            .appendQueryParameter("code_challenge", challenge)
            .appendQueryParameter("code_challenge_method", "S256")
            .build()
        return AuthRequest(uri.toString(), verifier)
    }

    suspend fun handleCallback(
        clientId: String,
        code: String,
        redirectUri: String
    ): Boolean {
        val verifier = pkceStore.consumeVerifier() ?: return false
        val response = authApi.exchangeToken(
            clientId = clientId,
            code = code,
            codeVerifier = verifier,
            redirectUri = redirectUri
        )
        val access = response.accessToken ?: return false
        tokenProvider.setToken(access)
        response.merchantId?.let { merchantProvider.setMerchantId(it) }
        response.refreshToken?.let { tokenProvider.setRefreshToken(it) }
        return true
    }

    suspend fun refresh(clientId: String): Boolean {
        val refresh = tokenProvider.getRefreshToken() ?: return false
        val response = authApi.refreshToken(clientId = clientId, refreshToken = refresh)
        val access = response.accessToken ?: return false
        tokenProvider.setToken(access)
        response.refreshToken?.let { tokenProvider.setRefreshToken(it) }
        return true
    }
}

data class AuthRequest(
    val url: String,
    val verifier: String
)
