package domain.country

import io.reactivex.Single

/**
 * This describes all this module needs to know about how the data coming from outside is obtained.
 */
interface CountryListFacade {
    /**
     * Fetch the country list.
     */
    fun fetchCountries(): Single<List<Country>>
    /**
     * Get the country list.
     */
    fun getCountries(): Single<List<Country>>
}
