package app.detail

import android.widget.ImageView
import android.widget.TextView
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * A component to inject instances of CountryDetailActivity.
 * @see CountryDetailModule
 */
@Component(modules = arrayOf(CountryDetailModule::class))
@Singleton
internal interface PostDetailFeatureComponent {
    fun inject(target: CountryDetailActivity)
}

/**
 * Module used to provide stuff required by CountryDetailActivity.
 * @see CountryDetailActivity
 */
@Module
internal class CountryDetailModule(
        private val textView: TextView,
        private val imageView: ImageView) {
    @Provides
    fun postDetailView() = CountryDetailView(textView, imageView)
}
