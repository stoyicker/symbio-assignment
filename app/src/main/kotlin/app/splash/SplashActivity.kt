package app.splash

import android.content.Intent
import android.os.Handler
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import app.common.UIPostExecutionThread
import app.list.CountryListActivity
import domain.country.Country
import domain.country.FetchCountriesUseCase
import io.reactivex.observers.DisposableSingleObserver

/**
 * A simple activity that acts as a splash screen.
 * Note how, instead of using the content view to set the splash, we just set the splash as
 * background in the theme. This allows it to be shown without having to wait for the content view
 * to be drawn.
 */
internal class SplashActivity : AppCompatActivity() {
    private var handler: Handler? = null

    override fun onResume() {
        super.onResume()
        scheduleContentOpening()
    }

    /**
     * Performs pre-fetching and schedules the app content to be shown.
     */
    private fun scheduleContentOpening() {
        // 'Pre-fetch'. The way this is implemented is a bit arguable, due to it being inside the
        // activity and instantiated directly. That's coupling with framework classes, which is bad.
        // It would be delegated to a coordinator of its own if I was a bit less tired :P
        FetchCountriesUseCase(0, UIPostExecutionThread).execute(
                object : DisposableSingleObserver<List<Country>>() {
                    override fun onSuccess(ignored: List<Country>) { prepareToMoveOn() }

                    override fun onError(ignored: Throwable) { prepareToMoveOn() }

                    private fun prepareToMoveOn() {
                        handler = Handler()
                        handler!!.postDelayed({ openContent() }, SHOW_TIME_MILLIS)
                    }
                })
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
        handler?.removeCallbacksAndMessages(null)
        super.onPause()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    companion object {
        const val SHOW_TIME_MILLIS = 600L
    }
}
