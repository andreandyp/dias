package com.andreandyp.dias.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito


@RunWith(AndroidJUnit4::class)
@LargeTest
class DescargarDatosAmanecerWorkerTest {
    private lateinit var context: Context
    private lateinit var sharedPrefs: SharedPreferences

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    /*@Test
    fun shouldReturnSuccessWithOrdinaryConditions() {
        sharedPrefs = Mockito.mock(SharedPreferences::class.java)
        //Mockito.`when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs)
        val worker = TestListenableWorkerBuilder<DescargarDatosAmanecerWorker>(context).build()
        val result: ListenableWorker.Result = worker.startWork().get()
        assertThat(result, `is`(ListenableWorker.Result.success()))
    }

    @Test
    fun shouldReturnFailureWhenBackSyncIsDisabled() {
        val worker = TestListenableWorkerBuilder<DescargarDatosAmanecerWorker>(context).build()
        val result: ListenableWorker.Result = worker.startWork().get()
        assertThat(result, `is`(ListenableWorker.Result.failure()))
    }*/
}