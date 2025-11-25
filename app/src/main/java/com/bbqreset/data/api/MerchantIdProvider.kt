package com.bbqreset.data.api

interface MerchantIdProvider {
    fun getMerchantId(): String?

    object Stub : MerchantIdProvider {
        override fun getMerchantId(): String? = null
    }
}
