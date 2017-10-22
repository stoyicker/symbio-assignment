package app.splash

import android.content.Intent
import android.os.Handler
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import app.common.UIPostExecutionThread
import app.list.CountryListActivity
import domain.country.FetchCountriesUseCase

/**
 * A simple activity that acts as a splash screen.
 * Note how, instead of using the content view to set the splash, we just set the splash as
 * background in the theme. This allows it to be shown without having to wait for the content view
 * to be drawn.
 */
internal class SplashActivity : AppCompatActivity() {
    private lateinit var handler: Handler

    override fun onResume() {
        super.onResume()
        scheduleContentOpening()
    }

    /**
     * Schedules the app content to be shown.
     */
    private fun scheduleContentOpening() {
        // 'Pre-fetch'. This is a bit arguable, due to several reasons:
        // 1. It's inside the activity. That's coupling with framework classes, which is bad. Would
        // be delegated to a coordinator of its own if I was a bit less tired :P
        // 2. It's unreliable, as in there's no guarantee that the prefetch will be done by the time
        // the splash is over. It's not a problem in our app because of how Store works, but I
        // thought it should be noted.
        FetchCountriesUseCase(0, UIPostExecutionThread).buildUseCase().subscribe()
        handler = Handler()
        handler.postDelayed({ openContent() }, SHOW_TIME_MILLIS)
    }

    /**
     * Closes the splash and introduces the actual content of the app.
     */
    private fun openContent() {
        val intent = CountryListActivity.getCallingIntent(this)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        supportFinishAfterTransition()
    }

    override fun onPause() {
        handler.removeCallbacksAndMessages(null)
        super.onPause()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    companion object {
        const val SHOW_TIME_MILLIS = 1000L
    }
}
