package ru.jmorozov.prodkalendar.telegram.bot

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.jmorozov.prodkalendar.bot.service.MessageService

@Component
class TelegramBot @Autowired constructor(
    @Value("\${bot.name}") val name: String,
    @Value("\${bot.token}") val token: String,
    private val messageService: MessageService
) : TelegramLongPollingBot() {

    private companion object {
        val log: Logger = LoggerFactory.getLogger(TelegramBot::class.java.name)
    }

    override fun getBotUsername(): String = name

    override fun getBotToken(): String = token

    override fun onUpdateReceived(update: Update?) {
        log.info("Message received: ${update?.message?.text}")

        if (update?.message == null) {
            return
        }

        val message = update.message?.text ?: "Помогите"

        val responseMessage = messageService.getResponseMessage(message)

        sendMsg(update.message.chatId.toString(), responseMessage)
    }

    @Synchronized
    fun sendMsg(chatId: String, message: String) {
        log.info("Send message: $message")

        val sendMessage = SendMessage()
        with(sendMessage) {
            enableMarkdown(true)
            this.chatId = chatId
            text = message
        }
        try {
            execute(sendMessage)
        } catch (e: TelegramApiException) {
            log.error("An exception occurred while sending telegram message", e)
        }
    }
}