package app

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import app.detail.DaggerPostDetailFeatureInstrumentationComponent
import app.detail.PostDetailFeatureComponent
import app.detail.PostDetailFeatureInstrumentationModule
import app.list.CountryListActivityComponent
import app.list.CountryListActivityInstrumentationModule
import app.list.CountryListViewConfig
import app.list.DaggerCountryListActivityInstrumentationActivityComponent

/**
 * Custom application.
 */
internal open class AndroidTestApplication : MainApplication() {
    override fun buildCountryListActivityComponent(
            contentView: RecyclerView,
            errorView: View,
            progressView: View,
            guideView: View,
            interactionCallback: CountryListViewConfig.InteractionCallback): CountryListActivityComponent =
            DaggerCountryListActivityInstrumentationActivityComponent.builder()
                    .countryListActivityInstrumentationModule(
                            CountryListActivityInstrumentationModule(
                                    contentView = contentView,
                                    errorView = errorView,
                                    progressView = progressView,
                                    guideView = guideView,
                                    interactionCallback = interactionCallback))
                    .build()

    override fun buildCountryDetailComponent(textView: TextView, imageView: ImageView)
            : PostDetailFeatureComponent = DaggerPostDetailFeatureInstrumentationComponent.builder()
            .postDetailFeatureInstrumentationModule(PostDetailFeatureInstrumentationModule(this))
            .build()
}
