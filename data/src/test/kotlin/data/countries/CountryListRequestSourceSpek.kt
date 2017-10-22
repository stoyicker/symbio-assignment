package data.countries

import com.nhaarman.mockito_kotlin.anyVararg
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.nytimes.android.external.store3.base.impl.Store
import dagger.Component
import dagger.Module
import dagger.Provides
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import retrofit2.Retrofit
import java.io.File
import javax.inject.Singleton

/**
 * Unit tests for cache cleanup.
 */
@RunWith(JUnitPlatform::class)
internal class CountryListRequestSourceSpek : SubjectSpek<CountryListRequestSource>({
    subject { CountryListRequestSource() }

    beforeEachTest {
        CountryComponentHolder.countryListRequestSourceComponent = DaggerCountryListRequestSourceSpekComponent
                .builder()
                .countryListRequestSourceSpekModule(CountryListRequestSourceSpekModule(CACHE_DIR, MOCK_STORE))
                .build()
    }

    afterEachTest {
        CACHE_DIR.deleteRecursively()
        reset(MOCK_STORE)
    }

    it ("should fall back to the cache on failed fetch") {
        val value = mock<List<DataCountry>>()
        whenever(MOCK_STORE.fetch(anyVararg())) doReturn Single.error(mock<Exception>())
        whenever(MOCK_STORE.get(anyVararg())) doReturn Single.just(value)
        val testObserver = TestObserver<List<DataCountry>>()
        subject.fetch().subscribe(testObserver)
        verify(MOCK_STORE).fetch(anyVararg())
        testObserver.assertValue(value)
        testObserver.assertComplete()
    }

    it ("should fall back to the cache on failed fetch without propagating the error when the cache is not empty") {
        val cachedValue = mock<List<DataCountry>>()
        val fetchError = mock<Exception>()
        whenever(MOCK_STORE.fetch(anyVararg())) doReturn Single.error(fetchError)
        whenever(MOCK_STORE.get(anyVararg())) doReturn Single.just(cachedValue)
        val testObserver = TestObserver<List<DataCountry>>()
        subject.fetch().subscribe(testObserver)
        verify(MOCK_STORE).fetch(anyVararg())
        verify(MOCK_STORE).get(anyVararg())
        testObserver.assertNoErrors()
        testObserver.assertValue(cachedValue)
        testObserver.assertComplete()
    }
}) {
    private companion object {
        val CACHE_DIR = File("build/test-generated/")
        val MOCK_STORE = mock<Store<List<DataCountry>, Unit>>()
    }
}

/**
 * Module used to provide stuff required by this spek.
 */
@Module
internal class CountryListRequestSourceSpekModule(
        private val cacheDir: File, private val store: Store<List<DataCountry>, Unit>) {
    @Provides
    fun networkInterface() = mock<Retrofit>()

    @Provides
    fun cacheDir() = cacheDir

    @Provides
    @Singleton
    fun store() = store
}

/**
 * The reason why we use a replacement component instead of inheritance in the module structure
 * is that such a solution could have some potentially bad consequences.
 * @see <a href="https://google.github.io/dagger/testing.html">Testing with Dagger</a>
 */
@Component(modules = arrayOf(CountryListRequestSourceSpekModule::class))
@Singleton
internal interface CountryListRequestSourceSpekComponent : CountryListRequestSourceComponent
