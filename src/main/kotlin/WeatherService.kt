package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDate

var currentMonth = LocalDate.now().monthValue.toString()
    get() {
        return if (field.toInt() <= 9) "0$field"
        else field
    }

var currentDay = LocalDate.now().dayOfMonth.toString()
    get() {
        return if (field.toInt() <= 9) "0$field"
        else field
    }

class WeatherService {

    private val client = HttpClient.newBuilder().build()

    fun getWeather(latitude: String, longitude: String): String {
        val urlGetWeather =
            "${Constants.URL_API_WEATHER}latitude=$latitude&longitude=$longitude" +
                    "&hourly=temperature_2m" +
                    "&start_date=${LocalDate.now().year}-$currentMonth-${currentDay}" +
                    "&end_date=${LocalDate.now().year}-$currentMonth-${currentDay}"

        val requestGetWeather: HttpRequest =
            HttpRequest.newBuilder().uri(URI.create(urlGetWeather)).build()
        val responseGetWeather: HttpResponse<String> =
            client.send(requestGetWeather, HttpResponse.BodyHandlers.ofString())

        return responseGetWeather.body()
    }

}
