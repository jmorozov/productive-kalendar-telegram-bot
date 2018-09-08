package ru.jmorozov.prodkalendar.telegram.bot.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import ru.jmorozov.prodkalendar.bot.service.MessageService
import ru.jmorozov.prodkalendar.telegram.bot.service.ProductiveKalendarClient
import ru.jmorozov.prodkalendar.telegram.bot.utils.parseDate
import ru.jmorozov.prodkalendar.telegram.bot.utils.parseEndDate
import ru.jmorozov.prodkalendar.telegram.bot.utils.parseStartDate
import ru.jmorozov.prodkalendar.telegram.dto.BotResponse
import ru.jmorozov.prodkalendar.telegram.dto.DateRange
import ru.jmorozov.prodkalendar.telegram.dto.DayType
import java.time.format.DateTimeParseException
import javax.validation.ValidationException

@Service
class MessageServiceImpl @Autowired constructor(
    val productiveKalendarClient: ProductiveKalendarClient
) : MessageService {

    companion object {
        val IS_HOLIDAY_REGEX = """(?<date>\d{4}-\d{2}-\d{2})\s+(выходной|ВЫХОДНОЙ)\s*\??""".toRegex()
        val IS_HOLIDAY_TOMORROW = """[Зз][Аа]втра\s+выходной\s*\??""".toRegex()
        val DATE_REGEX = """(?<date>\d{4}-\d{2}-\d{2})""".toRegex()
    }

    @Suppress("ReturnCount", "ComplexMethod")
    override fun getResponseMessage(message: String): String {
        val trimmedMessage = message.trim()
        return try {
            when {
                trimmedMessage.startsWith("выходных", true) ->
                    getCountBetween(trimmedMessage, productiveKalendarClient::getHolidaysBetween)
                trimmedMessage.startsWith("рабочих", true) ->
                    getCountBetween(trimmedMessage, productiveKalendarClient::getWorkdaysBetween)

                IS_HOLIDAY_REGEX.matches(trimmedMessage) -> isDateHoliday(trimmedMessage).text
                IS_HOLIDAY_TOMORROW.matches(trimmedMessage) -> isTomorrowHoliday().text

                DATE_REGEX.matches(trimmedMessage) -> getDayType(trimmedMessage).text
                "Завтра".equals(trimmedMessage, true) -> getTomorrowType().text
                else -> BotResponse.HELP.text
            }
        } catch (dtpe: DateTimeParseException) {
            BotResponse.INCORRECT_DATE.text
        } catch (rce: HttpClientErrorException) {
            if (rce.statusCode.is4xxClientError) {
                BotResponse.VALIDATION_ERROR_MESSAGE.text
            } else {
                BotResponse.REST_EXCEPTION.text
            }
        }
    }

    private fun getCountBetween(message: String, getCountByRange: (DateRange) -> String?): String {
        val range = DateRange(parseStartDate(message), parseEndDate(message))
        return try {
            getCountByRange(range) ?: BotResponse.VALIDATION_ERROR_MESSAGE.text
        } catch (e: ValidationException) {
            BotResponse.VALIDATION_ERROR_MESSAGE.text
        }
    }

    private fun isDateHoliday(message: String): BotResponse {
        val date = parseDate(message, IS_HOLIDAY_REGEX, "date") ?: return BotResponse.INCORRECT_DATE

        val isHoliday: Boolean = productiveKalendarClient.isHoliday(date) ?: return BotResponse.INCORRECT_DATE

        return if (isHoliday) BotResponse.YES else BotResponse.NO
    }

    private fun isTomorrowHoliday(): BotResponse {
        val isHoliday: Boolean = productiveKalendarClient.isHolidayTomorrow() ?: return BotResponse.REST_EXCEPTION

        return if (isHoliday) BotResponse.YES else BotResponse.NO
    }

    private fun getDayType(message: String): BotResponse {
        val date = parseDate(message, DATE_REGEX, "date") ?: return BotResponse.INCORRECT_DATE

        val dayType: DayType = productiveKalendarClient.getDayType(date) ?: return BotResponse.INCORRECT_DATE

        return toBotResponse(dayType)
    }

    private fun toBotResponse(dayType: DayType): BotResponse =
            when(dayType) {
                DayType.PREHOLIDAY -> BotResponse.PREHOLIDAY
                DayType.HOLIDAY -> BotResponse.HOLIDAY
                DayType.WORKDAY -> BotResponse.WORKDAY
            }

    private fun getTomorrowType(): BotResponse {
        val dayType: DayType = productiveKalendarClient.getTomorrowType() ?: return BotResponse.REST_EXCEPTION

        return toBotResponse(dayType)
    }
}