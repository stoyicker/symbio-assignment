package domain

import domain.repository.CountryListFacade
import io.reactivex.schedulers.Schedulers

/**
 * Global configuration holder for the module.
 * Note how this class acts as a dependency holder. You could also a DI framework like Dagger for
 * example, but to only provide a single dependency, which is also a singleton, might as well do it
 * myself instead.
 */
object Domain {
    internal lateinit var countryListFacade: CountryListFacade
    internal val useCaseScheduler = Schedulers.io()

    /**
     * Set an implemented facade.
     * @param facade The facade to set.
     */
    fun countryListFacade(facade: CountryListFacade) { this.countryListFacade = facade }
}
