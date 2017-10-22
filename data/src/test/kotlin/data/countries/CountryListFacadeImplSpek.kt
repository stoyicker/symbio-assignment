package data.countries

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.whenever
import dagger.Component
import dagger.Module
import dagger.Provides
import domain.country.Country
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import javax.inject.Singleton

/**
 * Unit tests for CountryListFacadeImpl.
 * @see CountryListFacadeImpl
 */
@RunWith(JUnitPlatform::class)
internal class CountryListFacadeImplSpek : SubjectSpek<CountryListFacadeImpl>({
    subject { CountryListFacadeImpl() }

    beforeEachTest {
        CountryComponentHolder.countryListFacadeComponent = DaggerCountryListFacadeModuleSpekComponent
                .builder()
                .countryListFacadeSpekModule(CountryListFacadeSpekModule(MOCK_ENTITY_MAPPER, MOCK_SOURCE))
                .build()
    }

    afterEachTest {
        reset(MOCK_ENTITY_MAPPER, MOCK_SOURCE)
    }

    it ("should return an observable of domain models upon successful fetch") {
        val countryOne = mock<DataCountry>()
        val countryTwo = mock<DataCountry>()
        val countryThree = mock<DataCountry>()
        val expectedTransformations = listOf<Country>(mock(), mock(), mock())
        whenever(MOCK_ENTITY_MAPPER.transform(eq(countryOne))) doReturn expectedTransformations[0]
        whenever(MOCK_ENTITY_MAPPER.transform(eq(countryTwo))) doReturn expectedTransformations[1]
        whenever(MOCK_ENTITY_MAPPER.transform(eq(countryThree))) doReturn expectedTransformations[2]
        whenever(MOCK_SOURCE.fetch()) doReturn Single.just(listOf(countryOne, countryTwo, countryThree))
        val testObserver = TestObserver<List<Country>>()
        subject.getCountries().subscribe(testObserver)
        testObserver.assertNoErrors()
        testObserver.assertValues(expectedTransformations)
        testObserver.assertComplete()
    }

    it ("should return an observable of domain models upon successful get") {
        val countryOne = mock<DataCountry>()
        val countryTwo = mock<DataCountry>()
        val countryThree = mock<DataCountry>()
        val expectedTransformations = listOf<Country>(mock(), mock(), mock())
        whenever(MOCK_ENTITY_MAPPER.transform(eq(countryOne))) doReturn expectedTransformations[0]
        whenever(MOCK_ENTITY_MAPPER.transform(eq(countryTwo))) doReturn expectedTransformations[1]
        whenever(MOCK_ENTITY_MAPPER.transform(eq(countryThree))) doReturn expectedTransformations[2]
        whenever(MOCK_SOURCE.get()) doReturn Single.just(listOf(countryOne, countryTwo, countryThree))
        val testObserver = TestObserver<List<Country>>()
        subject.getCountries().subscribe(testObserver)
        testObserver.assertNoErrors()
        testObserver.assertValues(expectedTransformations)
        testObserver.assertComplete()
    }

    it ("should return an observable with a propagated exception upon failed fetch") {
        val expectedError = mock<Exception>()
        whenever(MOCK_SOURCE.fetch()) doReturn Single.error(expectedError)
        val testObserver = TestObserver<List<Country>>()
        subject.getCountries().subscribe(testObserver)
        testObserver.assertNoValues()
        testObserver.assertError(expectedError)
        testObserver.assertNotComplete()
    }

    it ("should return an observable with a propagated exception upon failed get") {
        val expectedError = mock<Exception>()
        whenever(MOCK_SOURCE.get()) doReturn Single.error(expectedError)
        val testObserver = TestObserver<List<Country>>()
        subject.getCountries().subscribe(testObserver)
        testObserver.assertNoValues()
        testObserver.assertError(expectedError)
        testObserver.assertNotComplete()
    }
}) {
    private companion object {
        val MOCK_ENTITY_MAPPER = mock<CountryEntityMapper>()
        val MOCK_SOURCE = mock<CountryListRequestSource>()
    }
}

/**
 * The reason why we use a replacement component instead of inheritance in the module structure
 * is that such a solution could have some potentially bad consequences.
 * @see <a href="https://google.github.io/dagger/testing.html">Testing with Dagger</a>
 */
@Component(modules = arrayOf(CountryListFacadeSpekModule::class))
@Singleton
internal interface CountryListFacadeModuleSpekComponent : CountryListFacadeComponent

/**
 * Module used to provide stuff required by this Spek.
 */
@Module
internal class CountryListFacadeSpekModule(
        private val entityMapper: CountryEntityMapper,
        private val source: CountryListRequestSource) {
    @Provides
    @Singleton
    fun entityMapper() = entityMapper

    @Provides
    @Singleton
    fun countryListRequestSource() = source
}
