package com.andreandyp.dias.fragments

import android.content.Context
import androidx.core.content.edit
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.andreandyp.dias.R
import com.google.common.truth.Truth
import org.junit.After
import org.junit.Before
import org.junit.Test

class AjustesFragmentTest {
    private lateinit var scenario: FragmentScenario<AjustesFragment>
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file), Context.MODE_PRIVATE
    )

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(null, R.style.AppTheme)
    }

    @Test
    fun showsCategories() {
        onView(withText(R.string.alarmas_header)).check(matches(isDisplayed()))
        onView(withText(R.string.sync_header)).check(matches(isDisplayed()))
    }

    @Test
    fun showsAlarmsPreferences() {
        onView(withText(R.string.hora_default_title)).check(matches(isDisplayed()))
        onView(withText(R.string.posponer_title)).check(matches(isDisplayed()))
    }

    @Test
    fun showsSnoozeDialog() {
        onView(withText(R.string.posponer_title)).perform(click())
        onView(withText(android.R.string.cancel)).check(matches(isDisplayed()))
        onView(withText(android.R.string.ok)).check(matches(isDisplayed()))
    }

    @Test
    fun showsBackgroundSyncPreference() {
        onView(withText(R.string.sync_title)).check(matches(isDisplayed()))
        onView(withText(R.string.sync_summary_off)).check(matches(isDisplayed()))
    }

    @Test
    fun changesSwitchBackgroundSyncPreference() {
        onView(withText(R.string.sync_title)).perform(click())
        onView(withText(R.string.sync_summary_on)).check(matches(isDisplayed()))
    }

    @Test
    fun navigatesBackToMainFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        scenario.onFragment {
            navController.setGraph(R.navigation.nav_main)
            Navigation.setViewNavController(it.view!!, navController)
        }
        Espresso.pressBackUnconditionally()

        Truth.assertThat(navController.currentDestination!!.id).isEqualTo(R.id.mainFragment)
    }

    @After
    fun tearDown() {
        sharedPreferences.edit { clear() }
    }
}