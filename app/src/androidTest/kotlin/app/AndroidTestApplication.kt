package app

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import app.detail.CountryDetailComponent
import app.detail.CountryDetailInstrumentationModule
import app.detail.DaggerCountryDetailInstrumentationComponent
import app.list.CountryListComponent
import app.list.CountryListInstrumentationModule
import app.list.CountryListViewConfig
import app.list.DaggerCountryListInstrumentationComponent

/**
 * Custom application.
 */
internal open class AndroidTestApplication : MainApplication() {
    override fun buildCountryListActivityComponent(
            contentView: RecyclerView,
            errorView: View,
            progressView: View,
            guideView: View,
            interactionCallback: CountryListViewConfig.InteractionCallback): CountryListComponent =
            DaggerCountryListInstrumentationComponent.builder()
                    .countryListInstrumentationModule(
                            CountryListInstrumentationModule(
                                    contentView = contentView,
                                    errorView = errorView,
                                    progressView = progressView,
                                    guideView = guideView,
                                    interactionCallback = interactionCallback))
                    .build()

    override fun buildCountryDetailComponent(
            nameView: TextView,
            flagView: ImageView,
            detailView: TextView): CountryDetailComponent =
            DaggerCountryDetailInstrumentationComponent.builder()
                    .countryDetailInstrumentationModule(CountryDetailInstrumentationModule())
                    .build()
}
