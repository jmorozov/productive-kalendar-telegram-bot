package ru.jmorozov.prodkalendar.telegram.bot.service

import java.time.LocalDate
import ru.jmorozov.prodkalendar.telegram.dto.DateRange

interface ProductiveKalendarClient {
    fun getHolidaysBetween(range: DateRange): String?

    fun getWorkdaysBetween(range: DateRange): String?

    fun isHoliday(date: LocalDate): Boolean?
}