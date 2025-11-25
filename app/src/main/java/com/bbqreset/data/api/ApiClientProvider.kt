package com.bbqreset.data.api

import retrofit2.Retrofit

object ApiClientProvider {
    fun inventoryApi(
        baseUrl: String = ApiConfig.DEFAULT_BASE_URL,
        tokenProvider: TokenProvider = TokenProvider.Stub,
        logging: Boolean = false
    ): CloverInventoryApi = retrofit(baseUrl, tokenProvider, logging).create(CloverInventoryApi::class.java)

    fun merchantApi(
        baseUrl: String = ApiConfig.DEFAULT_BASE_URL,
        tokenProvider: TokenProvider = TokenProvider.Stub,
        logging: Boolean = false
    ): CloverMerchantApi = retrofit(baseUrl, tokenProvider, logging).create(CloverMerchantApi::class.java)

    fun ordersApi(
        baseUrl: String = ApiConfig.DEFAULT_BASE_URL,
        tokenProvider: TokenProvider = TokenProvider.Stub,
        logging: Boolean = false
    ): CloverOrdersApi = retrofit(baseUrl, tokenProvider, logging).create(CloverOrdersApi::class.java)

    fun authApi(
        baseUrl: String = ApiConfig.AUTH_BASE_URL,
        logging: Boolean = false
    ): AuthApi {
        val client = NetworkModule.okHttpClient(TokenProvider.Stub, enableLogging = logging)
        return NetworkModule.retrofit(baseUrl, client).create(AuthApi::class.java)
    }

    private fun retrofit(baseUrl: String, tokenProvider: TokenProvider, logging: Boolean): Retrofit {
        val client = NetworkModule.okHttpClient(tokenProvider, enableLogging = logging)
        return NetworkModule.retrofit(baseUrl, client)
    }
}
