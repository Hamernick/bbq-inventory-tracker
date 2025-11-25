package com.bbqreset.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bbqreset.core.di.DatabaseModule
import com.bbqreset.data.api.ApiConfig
import com.bbqreset.data.api.SecureMerchantIdProvider
import com.bbqreset.data.api.SecureTokenProvider
import com.bbqreset.data.db.seedDebug
import com.bbqreset.data.repo.CatalogRepository
import com.bbqreset.domain.usecase.SyncCatalogUseCase
import java.time.Clock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant
import java.time.ZoneId

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val clock = Clock.systemUTC()
    private val db by lazy { DatabaseModule.provideAppDatabase(app.applicationContext, clock) }
    private val token by lazy { SecureTokenProvider(app.applicationContext) }
    private val merchant by lazy { SecureMerchantIdProvider(app.applicationContext) }
    private val catalog by lazy { CatalogRepository(app.applicationContext, db.itemDao(), db.locationDao(), token, merchant) }
    private val sync by lazy { SyncCatalogUseCase(catalog) }
    private val _logs = MutableStateFlow<List<InventoryLog>>(emptyList())
    val logs: StateFlow<List<InventoryLog>> = _logs.asStateFlow()

    val baseUrl: String get() = ApiConfig.getBaseUrl(getApplication())

    fun currentToken(): String? = token.getToken()
    fun saveToken(value: String?) { token.setToken(value?.trim().takeUnless { it.isNullOrEmpty() }) }
    fun clearToken() { token.setToken(null) }

    fun currentMerchant(): String? = merchant.getMerchantId()
    fun saveMerchant(value: String?) { merchant.setMerchantId(value?.trim().takeUnless { it.isNullOrEmpty() }) }
    fun clearMerchant() { merchant.setMerchantId(null) }

    fun saveBaseUrl(value: String?) { ApiConfig.setBaseUrl(getApplication(), value) }
    fun resetBaseUrl() { ApiConfig.setBaseUrl(getApplication(), ApiConfig.DEFAULT_BASE_URL) }

    fun seedSample() {
        viewModelScope.launch(Dispatchers.IO) { db.seedDebug(clock) }
    }

    fun syncCatalog(locationId: Long = 1L) {
        viewModelScope.launch(Dispatchers.IO) { sync(locationId) }
    }

    fun currentLocationName(): String {
        return runCatching {
            runBlocking { db.locationDao().getLocationById(1L)?.name ?: "Location 1" }
        }.getOrDefault("Location 1")
    }

    fun loadLogs(limit: Int = 50) {
        viewModelScope.launch(Dispatchers.IO) {
            val rows = db.logDao().pageLogs(limit = limit, offset = 0)
            _logs.value = rows.map {
                InventoryLog(
                    ts = Instant.ofEpochSecond(it.ts).atZone(ZoneId.systemDefault()).toString(),
                    action = it.action,
                    meta = it.metaJson
                )
            }
        }
    }
}

data class InventoryLog(
    val ts: String,
    val action: String,
    val meta: String
)
