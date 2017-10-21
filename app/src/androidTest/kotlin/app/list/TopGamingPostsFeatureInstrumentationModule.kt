package app.list

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import app.common.UIPostExecutionThread
import app.detail.CountryDetailActivity
import app.list.TopGamingActivityInstrumentation.Companion.SUBJECT
import app.list.TopGamingActivityInstrumentation.Companion.SUBSCRIBER_GENERATOR
import dagger.Component
import dagger.Module
import dagger.Provides
import domain.entity.Country
import domain.exec.PostExecutionThread
import domain.interactor.TopGamingAllTimePostsUseCase
import io.reactivex.Single
import javax.inject.Singleton

/**
 * Module used to provide stuff required by this test.
 */
@Module
internal class TopGamingAllTimePostsFeatureInstrumentationModule(
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
    fun pageLoadSubscriberFactory() = object : CountryPageLoadSubscriber.Factory {
        override fun newSubscriber(coordinator: CountryListCoordinator) =
                SUBSCRIBER_GENERATOR(coordinator)
    }

    @Provides
    @Singleton
    fun topGamingAllTimePostsCoordinator(view: CountryListLoadableContentView,
                                         useCaseFactory: TopGamingAllTimePostsUseCase.Factory,
                                         countryPageLoadSubscriberFactory: CountryPageLoadSubscriber.Factory) =
            CountryListCoordinator(view, useCaseFactory, countryPageLoadSubscriberFactory)

    @Provides
    @Singleton
    fun topGamingAllTimePostsUseCaseFactory(): TopGamingAllTimePostsUseCase.Factory =
        object : TopGamingAllTimePostsUseCase.Factory {
            override fun newFetch(page: Int, postExecutionThread: PostExecutionThread) =
                object : TopGamingAllTimePostsUseCase(page, UIPostExecutionThread) {
                    override fun buildUseCase(): Single<List<Country>> = SUBJECT.singleOrError()
                }

            override fun newGet(page: Int, postExecutionThread: PostExecutionThread) =
                newFetch(page, postExecutionThread)
    }

    @Provides
    @Singleton
    fun topGamingAllTimePostsView() = CountryListLoadableContentView(
            contentView, errorView, progressView, guideView)

    @Provides
    @Singleton
    fun viewConfig(
            view: CountryListLoadableContentView,
            callback: CountryListActivity.BehaviorCallback) =
            CountryListViewConfig(view, callback)
}

/**
 * The reason why we use a replacement component instead of inheritance in the module structure
 * is that such a solution could have some potentially bad consequences.
 * @see <a href="https://google.github.io/dagger/testing.html">Testing with Dagger</a>
 */
@Component(modules = arrayOf(TopGamingAllTimePostsFeatureInstrumentationModule::class))
@Singleton
internal interface CountryListInstrumentationComponent
    : CountryListComponent
