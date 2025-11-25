package com.bbqreset.data.api

import com.bbqreset.data.api.dto.MerchantDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CloverMerchantApi {
    @GET("/v3/merchants/{merchantId}")
    suspend fun getMerchant(@Path("merchantId") merchantId: String): MerchantDto
}
