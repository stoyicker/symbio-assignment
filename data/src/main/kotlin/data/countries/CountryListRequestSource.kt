package data.countries

import com.nytimes.android.external.store3.base.impl.Store
import data.ComponentHolder
import javax.inject.Inject
import dagger.Lazy as DaggerLazy

/**
 * Contains the data source for top requests.
 */
internal class CountryListRequestSource {
    // Dagger does not play well with providing Kotlin's Lazy instances, so we do a workaround by
    // targeting a holder from Dagger's Lazy and then converting it to Kotlin's.
    // Copying the actual value to a different reference holder instead of requesting it through the
    // accessor every time trades off a slightly higher on-paper peak RAM consumption to minimize
    // virtual method usage.
    // See https://developer.android.com/training/articles/perf-tips.html#GettersSetters for a
    // similar use case with internal getters and setters.
    @Inject
    lateinit var storeAccessor: DaggerLazy<Store<List<DataCountry>, Unit>>
    private val store: Store<List<DataCountry>, Unit> by lazy { storeAccessor.get() }

    init {
        ComponentHolder.countryListRequestSourceComponent.inject(this)
    }

    /**
     * Delegates to its internal responsible for the request. Cache is ignored, but updated on
     * success. On failure, cache is the fallback.
     * @see Store
     */
    internal fun fetch() = store.fetch(Unit).onErrorResumeNext { store.get(Unit) }

    /**
     * Delegates to its internal responsible for the request. Cache checks: memory > disk > network.
     * @see Store
     */
    internal fun get() = store.get(Unit)
}
