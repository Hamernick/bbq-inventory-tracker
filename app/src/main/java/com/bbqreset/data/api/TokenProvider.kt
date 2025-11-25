package com.bbqreset.data.api

interface TokenProvider {
    fun getToken(): String?

    object Stub : TokenProvider {
        override fun getToken(): String? = null
    }
}
