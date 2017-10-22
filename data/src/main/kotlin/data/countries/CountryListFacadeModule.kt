package data.countries

import dagger.Component
import dagger.Module
import dagger.Provides
import domain.country.CountryListFacade
import javax.inject.Singleton

/**
 * A component to inject CountryListFacade instances,
 * @see CountryListFacade
 */
@Component(modules = arrayOf(CountryListFacadeModule::class))
@Singleton
internal interface CountryListFacadeComponent {
    fun inject(target: CountryListFacade)
}

/**
 * Module used to provide stuff required by CountryListFacade objects.
 */
@Module
internal class CountryListFacadeModule {
    @Provides
    @Singleton
    fun entityMapper() = CountryEntityMapper()

    @Provides
    @Singleton
    fun topRequestSource() = CountryListRequestSource()
}
