package com.bbqreset.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderDto(
    @Json(name = "id") val id: String,
    @Json(name = "modifiedTime") val modifiedTime: Long?
)

@JsonClass(generateAdapter = true)
data class OrdersResponseDto(
    @Json(name = "elements") val elements: List<OrderDto> = emptyList(),
    @Json(name = "href") val href: String? = null,
    @Json(name = "next") val next: String? = null
)
