package app

import android.app.Application
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import app.detail.CountryDetailModule
import app.detail.DaggerPostDetailFeatureComponent
import app.detail.PostDetailFeatureComponent
import app.list.CountryListActivityComponent
import app.list.CountryListActivityModule
import app.list.CountryListViewConfig
import app.list.DaggerCountryListActivityComponent

/**
 * Custom application.
 */
internal open class MainApplication : Application() {

    /**
     * Objects related to this feature can call this method to have its component created and access
     * a reference to it in order to inject itself.
     * @see app.list.CountryListActivity
     */
    open fun buildCountryListActivityComponent(
            contentView: RecyclerView,
            errorView: View,
            progressView: View,
            guideView: View,
            interactionCallback: CountryListViewConfig.InteractionCallback): CountryListActivityComponent =
            DaggerCountryListActivityComponent
                    .builder()
                    .countryListActivityModule(
                            CountryListActivityModule(
                                    contentView = contentView,
                                    errorView = errorView,
                                    progressView = progressView,
                                    guideView = guideView,
                                    interactionCallback = interactionCallback))
                    .build()


    /**
     * Objects related to this feature can call this method to have its component created and access
     * a reference to it in order to inject itself.
     * @see app.detail.CountryDetailActivity
     */
    open fun buildCountryDetailComponent(textView: TextView, imageView: ImageView)
            : PostDetailFeatureComponent = DaggerPostDetailFeatureComponent.builder()
                    .countryDetailModule(CountryDetailModule(textView, imageView))
                    .build()
}
