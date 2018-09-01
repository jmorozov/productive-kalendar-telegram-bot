package ru.jmorozov.prodkalendar.telegram.bot.service.impl

import java.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import ru.jmorozov.prodkalendar.telegram.bot.service.ProductiveKalendarClient
import ru.jmorozov.prodkalendar.telegram.dto.DateRange

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
            rest.getForObject("$productiveKalendarHost/api/query/$date/is/holiday")
}