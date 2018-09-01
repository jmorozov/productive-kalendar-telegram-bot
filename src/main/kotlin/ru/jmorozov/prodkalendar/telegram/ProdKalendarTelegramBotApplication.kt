package ru.jmorozov.prodkalendar.telegram

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.telegram.telegrambots.ApiContextInitializer

@SpringBootApplication
class ProdKalendarApplication

fun main(args: Array<String>) {
    ApiContextInitializer.init()

    @Suppress("SpreadOperator")
    runApplication<ProdKalendarApplication>(*args)
}
