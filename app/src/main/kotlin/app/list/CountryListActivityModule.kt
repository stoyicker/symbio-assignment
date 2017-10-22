package app.list

import android.support.v7.widget.RecyclerView
import android.view.View
import dagger.Component
import dagger.Module
import dagger.Provides
import domain.exec.PostExecutionThread
import domain.interactor.TopGamingAllTimeFetchPostsUseCase
import domain.interactor.TopGamingAllTimeGetPostsUseCase
import domain.interactor.TopGamingAllTimePostsUseCase
import javax.inject.Singleton

/**
 * A component to inject instances of CountryListActivity.
 * @see CountryListActivityModule
 */
@Component(modules = arrayOf(CountryListActivityModule::class))
@Singleton
internal interface CountryListActivityComponent {
    fun inject(target: CountryListActivity)
}

/**
 * Module used to provide stuff required by CountryListActivity.
 * @see CountryListActivity
 */
@Module
internal class CountryListActivityModule constructor(
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
    fun topGamingAllTimePostsCoordinator(view: CountryListLoadableContentView,
                                         useCaseFactory: TopGamingAllTimePostsUseCase.Factory,
                                         countryPageLoadSubscriberFactory: CountryPageLoadSubscriber.Factory) =
            CountryListCoordinator(view, useCaseFactory, countryPageLoadSubscriberFactory)

    @Provides
    @Singleton
    fun topGamingAllTimePostsUseCaseFactory() = object : TopGamingAllTimePostsUseCase.Factory {
        override fun newFetch(page: Int, postExecutionThread: PostExecutionThread) =
                TopGamingAllTimeFetchPostsUseCase(page, postExecutionThread)

        override fun newGet(page: Int, postExecutionThread: PostExecutionThread) =
                TopGamingAllTimeGetPostsUseCase(page, postExecutionThread)
    }

    @Provides
    @Singleton
    fun topGamingAllTimePostsView() =
            CountryListLoadableContentView(contentView, errorView, progressView, guideView)

    @Provides
    @Singleton
    fun viewConfig(view: CountryListLoadableContentView) =
            CountryListViewConfig(view, interactionCallback)
}
