package domain.country

import domain.Domain
import domain.interactor.PostExecutionThread

/**
 * A use case for getting the countries (that is, looking first at the cache).
 */
class GetCountriesUseCase(page: Int, postExecutionThread: PostExecutionThread)
    : CountryListUseCase(page, postExecutionThread) {
    override fun buildUseCase() = Domain.countryListFacade.getCountries()
}
