package data.countries

import com.nytimes.android.external.fs3.FileSystemPersister
import com.nytimes.android.external.fs3.filesystem.FileSystemFactory
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.FluentStoreBuilder
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.middleware.moshi.MoshiParserFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.Component
import dagger.Module
import dagger.Provides
import data.NetworkModule
import okio.BufferedSource
import retrofit2.Retrofit
import java.io.File
import javax.inject.Singleton

/**
 * A component to inject instances that require access to data provided by CountryListRequestSourceModule.
 * @see CountryListRequestSourceModule
 */
@Component(modules = arrayOf(NetworkModule::class, CountryListRequestSourceModule::class))
@Singleton
internal interface CountryListRequestSourceComponent {
    fun inject(target: CountryListRequestSource)
}

/**
 * Module used to provide stuff required by CountryListRequestSource objects.
 */
@Module(includes = arrayOf(NetworkModule::class))
internal class CountryListRequestSourceModule(private val cacheDir: File) {
    @Provides
    @Singleton
    fun cacheDir() = cacheDir

    @Provides
    @Singleton
    fun apiServiceAccessor(retrofit: Retrofit) = retrofit.create(CountryApiService::class.java)

    @Provides
    @Singleton
    fun store(apiService: CountryApiService) =
            // We want to have long-term caching, since this data does not change often. Therefore
            // we are fine using the default memory cache implementation which expires items 24h
            // after acquisition.
            // We will also use disk caching to prepare against connectivity-related problems,
            // but we will default to checking the network because on app opening it is
            // reasonable to expected that, if network connectivity available, the data shown
            // should be the latest.
            FluentStoreBuilder.parsedWithKey<Unit, BufferedSource, List<DataCountry>>(
                    Fetcher { fetcher(apiService) }) {
                parsers = listOf(MoshiParserFactory.createSourceParser<List<DataCountry>>(Moshi.Builder().build().apply {
                    adapter<List<DataCountry>>(Types.newParameterizedType(List::class.java, DataCountry::class.java))
                }, List::class.java))
                persister = FileSystemPersister.create(FileSystemFactory.create(cacheDir)) { it.toString() }
                // When the disk data is stale, never try to retry from network since the likelihood
                // of a change is extremely low and it is not required because we do it a fresh
                // fetch on app launch anyway.
                stalePolicy = StalePolicy.REFRESH_ON_STALE
            }

    /**
     * Provides a Fetcher for the store.
     * @see Fetcher
     */
    private fun fetcher(apiService: CountryApiService) =
            apiService.allCountries().map { it.source() }
}
