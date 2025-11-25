package com.bbqreset.domain.usecase

import com.bbqreset.data.repo.WeekPlanRepository

class LoadWeekPlanUseCase(private val repo: WeekPlanRepository) {
    suspend operator fun invoke(locationId: Long, weekStart: String) = repo.list(locationId, weekStart)
}
