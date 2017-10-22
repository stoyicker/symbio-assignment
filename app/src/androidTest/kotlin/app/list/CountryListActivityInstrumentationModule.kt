package app.list

import android.support.v7.widget.RecyclerView
import android.view.View
import app.common.UIPostExecutionThread
import app.list.CountryListActivityInstrumentation.Companion.SUBJECT
import app.list.CountryListActivityInstrumentation.Companion.SUBSCRIBER_GENERATOR
import dagger.Component
import dagger.Module
import dagger.Provides
import domain.entity.Country
import domain.exec.PostExecutionThread
import domain.interactor.TopGamingAllTimePostsUseCase
import io.reactivex.Single
import javax.inject.Singleton

/**
 * The reason why we use a replacement component instead of inheritance in the module structure
 * is that such a solution could have some potentially bad consequences.
 * @see <a href="https://google.github.io/dagger/testing.html">Testing with Dagger</a>
 */
@Component(modules = arrayOf(CountryListActivityInstrumentationModule::class))
@Singleton
internal interface CountryListActivityInstrumentationActivityComponent : CountryListActivityComponent

/**
 * Module used to provide stuff required by this test.
 */
@Module
internal class CountryListActivityInstrumentationModule(
        private val contentView: RecyclerView,
        private val errorView: View,
        private val progressView: View,
        private val guideView: View,
        private val interactionCallback: CountryListViewConfig.InteractionCallback) {
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
    fun viewConfig(view: CountryListLoadableContentView) =
            CountryListViewConfig(view, interactionCallback)
}
