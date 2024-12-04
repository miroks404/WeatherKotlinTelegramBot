package org.example

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class TelegramService(private val botToken: String) {

    private val client = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "${Constants.URL_API_TELEGRAM}$botToken/getUpdates?offset=$updateId"

        val requestGetUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val responseGetUpdates: HttpResponse<String> =
            client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())

        return responseGetUpdates.body()
    }

    fun sendMessage(chatId: String, text: String) {

        val encoded = URLEncoder.encode(
            text,
            StandardCharsets.UTF_8
        )

        println(encoded)

        val urlSendMessage = "${Constants.URL_API_TELEGRAM}$botToken/sendMessage?chat_id=$chatId&text=$encoded"

        val requestSendMessage = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()

        client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString())
    }


}