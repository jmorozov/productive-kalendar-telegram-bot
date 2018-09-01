package ru.jmorozov.prodkalendar.bot.service

interface MessageService {
    fun getResponseMessage(message: String): String
}