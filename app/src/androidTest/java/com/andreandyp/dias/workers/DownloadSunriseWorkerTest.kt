package com.andreandyp.dias.workers

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestListenableWorkerBuilder
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class DownloadSunriseWorkerTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var context: Context
    private lateinit var worker: DownloadSunriseWorker

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        hiltRule.inject()
        worker = TestListenableWorkerBuilder<DownloadSunriseWorker>(context)
            .setWorkerFactory(workerFactory)
            .build()
    }

    @Test
    fun executesSuccessfullyWhenLocationIsFetched() = runBlocking {
        val result = worker.doWork()
        Truth.assertThat(result).isEqualTo(Result.success())
    }
}