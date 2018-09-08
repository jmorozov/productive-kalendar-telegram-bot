package ru.jmorozov.prodkalendar.telegram.bot.service.impl

import org.amshove.kluent.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import ru.jmorozov.prodkalendar.telegram.bot.service.ProductiveKalendarClient
import ru.jmorozov.prodkalendar.telegram.dto.BotResponse
import ru.jmorozov.prodkalendar.telegram.dto.DateRange
import ru.jmorozov.prodkalendar.telegram.dto.DayType
import java.time.LocalDate
import java.time.Month

class MessageServiceImplSpec : Spek ({
    given("Message service") {
        val startDate = LocalDate.of(2018, Month.JANUARY, 2)
        val endDate = LocalDate.of(2018, Month.FEBRUARY, 3)
        val preholiday = startDate.minusDays(1)
        val holidays = "5"
        val workdays = "3"
        val productiveKalendarClient = mock(ProductiveKalendarClient::class)
        When calling productiveKalendarClient.getHolidaysBetween(DateRange(startDate, endDate)) itReturns holidays
        When calling productiveKalendarClient.getHolidaysBetween(DateRange(startDate, null)) itReturns holidays
        When calling productiveKalendarClient.getWorkdaysBetween(DateRange(startDate, endDate)) itReturns workdays
        When calling productiveKalendarClient.getWorkdaysBetween(
                DateRange(null, LocalDate.of(2022, Month.FEBRUARY, 2))
        ) itReturns workdays
        When calling productiveKalendarClient.isHoliday(startDate) itReturns true
        When calling productiveKalendarClient.isHoliday(endDate) itReturns false
        When calling productiveKalendarClient.isHolidayTomorrow() itReturns true

        When calling productiveKalendarClient.getDayType(startDate) itReturns DayType.HOLIDAY
        When calling productiveKalendarClient.getDayType(preholiday) itReturns DayType.PREHOLIDAY
        When calling productiveKalendarClient.getDayType(endDate) itReturns DayType.WORKDAY
        When calling productiveKalendarClient.getTomorrowType() itReturns DayType.HOLIDAY

        val messageService = MessageServiceImpl(productiveKalendarClient)

        listOf(
            Test("вЫхОднЫх с 2018-01-02 до 2018-02-03", "should return holidays count string", holidays),
            Test("вЫхОднЫх с 2018-01-02", "should return holidays count string", holidays),
            Test(" рАбОчИх с 2018-01-02 до 2018-02-03 ", "should return workdays count string", workdays),
            Test(" рАбОчИх до 2022-02-02 ", "should return workdays count string", workdays),
            Test("вЫхОднЫх c тогда до сейчас", "should return validation error message", BotResponse.VALIDATION_ERROR_MESSAGE.text),
            Test("рАбОчИх c тогда до сейчас", "should return validation error message", BotResponse.VALIDATION_ERROR_MESSAGE.text),
            Test("рАбОчИх c 2018-02-30 до 2018-02-31", "should return incorrect date message", BotResponse.INCORRECT_DATE.text),
            Test("вЫхОднЫх с 4444-44-44", "should return incorrect date message", BotResponse.INCORRECT_DATE.text),
            Test("2018-01-01", "should return preholiday", BotResponse.PREHOLIDAY.text),
            Test("2018-01-02", "should return holiday", BotResponse.HOLIDAY.text),
            Test(" 2018-02-03 ", "should return workday", BotResponse.WORKDAY.text),
            Test(" ЗАвТрА ", "should return holiday", BotResponse.HOLIDAY.text),
            Test("4444-44-44", "should return incorrect date message", BotResponse.INCORRECT_DATE.text),
            Test(" 2018-01-02 выходной ?", "should return yes", BotResponse.YES.text),
            Test("  2018-02-03  выходной  ", "should return no", BotResponse.NO.text),
            Test("  ЗАвтра  выходной ? ", "should return no", BotResponse.YES.text),
            Test("Абдырда", "should return help message", BotResponse.HELP.text)
        ).forEach {
            test ->
            on("request message `${test.message}`") {
                val responseMessage = messageService.getResponseMessage(test.message)
                it(test.should) {
                    responseMessage shouldEqual test.expected
                }
            }
        }
    }
})

data class Test(val message: String, val should: String, val expected: String)