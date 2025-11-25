package com.bbqreset.data.repo

import android.content.Context
import com.bbqreset.data.api.ApiClientProvider
import com.bbqreset.data.api.ApiConfig
import com.bbqreset.data.api.SecureMerchantIdProvider
import com.bbqreset.data.api.SecureTokenProvider
import com.bbqreset.data.db.dao.ItemDao
import com.bbqreset.data.db.dao.LocationDao
import com.bbqreset.data.db.entity.ItemEntity
import com.bbqreset.data.db.entity.LocationEntity

class CatalogRepository(
    private val context: Context,
    private val itemDao: ItemDao,
    private val locationDao: LocationDao,
    private val token: SecureTokenProvider,
    private val merchant: SecureMerchantIdProvider
) {
    suspend fun syncItemsToLocation(locationId: Long): Boolean {
        val tokenStr = token.getToken() ?: return false
        val merchantId = merchant.getMerchantId() ?: return false
        // Ensure location exists locally
        val loc = locationDao.getLocationById(locationId)
            ?: LocationEntity(id = locationId, name = "Sandbox Location", timeZoneId = "UTC").also {
                locationDao.upsert(it)
            }

        val api = ApiClientProvider.inventoryApi(
            baseUrl = ApiConfig.getBaseUrl(context),
            tokenProvider = token
        )
        val items = mutableListOf<ItemEntity>()
        var offset = 0
        val limit = 100
        var idxOffset = 0
        do {
            val page = api.listItems(merchantId = merchantId, limit = limit, offset = offset)
            items += page.elements.mapIndexed { idx, dto ->
                ItemEntity(
                    id = (idxOffset + idx + 1).toLong(),
                    cloverItemId = dto.id,
                    name = dto.name,
                    sku = dto.sku,
                    locationId = loc.id,
                    unitType = dto.unitName ?: "count"
                )
            }
            idxOffset = items.size
            val next = page.next
            offset = next
                ?.let { android.net.Uri.parse(it).getQueryParameter("offset")?.toIntOrNull() }
                ?: -1
        } while (offset >= 0)
        if (items.isNotEmpty()) itemDao.upsertAll(items)
        return items.isNotEmpty()
    }
}
