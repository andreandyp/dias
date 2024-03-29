package com.andreandyp.dias.fragments

import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.andreandyp.dias.HiltTestActivity
import com.andreandyp.dias.R
import com.andreandyp.dias.adapters.AlarmasAdapter
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.fakes.repository.sunrise.FakeSunriseRepository
import com.andreandyp.dias.fakes.usecases.FakeGetTomorrowSunriseUseCase
import com.andreandyp.dias.launchFragmentInHiltContainer
import com.andreandyp.dias.mocks.PreferencesMocks
import com.andreandyp.dias.usecases.GetTomorrowSunriseUseCase
import com.andreandyp.dias.utils.translateDisplayName
import com.google.common.truth.Truth
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@HiltAndroidTest
class MainFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val fakeGetTomorrowSunriseUseCase: GetTomorrowSunriseUseCase =
        FakeGetTomorrowSunriseUseCase(FakeSunriseRepository())

    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    @Before
    fun setUp() {
        hiltRule.inject()
        scenario = launchFragmentInHiltContainer<MainFragment>()
    }

    @Test
    fun showsHeaderAndFooter() {
        onView(withId(R.id.proxima_alarma)).check(matches(isDisplayed()))
        onView(withId(R.id.proxima_alarma)).check(matches(withText(R.string.proxima_alarma)))
        onView(withId(R.id.creditos)).check(matches(isDisplayed()))
        onView(withId(R.id.creditos)).check(matches(withText(R.string.creditos_api)))
    }

    @Test
    fun showsNoAlarmText() {
        onView(withId(R.id.hora_alarma)).check(matches(isDisplayed()))
        onView(withId(R.id.hora_alarma)).check(matches(withText(R.string.sin_alarma)))
        onView(withId(R.id.obteniendo_datos)).check(matches(not(isDisplayed())))
    }

    @Test
    fun showsRecyclerView() {
        onView(withId(R.id.alarmas)).check(matches(isDisplayed()))
        scenario.onActivity {
            val recyclerView: RecyclerView = it.findViewById(R.id.alarmas)
            Truth.assertThat(recyclerView.adapter).isNotNull()
            Truth.assertThat(recyclerView.adapter!!.itemCount).isEqualTo(7)
        }
    }

    @Test
    fun changesColorOfDayOfNextAlarm() {
        val nextDay = LocalDate.now().plusDays(1)
        onView(withId(R.id.alarmas)).perform(
            RecyclerViewActions.scrollToPosition<AlarmasAdapter.AlarmViewHolder>(
                nextDay.dayOfWeek.value - 1
            )
        )
        onView(withText(nextDay.dayOfWeek.translateDisplayName()))
            .check(matches(hasTextColor(R.color.primaryColor)))
    }

    @Test
    fun updatesCurrentAlarmFromInternet() {
        onView(withId(R.id.swipeToRefresh)).perform(swipeDown())
        onView(withId(R.id.fuente)).check(matches(isDisplayed()))
        onView(withId(R.id.fuente)).check(matches(withText(R.string.segun_internet)))
    }

    @Test
    fun updatesCurrentAlarmFromDatabase() {
        val useCase = (fakeGetTomorrowSunriseUseCase as FakeGetTomorrowSunriseUseCase)
        useCase.originToFetch = Origin.DATABASE
        onView(withId(R.id.swipeToRefresh)).perform(swipeDown())
        onView(withId(R.id.fuente)).check(matches(isDisplayed()))
        onView(withId(R.id.fuente)).check(matches(withText(R.string.segun_bd)))
    }

    @Test
    fun updatesCurrentAlarmFromPreferencesNoInternet() {
        val useCase = (fakeGetTomorrowSunriseUseCase as FakeGetTomorrowSunriseUseCase)
        useCase.originToFetch = Origin.NO_INTERNET
        onView(withId(R.id.swipeToRefresh)).perform(swipeDown())
        onView(withId(R.id.fuente)).check(matches(isDisplayed()))
        onView(withId(R.id.fuente)).check(matches(withText(R.string.segun_usuario)))
    }

    @Test
    fun updatesCurrentAlarmFromPreferencesNoLocation() {
        val useCase = (fakeGetTomorrowSunriseUseCase as FakeGetTomorrowSunriseUseCase)
        useCase.originToFetch = Origin.NO_INTERNET
        onView(withId(R.id.swipeToRefresh)).perform(swipeDown())
        onView(withId(R.id.fuente)).check(matches(isDisplayed()))
        onView(withId(R.id.fuente)).check(matches(withText(R.string.segun_usuario)))
    }

    @Test
    fun updatesUIWhenTurningOnAlarm() {
        onView(withId(R.id.swipeToRefresh)).perform(swipeDown())

        val nextDay = LocalDate.now().plusDays(1)
        onView(withId(R.id.alarmas)).perform(
            RecyclerViewActions.scrollToPosition<AlarmasAdapter.AlarmViewHolder>(
                nextDay.dayOfWeek.value - 1
            )
        )

        onView(
            allOf(
                withId(R.id.switch_encender),
                hasSibling(
                    withText(nextDay.dayOfWeek.translateDisplayName())
                )
            )
        ).perform(click())

        onView(withId(R.id.hora_alarma)).check(matches(withText(PreferencesMocks.alarm.formattedDate)))
    }

    @Test
    fun showsDialogOnClickOffset() {
        val nextDay = LocalDate.now().plusDays(1)
        onView(withId(R.id.alarmas)).perform(
            RecyclerViewActions.scrollToPosition<AlarmasAdapter.AlarmViewHolder>(
                nextDay.dayOfWeek.value - 1
            )
        )
        onView(
            allOf(
                withId(R.id.tv_hora_antes_despues),
                hasSibling(
                    withText(nextDay.dayOfWeek.translateDisplayName())
                )
            )
        ).perform(click())

        onView(withText("Establece la hora antes o después del amanecer"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun navigatesToAjustesFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        scenario.onActivity {
            navController.setGraph(R.navigation.nav_main)
            val view = it.findViewById<View>(android.R.id.content).rootView
            Navigation.setViewNavController(view, navController)
        }

        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(R.string.ajustes)).perform(click())
        Truth.assertThat(navController.currentDestination!!.id).isEqualTo(R.id.ajustesFragment)
    }
}