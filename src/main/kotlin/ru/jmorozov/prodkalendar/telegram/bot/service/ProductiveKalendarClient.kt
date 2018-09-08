package ru.jmorozov.prodkalendar.telegram.bot.service

import ru.jmorozov.prodkalendar.telegram.dto.DateRange
import ru.jmorozov.prodkalendar.telegram.dto.DayType
import java.time.LocalDate

interface ProductiveKalendarClient {
    fun getHolidaysBetween(range: DateRange): String?

    fun getWorkdaysBetween(range: DateRange): String?

    fun isHoliday(date: LocalDate): Boolean?
    fun isHolidayTomorrow(): Boolean?

    fun getDayType(date: LocalDate): DayType?
    fun getTomorrowType(): DayType?
}