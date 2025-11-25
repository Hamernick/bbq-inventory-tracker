package com.bbqreset.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ItemDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "sku") val sku: String?,
    @Json(name = "unitName") val unitName: String? = null
)

@JsonClass(generateAdapter = true)
data class ItemsResponseDto(
    @Json(name = "elements") val elements: List<ItemDto> = emptyList(),
    @Json(name = "href") val href: String? = null,
    @Json(name = "next") val next: String? = null
)

@JsonClass(generateAdapter = true)
data class StockUpdateRequest(
    @Json(name = "quantity") val quantity: Int
)

@JsonClass(generateAdapter = true)
data class StockUpdateResponse(
    @Json(name = "status") val status: String? = null
)
