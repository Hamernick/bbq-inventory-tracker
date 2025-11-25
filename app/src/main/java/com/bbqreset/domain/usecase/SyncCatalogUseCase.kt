package com.bbqreset.domain.usecase

import com.bbqreset.data.repo.CatalogRepository

class SyncCatalogUseCase(private val repo: CatalogRepository) {
    suspend operator fun invoke(locationId: Long): Boolean = repo.syncItemsToLocation(locationId)
}

