package ru.jmorozov.prodkalendar.telegram.bot.utils

import java.time.LocalDate

private const val DATE_GROUP_NAME = "date"

fun parseStartDate(input: String): LocalDate? =
        parseDate(input, getDateRegexWithPrefix(".*с"), DATE_GROUP_NAME)

// Да, есть более полные регулярки для дат, но дальше всё равно вызывается LocalDate.parse - он быстрее
private fun getDateRegexWithPrefix(prefix: String) = ("""$prefix\s+(?<$DATE_GROUP_NAME>\d{4}-\d{2}-\d{2}).*""").toRegex()

fun parseEndDate(input: String): LocalDate? =
        parseDate(input, getDateRegexWithPrefix(".*до"), DATE_GROUP_NAME)

fun parse(str: String, regex: Regex, groupName: String): String? =
        regex.matchEntire(str)?.groups?.get(groupName)?.value

fun parseDate(str: String, regex: Regex, groupName: String): LocalDate? {
    val dateStr = parse(str, regex, groupName) ?: return null

    return LocalDate.parse(dateStr)
}