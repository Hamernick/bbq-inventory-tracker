package com.bbqreset.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MerchantDto(
    @Json(name = "id") val id: String,
    @Json(name = "timezone") val timezone: String?
)
