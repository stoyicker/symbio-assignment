package app.list

import android.support.v7.widget.RecyclerView
import android.view.View
import app.common.UIPostExecutionThread
import app.list.CountryListActivityInstrumentation.Companion.SUBJECT
import app.list.CountryListActivityInstrumentation.Companion.SUBSCRIBER_GENERATOR
import dagger.Component
import dagger.Module
import dagger.Provides
import domain.country.Country
import domain.country.CountryListUseCase
import domain.interactor.PostExecutionThread
import io.reactivex.Single
import javax.inject.Singleton

/**
 * The reason why we use a replacement component instead of inheritance in the module structure
 * is that such a solution could have some potentially bad consequences.
 * @see <a href="https://google.github.io/dagger/testing.html">Testing with Dagger</a>
 */
@Component(modules = arrayOf(CountryListInstrumentationModule::class))
@Singleton
internal interface CountryListInstrumentationComponent : CountryListComponent

/**
 * Module used to provide stuff required by this test.
 */
@Module
internal class CountryListInstrumentationModule(
        private val contentView: RecyclerView,
        private val errorView: View,
        private val progressView: View,
        private val guideView: View,
        private val interactionCallback: CountryListViewConfig.InteractionCallback) {
    @Provides
    @Singleton
    fun pageLoadSubscriberFactory() = object : CountryPageLoadObserver.Factory {
        override fun newSubscriber(coordinator: CountryListCoordinator) =
                SUBSCRIBER_GENERATOR(coordinator)
    }

    @Provides
    @Singleton
    fun countryListCoordinator(view: CountryListLoadableContentView,
                               useCaseFactory: CountryListUseCase.Factory,
                               countryPageLoadObserverFactory: CountryPageLoadObserver.Factory) =
            CountryListCoordinator(view, useCaseFactory, countryPageLoadObserverFactory)

    @Provides
    @Singleton
    fun countryListUseCaseFactory(): CountryListUseCase.Factory =
        object : CountryListUseCase.Factory {
            override fun newFetch(page: Int, postExecutionThread: PostExecutionThread) =
                object : CountryListUseCase(page, UIPostExecutionThread) {
                    override fun buildUseCase(): Single<List<Country>> = SUBJECT.singleOrError()
                }

            override fun newGet(page: Int, postExecutionThread: PostExecutionThread) =
                newFetch(page, postExecutionThread)
    }

    @Provides
    @Singleton
    fun countryListView() = CountryListLoadableContentView(
            contentView, errorView, progressView, guideView)

    @Provides
    @Singleton
    fun viewConfig(view: CountryListLoadableContentView) =
            CountryListViewConfig(view, interactionCallback)
}
