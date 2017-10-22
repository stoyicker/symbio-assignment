package domain.interactor

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.whenever
import domain.Domain
import domain.country.Country
import domain.country.CountryListFacade
import domain.country.GetCountriesUseCase
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
 * Tests for the use case that gets countries from the cache first.
 * @see GetCountriesUseCase
 */
@RunWith(JUnitPlatform::class)
internal class GetCountriesUseCaseSpek : SubjectSpek<GetCountriesUseCase>({
    subject { GetCountriesUseCase(PAGE, POST_EXECUTION_THREAD_SCHEDULE_IMMEDIATELY) }

    beforeEachTest {
        reset(MOCK_FACADE)
        Domain.countryListFacade(MOCK_FACADE)
    }

    it ("should build its implementation as an observable") {
        // Cannot mock Post as it is a data class
        val values = listOf<Country>(mock(), mock(), mock())
        val testSubscriber = object : DisposableSingleObserver<List<Country>>() {
            override fun onSuccess(payload: List<Country>) {
                assertEquals(payload, values, "Values not as expected")
            }

            override fun onError(error: Throwable) {
                fail("An error occurred: $error")
            }
        }
        whenever(MOCK_FACADE.getCountries()) doReturn Single.just<List<Country>>(values)
        subject.execute(testSubscriber)
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
