package app

import android.app.Application
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import app.detail.CountryDetailModule
import app.detail.DaggerPostDetailFeatureComponent
import app.detail.PostDetailFeatureComponent
import app.gaming.DaggerTopGamingAllTimePostsFeatureComponent
import app.gaming.TopGamingAllTimePostsFeatureComponent
import app.gaming.TopGamingAllTimePostsFeatureModule

/**
 * Custom application.
 */
internal open class MainApplication : Application() {

    /**
     * Objects related to this feature can call this method to have its component created and access
     * a reference to it in order to inject itself.
     * @see app.gaming.TopGamingAllTimePostsActivity
     */
    open fun buildTopGamingAllTimePostsFeatureComponent(
            contentView: RecyclerView, errorView: View, progressView: View, guideView: View):
            TopGamingAllTimePostsFeatureComponent = DaggerTopGamingAllTimePostsFeatureComponent
            .builder()
            .topGamingAllTimePostsFeatureModule(TopGamingAllTimePostsFeatureModule(
                                    this, contentView, errorView, progressView, guideView))
            .build()


    /**
     * Objects related to this feature can call this method to have its component created and access
     * a reference to it in order to inject itself.
     * @see app.detail.CountryDetailActivity
     */
    open fun buildPostDetailFeatureComponent(textView: TextView, imageView: ImageView)
            : PostDetailFeatureComponent = DaggerPostDetailFeatureComponent.builder()
                    .countryDetailModule(CountryDetailModule(textView, imageView))
                    .build()
}
