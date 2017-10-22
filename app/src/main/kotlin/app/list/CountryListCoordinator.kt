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
     */
    fun actionLoadNextPage() {
        ongoingUseCase = useCaseFactory.newGet(page, UIPostExecutionThread)
        ongoingUseCase?.execute(countryPageLoadSubscriberFactory.newSubscriber(this))
    }

    /**
     * Aborts the on-going next page load, if any.
     */
    fun abortActionLoadNextPage() {
        ongoingUseCase?.dispose()
    }
}
