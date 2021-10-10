package com.andreandyp.dias.network

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets

fun MockWebServer.enqueueFromFile(code: Int, fileName: String) {
    val inputStream = javaClass.classLoader?.getResourceAsStream("api-responses/$fileName")
    val source = inputStream?.source()?.buffer()
    source?.let {
        enqueue(
            MockResponse.responseFromFile(code, fileName)
        )
    }
}

fun MockResponse.Companion.responseFromFile(code: Int, fileName: String): MockResponse {
    val inputStream = this::class.java.classLoader?.getResourceAsStream("api-responses/$fileName")
    val source = inputStream?.source()?.buffer()
        ?: throw FileNotFoundException("$fileName does not exists")
    return MockResponse()
        .setResponseCode(code)
        .setBody(source.readString(StandardCharsets.UTF_8))
}