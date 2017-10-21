package domain.repository

import domain.entity.Country
import io.reactivex.Single

/**
 * This describes all this module needs to know about how the data coming from outside is obtained.
 */
interface CountryListFacade {
    /**
     * Fetch top posts from a subreddit.
     */
    fun fetchCountries(): Single<List<Country>>
    /**
     * Get top posts from a subreddit.
     */
    fun getCountries(): Single<List<Country>>
}
