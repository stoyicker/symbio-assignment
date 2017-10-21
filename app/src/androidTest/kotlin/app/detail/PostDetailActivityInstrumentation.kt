package app.detail

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom
import android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.Toolbar
import android.view.View
import app.list.PresentationCountry
import org.jorge.assignment.app.R
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify
import kotlin.test.assertEquals

/**
 * Instrumentation tests for CountryDetailActivity.
 */
internal class PostDetailActivityInstrumentation {
    @JvmField
    @Rule
    val activityTestRule = object : ActivityTestRule<CountryDetailActivity>(
            CountryDetailActivity::class.java) {
        override fun getActivityIntent(): Intent {
            return CountryDetailActivity.getCallingIntent(InstrumentationRegistry.getTargetContext(),
                    ITEM)
        }
    }

    @Test
    fun activityIsShown() {
        onView(withId(android.R.id.content)).check { view, _ ->
            assertEquals(View.VISIBLE, view.visibility, "Window visibility was not VISIBLE") }
    }

    @Test
    fun toolbarIsCompletelyShownOnOpening() {
        val completelyDisplayedMatcher = matches(isCompletelyDisplayed())
        onView(isAssignableFrom(Toolbar::class.java)).check(completelyDisplayedMatcher)
        onView(withText(R.string.app_label)).check(completelyDisplayedMatcher)
    }

    @Test
    fun viewIsUpdated() {
        verify(activityTestRule.activity.view).updateContent(ITEM)
    }

    private companion object {
        private val ITEM = PresentationCountry("name")
    }
}
