package app.detail

import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import org.mockito.Mockito.mock
import javax.inject.Singleton

/**
 * Module used to provide stuff required by CountryDetailActivity.
 * @see CountryDetailActivity
 */
@Module
internal class PostDetailFeatureInstrumentationModule(private val context: Context) {
    @Provides
    fun postDetailView(): CountryDetailView = mock(CountryDetailView::class.java)
}

/**
 * A component to inject instances that require access to dependencies provided by
 * CountryDetailModule.
 * @see CountryDetailModule
 */
@Component(modules = arrayOf(PostDetailFeatureInstrumentationModule::class))
@Singleton
internal interface PostDetailFeatureInstrumentationComponent : PostDetailFeatureComponent

