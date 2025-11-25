package com.bbqreset.data.api

import com.bbqreset.data.api.dto.OrdersResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CloverOrdersApi {
    @GET("/v3/merchants/{merchantId}/orders")
    suspend fun listOrders(
        @Path("merchantId") merchantId: String,
        @Query("filter") filter: String? = null,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): OrdersResponseDto
}
