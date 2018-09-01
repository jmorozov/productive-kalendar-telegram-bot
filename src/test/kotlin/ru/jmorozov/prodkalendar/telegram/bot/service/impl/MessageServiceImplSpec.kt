package ru.jmorozov.prodkalendar.telegram.bot.service.impl

import org.amshove.kluent.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import ru.jmorozov.prodkalendar.telegram.bot.service.ProductiveKalendarClient
import ru.jmorozov.prodkalendar.telegram.bot.service.impl.MessageServiceImpl.Companion.HELP
import ru.jmorozov.prodkalendar.telegram.bot.service.impl.MessageServiceImpl.Companion.HOLIDAY
import ru.jmorozov.prodkalendar.telegram.bot.service.impl.MessageServiceImpl.Companion.INCORRECT_DATE
import ru.jmorozov.prodkalendar.telegram.bot.service.impl.MessageServiceImpl.Companion.VALIDATION_ERROR_MESSAGE
import ru.jmorozov.prodkalendar.telegram.bot.service.impl.MessageServiceImpl.Companion.WORKDAY
import ru.jmorozov.prodkalendar.telegram.dto.DateRange
import java.time.LocalDate
import java.time.Month

class MessageServiceImplSpec : Spek ({
    given("Message service") {
        val startDate = LocalDate.of(2018, Month.JANUARY, 1)
        val endDate = LocalDate.of(2018, Month.FEBRUARY, 2)
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

        val messageService = MessageServiceImpl(productiveKalendarClient)

        listOf(
            Test("вЫхОднЫх с 2018-01-01 до 2018-02-02", "should return holidays count string", holidays),
            Test("вЫхОднЫх с 2018-01-01", "should return holidays count string", holidays),
            Test("рАбОчИх с 2018-01-01 до 2018-02-02", "should return workdays count string", workdays),
            Test("рАбОчИх до 2022-02-02", "should return workdays count string", workdays),
            Test("вЫхОднЫх c тогда до сейчас", "should return validation error message", VALIDATION_ERROR_MESSAGE),
            Test("рАбОчИх c тогда до сейчас", "should return validation error message", VALIDATION_ERROR_MESSAGE),
            Test("рАбОчИх c 2018-02-30 до 2018-02-31", "should return incorrect date message", INCORRECT_DATE),
            Test("вЫхОднЫх с 4444-44-44", "should return incorrect date message", INCORRECT_DATE),
            Test("2018-01-01", "should return holiday", HOLIDAY),
            Test("2018-02-02", "should return workday", WORKDAY),
            Test("4444-44-44", "should return incorrect date message", INCORRECT_DATE),
            Test("Абдырда", "should return help message", HELP)
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