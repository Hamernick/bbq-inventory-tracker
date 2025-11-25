package com.bbqreset.ui.vm

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bbqreset.data.api.ApiClientProvider
import com.bbqreset.data.api.ApiConfig
import com.bbqreset.data.api.SecureMerchantIdProvider
import com.bbqreset.data.api.SecureTokenProvider
import com.bbqreset.data.repo.AuthRepository
import com.bbqreset.data.repo.PkceStateStore
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val authUrl: String? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val tokenProvider = SecureTokenProvider(app.applicationContext)
    private val merchantProvider = SecureMerchantIdProvider(app.applicationContext)
    private val pkceStore = PkceStateStore(app.applicationContext)
    private val authRepo = AuthRepository(
        authApi = ApiClientProvider.authApi(ApiConfig.AUTH_BASE_URL),
        tokenProvider = tokenProvider,
        merchantProvider = merchantProvider,
        pkceStore = pkceStore
    )

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()
    private val handled = AtomicBoolean(false)

    fun startAuth(clientId: String, redirectUri: String, scopes: List<String>) {
        val request = authRepo.buildAuthUrl(clientId, redirectUri, scopes)
        _state.value = _state.value.copy(authUrl = request.url, error = null)
    }

    fun handleCallback(clientId: String, uri: Uri?, redirectUri: String) {
        if (uri == null) return
        if (!handled.compareAndSet(false, true)) return
        val code = uri.getQueryParameter("code") ?: run {
            _state.value = _state.value.copy(error = "Missing code")
            return
        }
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            val ok = runCatching { authRepo.handleCallback(clientId, code, redirectUri) }.getOrElse {
                _state.value = _state.value.copy(loading = false, error = it.message)
                return@launch
            }
            _state.value = _state.value.copy(loading = false, success = ok, error = if (ok) null else "Auth failed")
        }
    }
}
