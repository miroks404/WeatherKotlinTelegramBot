package org.example

fun main(args: Array<String>) {

    val telegramService = TelegramService(args[0])

    val weatherService = WeatherService()

    var updateId = 0

    val updateIdRegex = "\"update_id\":(.+?),".toRegex()

    val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()

    val chatIdRegex = "\"chat\":\\{\"id\":(.+?),".toRegex()

    val latitudeRegex = "\"latitude\":(.+?),".toRegex()

    val longitudeRegex = "\"longitude\":(.+?)}".toRegex()

    while (true) {

        Thread.sleep(2000)
        val updates = telegramService.getUpdates(updateId)
        println(updates)

        val updateIdResult = updateIdRegex.find(updates)
        updateId = updateIdResult?.groups?.get(1)?.value?.toInt() ?: -1

        if (updateId == -1) continue

        println(updateId)

        updateId++

        val messageText = messageTextRegex.find(updates)?.groups?.get(1)?.value
        val chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value ?: continue

        if (messageText?.lowercase() == "/start") telegramService.sendMessage(
            chatId,
            "Привет! Я бот по получению погоды по точке. \nОтправляй мне геопозицию, а я тебе отправлю погоду в течении дня."
        )

        val latitude = latitudeRegex.find(updates)?.groups?.get(1)?.value   ?: continue
        val longitude = longitudeRegex.find(updates)?.groups?.get(1)?.value ?: continue

        val jsonWeather = weatherService.getWeather(latitude, longitude)

        println(jsonWeather)

        getTemperatures(args[0], chatId, jsonWeather)
    }

}

fun getTemperatures(botToken: String, chatId: String, json: String) {

    val telegramService = TelegramService(botToken)

    val temperaturesRegex = "\"temperature_2m\":\\[(.+?)]".toRegex()

    val temperatures = temperaturesRegex.find(json)?.groups?.get(1)?.value?.split(",")
        ?: return

    telegramService.sendMessage(chatId, "Температура на сегодня\n00:00 - ${temperatures[0]}" +
            "\n06:00 - ${temperatures[6]}\n12:00 - ${temperatures[12]}\n18:00 - ${temperatures[18]}\n23:00 - ${temperatures[23]}")
}