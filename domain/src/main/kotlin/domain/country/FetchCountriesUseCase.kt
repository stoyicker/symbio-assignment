package domain.country

import domain.interactor.PostExecutionThread

/**
 * A use case for fetching the countries (that is, looking first at the network, if available).
 */
class FetchCountriesUseCase(page: Int, postExecutionThread: PostExecutionThread)
    : CountryListUseCase(page, postExecutionThread) {
    override fun buildUseCase() = CountryListFacadeHolder.countryListFacade.fetchCountries()
}
