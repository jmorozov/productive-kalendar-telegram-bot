package ru.jmorozov.prodkalendar.telegram.bot.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import ru.jmorozov.prodkalendar.telegram.bot.service.ProductiveKalendarClient
import ru.jmorozov.prodkalendar.telegram.dto.DateRange
import ru.jmorozov.prodkalendar.telegram.dto.DayType
import java.time.LocalDate

@Service
class ProductiveKalendarClientImpl @Autowired constructor(
    @Value("\${productive.kalendar.host}") val productiveKalendarHost: String
) : ProductiveKalendarClient {

    private companion object {
        val rest = RestTemplate()
    }

    override fun getHolidaysBetween(range: DateRange): String? =
            rest.postForObject("$productiveKalendarHost/api/query/holidays/between", range, String::class.java)

    override fun getWorkdaysBetween(range: DateRange): String? =
            rest.postForObject("$productiveKalendarHost/api/query/workdays/between", range, String::class.java)

    override fun isHoliday(date: LocalDate): Boolean? =
            rest.getForObject("$productiveKalendarHost/api/query/is/$date/holiday")

    override fun isHolidayTomorrow(): Boolean? =
            rest.getForObject("$productiveKalendarHost/api/query/is/tomorrow/holiday")

    override fun getDayType(date: LocalDate): DayType? =
            rest.getForObject("$productiveKalendarHost/api/query/day/$date/type")

    override fun getTomorrowType(): DayType? =
            rest.getForObject("$productiveKalendarHost/api/query/day/tomorrow/type")
}