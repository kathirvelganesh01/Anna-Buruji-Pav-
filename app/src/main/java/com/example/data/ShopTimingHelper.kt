package com.example.data

import java.util.Calendar
import java.util.Locale

data class StallTimingStatus(
    val isOpen: Boolean,
    val statusText: String,
    val timeSubtitle: String,
    val isPeakHours: Boolean
)

object ShopTimingHelper {
    // 8:30 PM (20:30) to 12:30 AM (00:30)
    const val OPEN_HOUR = 20
    const val OPEN_MINUTE = 30
    const val CLOSE_HOUR = 0
    const val CLOSE_MINUTE = 30

    fun calculateStallStatus(manualOverride: Boolean? = null): StallTimingStatus {
        if (manualOverride != null) {
            return if (manualOverride) {
                StallTimingStatus(
                    isOpen = true,
                    statusText = "OPEN NOW (Manager Mode)",
                    timeSubtitle = "Live Tawa Active • 8:30 PM - 12:30 AM",
                    isPeakHours = true
                )
            } else {
                StallTimingStatus(
                    isOpen = false,
                    statusText = "CLOSED TEMPORARILY",
                    timeSubtitle = "Stall opens at 8:30 PM • Shell Colony",
                    isPeakHours = false
                )
            }
        }

        val now = Calendar.getInstance()
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)

        val currentMinutesOfDay = hour * 60 + minute
        val openMinutesOfDay = OPEN_HOUR * 60 + OPEN_MINUTE // 1230 minutes (20:30)
        val closeMinutesOfDay = CLOSE_HOUR * 60 + CLOSE_MINUTE // 30 minutes (00:30)

        val isOpen = (currentMinutesOfDay >= openMinutesOfDay) || (currentMinutesOfDay < closeMinutesOfDay)
        val isPeakHours = (currentMinutesOfDay >= (21 * 60)) || (currentMinutesOfDay < 0) // 9:00 PM - 12:00 AM

        return if (isOpen) {
            StallTimingStatus(
                isOpen = true,
                statusText = "OPEN NOW • Sizzling on Tawa",
                timeSubtitle = "Shop Hours: 8:30 PM to 12:30 AM",
                isPeakHours = isPeakHours
            )
        } else {
            val minutesUntilOpen = if (currentMinutesOfDay < openMinutesOfDay) {
                openMinutesOfDay - currentMinutesOfDay
            } else {
                (24 * 60 - currentMinutesOfDay) + openMinutesOfDay
            }
            val hoursLeft = minutesUntilOpen / 60
            val minsLeft = minutesUntilOpen % 60

            val countdownText = if (hoursLeft > 0) "${hoursLeft}h ${minsLeft}m" else "${minsLeft}m"
            StallTimingStatus(
                isOpen = false,
                statusText = "OPENS AT 8:30 PM",
                timeSubtitle = "Opens in $countdownText • Night Street Stall",
                isPeakHours = false
            )
        }
    }
}
