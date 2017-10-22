package domain.interactor

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.whenever
import domain.Domain
import domain.country.Country
import domain.country.CountryListFacade
import domain.country.FetchCountriesUseCase
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * Tests for the use case that fetches countries from th enetwork first.
 * @see FetchCountriesUseCase
 */
@RunWith(JUnitPlatform::class)
internal class FetchCountriesUseCaseSpek : SubjectSpek<FetchCountriesUseCase>({
    subject { FetchCountriesUseCase(PAGE, POST_EXECUTION_THREAD_SCHEDULE_IMMEDIATELY) }

    beforeEachTest {
        reset(MOCK_FACADE)
        Domain.countryListFacade(MOCK_FACADE)
    }

    it ("should build its implementation as an observable") {
        val values = listOf<Country>(mock(), mock(), mock())
        val testObserver = object : DisposableSingleObserver<List<Country>>() {
            override fun onSuccess(payload: List<Country>) {
                assertEquals(payload, values, "Values not as expected")
            }

            override fun onError(error: Throwable) {
                fail("An error occurred: $error")
            }
        }
        whenever(MOCK_FACADE.fetchCountries()) doReturn Single.just<List<Country>>(values)
        subject.execute(testObserver)
    }
}) {
    private companion object {
        private const val PAGE = 0
        private val POST_EXECUTION_THREAD_SCHEDULE_IMMEDIATELY = object : PostExecutionThread {
            override fun scheduler(): Scheduler = Schedulers.trampoline()
        }
        private val MOCK_FACADE = mock<CountryListFacade>()
    }
}
