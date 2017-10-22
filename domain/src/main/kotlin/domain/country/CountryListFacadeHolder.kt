package domain.country

/**
 * This class acts as a dependency holder for the feature. You could also a DI framework like Dagger
 * for example, but to only provide a single dependency, which is also a singleton, might as well do
 * it myself instead.
 */
object CountryListFacadeHolder {
    internal lateinit var countryListFacade: CountryListFacade

    /**
     * Set an implemented facade.
     * @param facade The facade to set.
     */
    fun countryListFacade(facade: CountryListFacade) { countryListFacade = facade }
}
