package com.andreandyp.dias.network

import com.andreandyp.dias.domain.Sunrise
import com.andreandyp.dias.mocks.LocationMocks
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.time.LocalDate
import java.util.concurrent.TimeUnit

class SunriseRetrofitDataSourceTest {
    private val fakeLatitude = LocationMocks.fakeLatitude.toString()
    private val fakeLongitude = LocationMocks.fakeLongitude.toString()
    private val localDate = LocalDate.now().plusDays(1)
    private val mockWebServer = MockWebServer()

    private val client = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.SECONDS)
        .readTimeout(1, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.SECONDS)
        .build()
    private val sunriseSunsetService: SunriseSunsetService by lazy {
        SunriseSunsetAPI
            .getRetrofitInstance(mockWebServer.url("/").toString(), client)
            .create(SunriseSunsetService::class.java)
    }

    private lateinit var sunriseRetrofitDataSource: SunriseRetrofitDataSource

    @Before
    fun setup() {
        sunriseRetrofitDataSource = SunriseRetrofitDataSource(sunriseSunsetService)
    }

    @Test
    fun `fetches data from service successfully`() = runBlocking {
        mockWebServer.enqueueFromFile(200, SUCCESS)

        val result = sunriseRetrofitDataSource.fetchSunrise(localDate, fakeLatitude, fakeLongitude)

        assertThat(result).isInstanceOf(Sunrise::class.java)
    }

    @Test
    fun `throws an exception when server answers with 400 (invalid date)`(): Unit = runBlocking {
        mockWebServer.enqueueFromFile(400, INVALID_DATE)

        val exception = try {
            sunriseRetrofitDataSource.fetchSunrise(localDate, fakeLatitude, fakeLongitude)
            null
        } catch (e: Exception) {
            e
        }

        assertThat(exception).isInstanceOf(HttpException::class.java)
        exception as HttpException
        assertThat(exception.code()).isEqualTo(400)
    }

    @Test
    fun `throws an exception when server answers with 400 (invalid request)`(): Unit = runBlocking {
        mockWebServer.enqueueFromFile(400, INVALID_REQUEST)

        val exception = try {
            sunriseRetrofitDataSource.fetchSunrise(localDate, fakeLatitude, fakeLongitude)
            null
        } catch (e: Exception) {
            e
        }

        assertThat(exception).isInstanceOf(HttpException::class.java)
        exception as HttpException
        assertThat(exception.code()).isEqualTo(400)
    }

    @Test
    fun `throws an exception when server answers with 500 (server error)`(): Unit = runBlocking {
        mockWebServer.enqueueFromFile(500, UNKNOWN_ERROR)

        val exception = try {
            sunriseRetrofitDataSource.fetchSunrise(localDate, fakeLatitude, fakeLongitude)
            null
        } catch (e: Exception) {
            e
        }

        assertThat(exception).isInstanceOf(HttpException::class.java)
        exception as HttpException
        assertThat(exception.code()).isEqualTo(500)
    }

    @Test
    fun `throws an exception when response is invalid`(): Unit = runBlocking {
        mockWebServer.enqueue(MockResponse.responseFromFile(200, INVALID_RESPONSE).apply {
            throttleBody(1024, 1, TimeUnit.SECONDS)
        })

        val exception = try {
            sunriseRetrofitDataSource.fetchSunrise(localDate, fakeLatitude, fakeLongitude)
            null
        } catch (e: Exception) {
            e
        }

        assertThat(exception).isInstanceOf(JsonDataException::class.java)
    }

    @Test
    fun `throws an exception when timeout`(): Unit = runBlocking {
        mockWebServer.enqueue(MockResponse.responseFromFile(200, SUCCESS).apply {
            setBodyDelay(2, TimeUnit.SECONDS)
        })

        val exception = try {
            sunriseRetrofitDataSource.fetchSunrise(localDate, fakeLatitude, fakeLongitude)
            null
        } catch (e: Exception) {
            e
        }

        assertThat(exception).isInstanceOf(SocketTimeoutException::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    companion object {
        private const val SUCCESS = "success.json"
        private const val INVALID_DATE = "invalid-date.json"
        private const val INVALID_REQUEST = "invalid-request.json"
        private const val UNKNOWN_ERROR = "unknown-error.json"
        private const val INVALID_RESPONSE = "invalid-response.json"
    }
}