package app.list

import android.app.Activity
import android.app.Instrumentation
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.NoActivityResumedException
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.anyIntent
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra
import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom
import android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.Toolbar
import android.view.View
import app.detail.CountryDetailActivity
import domain.country.Country
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subjects.ReplaySubject
import org.hamcrest.Matchers.allOf
import org.jorge.assignment.app.R
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import util.android.test.BinaryIdlingResource
import util.android.test.matchers.withIndex
import java.net.UnknownHostException
import kotlin.test.assertEquals

/**
 * The setup seems a bit strange, but there is a reason: we need to define SUBJECT before
 * the activity is launched since injection happens on launch. Also,
ActivityTestRule#beforeActivityLaunched is only called when the Activity is scheduled for launch
 * already, and JUnit's @Before is invoked before the test, but with the Activity prepared already.
 * This forces us to manually launch the activity in every test.
 */
internal class CountryListActivityInstrumentation {
    @JvmField
    @Rule
    val activityTestRule = object : ActivityTestRule<CountryListActivity>(
            CountryListActivity::class.java, false, false) {
        override fun beforeActivityLaunched() {
            IDLING_RESOURCE = BinaryIdlingResource("load")
            Espresso.registerIdlingResources(IDLING_RESOURCE)
        }

        override fun afterActivityFinished() {
            super.afterActivityFinished()
            Espresso.unregisterIdlingResources(IDLING_RESOURCE)
        }
    }
    @JvmField
    @Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun activityIsShown() {
        SUBJECT = ReplaySubject.create()
        SUBJECT.onComplete()
        launchActivity()
        onView(withId(android.R.id.content)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Window visibility was not VISIBLE") }
    }

    @Test
    fun toolbarIsCompletelyShownOnOpening() {
        SUBJECT = ReplaySubject.create()
        SUBJECT.onComplete()
        launchActivity()
        val completelyDisplayedMatcher = matches(isCompletelyDisplayed())
        onView(isAssignableFrom(Toolbar::class.java)).check(completelyDisplayedMatcher)
        onView(withText(R.string.app_label)).check(completelyDisplayedMatcher)
    }

    @Test
    fun goingBackPausesApp() {
        SUBJECT = ReplaySubject.create()
        SUBJECT.onComplete()
        launchActivity()
        expectedException.expect(NoActivityResumedException::class.java)
        expectedException.expectMessage("Pressed back and killed the app")
        Espresso.pressBack()
    }

    @Test
    fun onLoadItemsAreShown() {
        val name = "name"
        SUBJECT = ReplaySubject.create()
        SUBJECT.onNext(listOf(Country(
                name = name,
                region = "",
                capital = "",
                area = "",
                languages = emptyArray(),
                germanTranslation = "",
                flag = "https://github.com/hjnilsson/country-flags/blob/master/png1000px/ad.png",
                nativeName = "")))
        SUBJECT.onComplete()
        launchActivity()
        onView(withId(R.id.progress)).check { view, _ ->
            assertEquals(View.GONE, view.visibility, "Progress visibility was not GONE") }
        onView(withId(R.id.error)).check { view, _ ->
            assertEquals(View.GONE, view.visibility, "Error visibility was not GONE") }
        onView(withId(R.id.content)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Content visibility was not VISIBLE") }
        onView(withIndex(withText(name), 0)).check(matches(isDisplayed()))
    }

    @Test
    fun onFailureErrorIsShown() {
        SUBJECT = ReplaySubject.create()
        SUBJECT.onError(UnknownHostException())
        launchActivity()
        onView(withId(R.id.progress)).check { view, _ ->
            assertEquals(View.GONE, view.visibility, "Progress visibility was not GONE") }
        onView(withId(R.id.error)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Error visibility was not VISIBLE") }
        onView(withId(R.id.content)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Content visibility was not VISIBLE") }
    }

    @Test
    fun onItemClickDetailIntentIsLaunched() {
        val name = "name"
        SUBJECT = ReplaySubject.create()
        SUBJECT.onNext(listOf(Country(
                name = name,
                region = "",
                capital = "",
                area = "",
                languages = emptyArray(),
                germanTranslation = "",
                flag = "https://github.com/hjnilsson/country-flags/blob/master/png1000px/ad.png",
                nativeName = "")))
        SUBJECT.onComplete()
        launchActivity()
        Intents.init()
        intending(anyIntent()).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
        onView(withIndex(withText(name), 0)).perform(click())
        intended(allOf(hasComponent(CountryDetailActivity::class.java.name),
                hasExtra(CountryDetailActivity.KEY_MODEL, PresentationCountry(
                        name = name,
                        region = "",
                        capital = "",
                        area = "",
                        languages = emptyArray(),
                        germanTranslation = "",
                        flag = "https://github.com/hjnilsson/country-flags/blob/master/png1000px/ad.png",
                        nativeName = ""))
        ))
        Intents.release()
    }

    /**
     * Launches the activity.
     */
    private fun launchActivity() = activityTestRule.launchActivity(
            CountryListActivity.getCallingIntent(InstrumentationRegistry.getContext()))

    companion object {
        private lateinit var IDLING_RESOURCE: BinaryIdlingResource
        internal lateinit var SUBJECT: ReplaySubject<List<Country>>
        internal val SUBSCRIBER_GENERATOR:
                (CountryListCoordinator) -> DisposableSingleObserver<List<Country>> =
                {
                    object : CountryPageLoadObserver(it, PresentationCountryEntityMapper()) {
                        override fun onStart() {
                            super.onStart()
                            IDLING_RESOURCE.setIdleState(false)
                        }

                        override fun onSuccess(payload: List<Country>) {
                            super.onSuccess(payload)
                            IDLING_RESOURCE.setIdleState(true)
                        }

                        override fun onError(throwable: Throwable) {
                            super.onError(throwable)
                            IDLING_RESOURCE.setIdleState(true)
                        }
                    }
        }
    }
}
