package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Response(
    @SerialName("result")
    val answerTelegram: List<Update>,
)

@Serializable
data class Update(
    @SerialName("update_id")
    val id: Long,

    @SerialName("message")
    val message: Message,
)

@Serializable
data class Message(
    @SerialName("chat")
    val chat: Chat,

    @SerialName("text")
    val text: String? = null,

    @SerialName("location")
    val location: Location? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val chatId: Long,
)

@Serializable
data class Location(
    @SerialName("latitude")
    val latitude: Double,

    @SerialName("longitude")
    val longitude: Double,
)

fun main(args: Array<String>) {

    val telegramService = TelegramService(args[0])

    val weatherService = WeatherService()

    val json = Json {
        ignoreUnknownKeys = true
    }

    var updateId = 0L

    while (true) {

        Thread.sleep(2000)
        val updates = telegramService.getUpdates(updateId)
        println(updates)

        val update = json.decodeFromString<Response>(updates)

        if (update.answerTelegram.isEmpty()) continue

        val sortedUpdates = update.answerTelegram.sortedBy { it.id }

        sortedUpdates.forEach {
            if (it.message.text?.lowercase() == "/start") telegramService.sendMessage(
                it.message.chat.chatId,
                "Привет! Я бот по получению температуры по точке. \nОтправляй мне геопозицию, а я тебе отправлю температуру в течении дня."
            )
            if (it.message.location != null) {
                getTemperatures(args[0], it.message.chat.chatId, weatherService.getWeather(it.message.location.latitude.toString() , it.message.location.longitude.toString()))
            }
        }

        updateId = sortedUpdates.last().id + 1

    }

}

fun getTemperatures(botToken: String, chatId: Long, json: String) {

    val telegramService = TelegramService(botToken)

    val temperaturesRegex = "\"temperature_2m\":\\[(.+?)]".toRegex()

    val temperatures = temperaturesRegex.find(json)?.groups?.get(1)?.value?.split(",")
        ?: return

    telegramService.sendMessage(chatId, "Температура на сегодня\n00:00 — ${temperatures[0]}" +
            "\n06:00 — ${temperatures[6]}\n12:00 — ${temperatures[12]}\n18:00 — ${temperatures[18]}\n23:00 — ${temperatures[23]}")
}