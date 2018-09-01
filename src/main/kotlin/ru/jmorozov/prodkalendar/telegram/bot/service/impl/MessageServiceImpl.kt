package ru.jmorozov.prodkalendar.telegram.bot.service.impl

import java.time.format.DateTimeParseException
import javax.validation.ValidationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import ru.jmorozov.prodkalendar.bot.service.MessageService
import ru.jmorozov.prodkalendar.telegram.bot.service.ProductiveKalendarClient
import ru.jmorozov.prodkalendar.telegram.bot.utils.parseDate
import ru.jmorozov.prodkalendar.telegram.bot.utils.parseEndDate
import ru.jmorozov.prodkalendar.telegram.bot.utils.parseStartDate
import ru.jmorozov.prodkalendar.telegram.dto.DateRange

@Service
class MessageServiceImpl @Autowired constructor(
    val productiveKalendarClient: ProductiveKalendarClient
) : MessageService {

    companion object {
        const val VALIDATION_ERROR_MESSAGE = "Некорректные входные данные. Начало или конец интервала дат должен быть указан в формате гггг-ММ-дд"
        val HELP = """Выходных с гггг-ММ-дд до ггг-ММ-дд - узнать количество выходных в интервале дат
                        |Рабочих с гггг-ММ-дд до ггг-ММ-дд - узнать количество рабочих в интервале дат
                        |гггг-ММ-дд - узнать является ли день выходным или рабочим
                    """.trimMargin()
        const val INCORRECT_DATE = "Некорректная дата. Дата должна соотеветствовать формату гггг-ММ-дд - 2018-08-30"
        const val HOLIDAY = "Выходной"
        const val WORKDAY = "Рабочий"
        const val REST_EXCEPTION = "Упс! Произошла ошибка в сервисе производственного календаря. Попробуйте повторить позже"

        val DATE_REGEX = """(?<date>\d{4}-\d{2}-\d{2})""".toRegex()
    }

    @Suppress("ReturnCount", "ComplexMethod")
    override fun getResponseMessage(message: String): String =
            try {
                when {
                    message.startsWith("выходных", true) -> getCountBetween(message, productiveKalendarClient::getHolidaysBetween)
                    message.startsWith("рабочих", true) -> getCountBetween(message, productiveKalendarClient::getWorkdaysBetween)
                    DATE_REGEX.matches(message) -> getDateType(message)
                    else -> HELP
                }
            } catch (dtpe: DateTimeParseException) {
                INCORRECT_DATE
            } catch (rce: HttpClientErrorException) {
                if (rce.statusCode.is4xxClientError) {
                    VALIDATION_ERROR_MESSAGE
                } else {
                    REST_EXCEPTION
                }
            }

    private fun getCountBetween(message: String, parseFunc: (DateRange) -> String?): String {
        val range = DateRange(parseStartDate(message), parseEndDate(message))
        return try {
            parseFunc(range) ?: VALIDATION_ERROR_MESSAGE
        } catch (e: ValidationException) {
            VALIDATION_ERROR_MESSAGE
        }
    }

    private fun getDateType(message: String): String {
        val date = parseDate(message, DATE_REGEX, "date") ?: return INCORRECT_DATE

        val isHoliday: Boolean = productiveKalendarClient.isHoliday(date) ?: return INCORRECT_DATE

        return if (isHoliday) HOLIDAY else WORKDAY
    }
}