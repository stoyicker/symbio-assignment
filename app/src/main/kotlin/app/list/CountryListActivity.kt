package app.list

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import app.MainApplication
import app.detail.CountryDetailActivity
import kotlinx.android.synthetic.main.activity_list.root
import kotlinx.android.synthetic.main.include_list_view.content
import kotlinx.android.synthetic.main.include_list_view.error
import kotlinx.android.synthetic.main.include_list_view.progress
import kotlinx.android.synthetic.main.include_list_view.scroll_guide
import kotlinx.android.synthetic.main.include_toolbar.toolbar
import org.jorge.assignment.app.R
import javax.inject.Inject

/**
 * An Activity that shows a country list.
 */
internal class CountryListActivity : CountryListViewConfig.InteractionCallback, AppCompatActivity() {
    @Inject
    lateinit var viewConfig: CountryListViewConfig
    @Inject
    lateinit var coordinator: CountryListCoordinator
    private lateinit var filterFeatureDelegate: FilterFeature

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        revealLayout()
        inject()
        setSupportActionBar(toolbar)
        requestLoad()
    }

    /**
     * This gets called before a configuration change happens, so we use it to prevent leaking
     * the observable in the use case. It does not get called when the process finishes abnormally,
     * bun in that case there is no leak to worry about.
     */
    override fun onDestroy() {
        super.onDestroy()
        coordinator.abortActionLoadNextPage()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_gaming, menu)
        filterFeatureDelegate = FilterFeature(this,
                menu.findItem(R.id.search).actionView as SearchView, viewConfig)
        filterFeatureDelegate.applyQuery(intent.getStringExtra(KEY_QUERY))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.search) onSearchRequested()
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        if (isChangingConfigurations) {
            intent.putExtra(KEY_ENABLE_ENTER_ANIMATION, false)
            intent.putExtra(KEY_QUERY, filterFeatureDelegate.query)
        }
        super.onStop()
    }

    /**
     * Requests the next item batch to load.
     */
    private fun requestLoad() {
        coordinator.actionLoadNextPage()
        intent.putExtra(CountryListActivity.KEY_STARTED_MANUALLY, false)
    }

    /**
     * Reveals the layout using a circular reveal (if API level allows).
     */
    @SuppressLint("NewApi") // False positive
    private fun revealLayout() {
        root.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && intent.getBooleanExtra(KEY_ENABLE_ENTER_ANIMATION, true)) {
            root.apply {
                post {
                    val cx = width / 2
                    val cy = 0
                    val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
                    ViewAnimationUtils.createCircularReveal(this , cx, cy, 0f, finalRadius).start()
                }
            }
        }
    }

    /**
     * Injects this instance with the corresponding feature component.
     */
    private fun inject() {
        (application as MainApplication).buildCountryListActivityComponent(
                // https://kotlinlang.org/docs/tutorials/android-plugin.html#using-kotlin-android-extensions
                contentView = content,
                errorView = error,
                progressView = progress,
                guideView = scroll_guide,
                interactionCallback = this)
                .inject(this)
    }

    override fun onItemClicked(item: PresentationCountry) {
        startActivity(CountryDetailActivity.getCallingIntent(this, item))
    }

    override fun onPageLoadRequested() {
        coordinator.actionLoadNextPage()
    }

    companion object {
        private const val KEY_ENABLE_ENTER_ANIMATION = "org.jorge.assignment.KEY_ENABLE_ENTER_ANIMATION"
        private const val KEY_QUERY = "org.jorge.assignment.KEY_QUERY"
        private const val KEY_STARTED_MANUALLY = "org.jorge.assignment.KEY_STARTED_MANUALLY"

        /**
         * Safe way to obtain an intent to route to this activity. More useful if it were to have
         * more parameters for example, but a good idea to have nevertheless.
         * @param context The context to start this activity from.
         */
        fun getCallingIntent(context: Context): Intent {
            val intent = Intent(context, CountryListActivity::class.java)
            intent.putExtra(CountryListActivity.KEY_STARTED_MANUALLY, true)
            return intent
        }
    }
}
