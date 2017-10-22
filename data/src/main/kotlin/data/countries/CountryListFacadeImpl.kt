package data.countries

import data.ComponentHolder
import domain.country.CountryListFacade
import io.reactivex.Single
import javax.inject.Inject

/**
 * Exposes entry points to top data requests. If we were to have more requests, it would probably be
 * a good idea to have more facades, not necessarily on a 1:1 countryListFacade-request proportion,
 * but just so we don't end up exposing too many requests in the same countryListFacade.
 * Having a countryListFacade might seem like "too much decoupling" (if such a thing exists) since we
 * already have a dedicated data module, but it eases separating unit from integration tests,
 * since the request sources, like CountryListSource, are just a layer of caching that is easily
 * tested built directly on top of third-party dependencies that are trusted to be tested.
 */
internal class CountryListFacadeImpl : CountryListFacade {
    @Inject
    lateinit var entityMapper: CountryEntityMapper
    @Inject
    lateinit var source: CountryListRequestSource

    init {
        ComponentHolder.countryListFacadeComponent.inject(this)
    }

    /**
     * Fetches the list of countries, first trying the network and then falling back to cache. All
     * caches are updated on success. Use when getting the latest content is more important than a
     * fast and reliable response.
     */
    override fun fetchCountries() = transform(source.fetch())

    /**
     * Fetches the list of countries, first trying the cache and then falling back to the network.
     * Use when a fast and reliable response is more important than obtaining the latest content.
     */
    override fun getCountries() = transform(source.get())

    /**
     * Requests mapping to be applied to some data to send it upwards.
     * @param data The data to map as seen by this layer.
     */
    private fun transform(data: Single<List<DataCountry>>) =
            data.map { it.map { entityMapper.transform(it) } }
}
