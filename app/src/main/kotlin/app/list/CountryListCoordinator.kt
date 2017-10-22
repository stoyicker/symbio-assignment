package app.list

import app.common.UIPostExecutionThread
import domain.country.CountryListUseCase

/**
 * Takes care of binding the logic of the country list request to the view that handles its
 * outcome.
 * @param view The view associated to this object.
 */
internal class CountryListCoordinator(
        internal val view: CountryListLoadableContentView,
        private val useCaseFactory: CountryListUseCase.Factory,
        private val countryPageLoadSubscriberFactory: CountryPageLoadSubscriber.Factory) {
    internal var page = 0
    private var ongoingUseCase: CountryListUseCase? = null

    /**
     * Triggers the load of the next page.
     * @param requestedManually In order to decide whether or not to resort to the cache, a boolean
     * indicating if this load was triggered manually. Defaults to <code>false</code>, which
     * resorts to memory and disk cache, checking for data availability in that order.
     */
    fun actionLoadNextPage(requestedManually: Boolean = true) {
        val ongoingUseCase = if (requestedManually) {
            useCaseFactory.newFetch(page, UIPostExecutionThread)
        } else {
            useCaseFactory.newGet(page, UIPostExecutionThread)
        }
        ongoingUseCase.execute(countryPageLoadSubscriberFactory.newSubscriber(this))
    }

    /**
     * Aborts the on-going next page load, if any.
     */
    fun abortActionLoadNextPage() {
        ongoingUseCase?.dispose()
    }
}
