package data.countries

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.observers.TestObserver
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.jorge.assignment.data.BuildConfig
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import kotlin.test.assertTrue

/**
 * Integration test to guarantee validity of the actual endpoint, request formation and model.
 */
@RunWith(JUnitPlatform::class)
internal class CountryListRequestSourceIntegrationSpek : SubjectSpek<CountryListRequestSource>({
    subject { CountryListRequestSource() }

    it ("should always fetch models with non-empty values for the attributes kept") {
        val retrofit: CountryApiService = Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .validateEagerly(true)
                .build()
                .create(CountryApiService::class.java)
        val testObserver = TestObserver<List<DataCountry>>()
        val moshi = Moshi.Builder().build()
        retrofit.allCountries()
                .map { moshi.adapter<List<DataCountry>>(
                        Types.newParameterizedType(List::class.java, DataCountry::class.java))
                        .fromJson(it.string()) }
                .subscribe(testObserver)
        testObserver.assertNoErrors()
        @Suppress("UNCHECKED_CAST")
        (testObserver.events.first() as List<List<DataCountry>>).first().first().apply {
            assertTrue { name.isNotBlank() }
            assertTrue { nativeName.isNotBlank() }
            assertTrue { region.isNotBlank() }
            assertTrue { capital.isNotBlank() }
            assertTrue { area!!.isNotBlank() }
            assertTrue { languages.isNotEmpty() }
            assertTrue { translations.isNotEmpty() }
            assertTrue { alpha2Code.isNotBlank() }
        }
        testObserver.assertComplete()
    }
})
