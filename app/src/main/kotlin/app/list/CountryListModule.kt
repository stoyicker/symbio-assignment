package app.list

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import app.detail.CountryDetailActivity
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
internal class CountryListModule(
        private val context: Context,
        private val contentView: RecyclerView,
        private val errorView: View,
        private val progressView: View,
        private val guideView: View) {
    @Provides
    @Singleton
    fun coordinatorBehaviorCallback(coordinator: CountryListCoordinator) =
            object : CountryListActivity.BehaviorCallback {
                @SuppressLint("InlinedApi")
                override fun onItemClicked(item: PresentationCountry) {
                    context.startActivity(CountryDetailActivity.getCallingIntent(context, item))
                }

                override fun onPageLoadRequested() {
                    coordinator.actionLoadNextPage()
                }
            }

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
    fun topGamingAllTimePostsView() = CountryListLoadableContentView(
            contentView, errorView, progressView, guideView)

    @Provides
    @Singleton
    fun viewConfig(
            view: CountryListLoadableContentView,
            callback: CountryListActivity.BehaviorCallback) = CountryListViewConfig(view, callback)
}
