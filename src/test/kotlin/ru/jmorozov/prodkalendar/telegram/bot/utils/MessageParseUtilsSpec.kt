package ru.jmorozov.prodkalendar.telegram.bot.utils

import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeParseException

private val date = LocalDate.of(2018, Month.AUGUST, 1)

class MessageParseUtilsSpec : Spek({
    given("Message parse utils") {

        val tests = mutableListOf<Test>()
        tests.addAll(getTests("parse start date", "с", ::parseStartDate))
        tests.addAll(getTests("parse end date", "до", ::parseEndDate))

        tests.forEach {
            test ->
            on(test.case) {
                val result = test.parseFunc(test.input)

                it(test.should) {
                    result shouldEqual test.expect
                }
            }
        }

        listOf(
            Test("parse start date with message `верни с 4000-88-99, пожалуйста`", "верни с 4000-88-99, пожалуйста", parseFunc = ::parseStartDate),
            Test("parse end date with message `верни до 4000-88-99, пожалуйста`", "верни до 4000-88-99, пожалуйста", parseFunc = ::parseEndDate)
        ).forEach {
            test ->
            on(test.case) {
                val func = { test.parseFunc(test.input) }

                it("throw DateTimeParseException") {
                    func shouldThrow DateTimeParseException::class
                }
            }
        }
    }
})

data class Test(
        val case: String,
        val input: String,
        val should: String = "",
        val expect: LocalDate? = null,
        val parseFunc: (String) -> LocalDate?
)

fun getTests(funcName: String, prefix: String, parseFunc: (String) -> LocalDate?) =
        listOf(
                Test("$funcName with message `верни $prefix 2018-08-01, пожалуйста`", "верни $prefix 2018-08-01, пожалуйста", "return 2018-08-01", date, parseFunc),
                Test("$funcName with message `верни $prefix 2018-8-1, пожалуйста`", "верни $prefix 2018-8-1, пожалуйста", "return null", null, parseFunc),
                Test("$funcName with message `верни $prefix завтра, пожалуйста`", "верни $prefix завтра, пожалуйста", "return null", null, parseFunc)
        )
