package com.bbqreset.data.api

import com.bbqreset.data.api.dto.ItemsResponseDto
import com.bbqreset.data.api.dto.StockUpdateRequest
import com.bbqreset.data.api.dto.StockUpdateResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CloverInventoryApi {
    @GET("/v3/merchants/{merchantId}/items")
    suspend fun listItems(
        @Path("merchantId") merchantId: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): ItemsResponseDto

    // Placeholder endpoint for stock update; exact Clover path may differ.
    @POST("/v3/merchants/{merchantId}/inventory/stock/{itemId}")
    suspend fun updateStock(
        @Path("merchantId") merchantId: String,
        @Path("itemId") itemId: String,
        @Body body: StockUpdateRequest,
        @Header("Idempotency-Key") idempotencyKey: String
    ): StockUpdateResponse
}
