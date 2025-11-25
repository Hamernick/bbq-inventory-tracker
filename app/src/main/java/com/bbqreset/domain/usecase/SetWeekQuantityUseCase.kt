package com.bbqreset.domain.usecase

import com.bbqreset.data.repo.WeekPlanRepository
import com.bbqreset.ui.vm.DayKey

class SetWeekQuantityUseCase(private val repo: WeekPlanRepository) {
    suspend operator fun invoke(
        locationId: Long,
        weekStart: String,
        itemId: Long,
        quantity: Int,
        day: DayKey?
    ) = repo.setQuantity(locationId, weekStart, itemId, quantity, day)
}
