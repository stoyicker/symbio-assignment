package app

import android.app.Application
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import app.detail.CountryDetailComponent
import app.detail.CountryDetailModule
import app.detail.DaggerCountryDetailComponent
import app.list.CountryListComponent
import app.list.CountryListModule
import app.list.CountryListViewConfig
import app.list.DaggerCountryListComponent
import com.squareup.picasso.Picasso
import org.jorge.assignment.app.BuildConfig

/**
 * Custom application.
 */
internal open class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setPicasso()
    }

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
            interactionCallback: CountryListViewConfig.InteractionCallback): CountryListComponent =
            DaggerCountryListComponent
                    .builder()
                    .countryListModule(
                            CountryListModule(
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
    open fun buildCountryDetailComponent(
            nameView: TextView,
            flagView: ImageView,
            detailView: TextView)
            : CountryDetailComponent = DaggerCountryDetailComponent.builder()
                    .countryDetailModule(CountryDetailModule(nameView, flagView, detailView))
                    .build()

    private fun setPicasso() = Picasso.Builder(this).apply {
        indicatorsEnabled(BuildConfig.DEBUG)
        Picasso.setSingletonInstance(this.build())
    }
}
