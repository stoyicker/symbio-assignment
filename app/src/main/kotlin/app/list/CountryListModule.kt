package app.list

import android.support.v7.widget.RecyclerView
import android.view.View
import dagger.Component
import dagger.Module
import dagger.Provides
import domain.country.CountryListUseCase
import domain.country.FetchCountriesUseCase
import domain.country.GetCountriesUseCase
import domain.interactor.PostExecutionThread
import javax.inject.Singleton

/**
 * A component to inject instances of CountryListActivity.
 * @see CountryListModule
 */
@Component(modules = arrayOf(CountryListModule::class))
@Singleton
internal interface CountryListComponent {
    fun inject(target: CountryListActivity)
}

/**
 * Module used to provide stuff required by CountryListActivity.
 * @see CountryListActivity
 */
@Module
internal class CountryListModule constructor(
        private val contentView: RecyclerView,
        private val errorView: View,
        private val progressView: View,
        private val guideView: View,
        private val interactionCallback: CountryListViewConfig.InteractionCallback) {
    @Provides
    @Singleton
    fun presentationCountryEntityMapper() = PresentationCountryEntityMapper()

    @Provides
    @Singleton
    fun pageLoadSubscriberFactory(entityMapper: PresentationCountryEntityMapper) =
            object : CountryPageLoadSubscriber.Factory {
                override fun newSubscriber(coordinator: CountryListCoordinator) =
                        CountryPageLoadSubscriber(coordinator, entityMapper)
    }

    @Provides
    @Singleton
    fun countryListCoordinator(
            view: CountryListLoadableContentView,
            useCaseFactory: CountryListUseCase.Factory,
            countryPageLoadSubscriberFactory: CountryPageLoadSubscriber.Factory) =
            CountryListCoordinator(view, useCaseFactory, countryPageLoadSubscriberFactory)

    @Provides
    @Singleton
    fun countryListUseCaseFactory() = object : CountryListUseCase.Factory {
        override fun newFetch(page: Int, postExecutionThread: PostExecutionThread) =
                FetchCountriesUseCase(page, postExecutionThread)

        override fun newGet(page: Int, postExecutionThread: PostExecutionThread) =
                GetCountriesUseCase(page, postExecutionThread)
    }

    @Provides
    @Singleton
    fun countryListView() =
            CountryListLoadableContentView(contentView, errorView, progressView, guideView)

    @Provides
    @Singleton
    fun viewConfig(view: CountryListLoadableContentView) =
            CountryListViewConfig(view, interactionCallback)
}
