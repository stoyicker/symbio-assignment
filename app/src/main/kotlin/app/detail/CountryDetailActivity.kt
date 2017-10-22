package app.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import app.MainApplication
import app.list.PresentationCountry
import kotlinx.android.synthetic.main.include_detail_view.detail
import kotlinx.android.synthetic.main.include_detail_view.flag
import kotlinx.android.synthetic.main.include_detail_view.name
import kotlinx.android.synthetic.main.include_toolbar.toolbar
import org.jorge.assignment.app.R
import javax.inject.Inject

/**
 * Activity that shows a model in detail.
 */
internal class CountryDetailActivity : AppCompatActivity() {
    @Inject
    lateinit var view: CountryDetailView

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)
        configureToolbar()
        inject()
        render()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Injects this instance with the corresponding feature component.
     */
    private fun inject() {
        // https://kotlinlang.org/docs/tutorials/android-plugin.html#using-kotlin-android-extensions
        (application as MainApplication).buildCountryDetailComponent(name, flag, detail)
                .inject(this)
    }

    /**
     * Requests the details of the given model to be shown.
     */
    private fun render() {
        intent?.getParcelableExtra<PresentationCountry>(KEY_MODEL)?.let { view.updateContent(it) }
    }

    companion object {
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        const val KEY_MODEL = "KEY_MODEL"

        /**
         * Safe way to obtain an intent to route to this activity. More useful if it were to have
         * more parameters for example, but a good idea to have nevertheless.
         * @param context The context to start this activity from.
         */
        fun getCallingIntent(context: Context, model: PresentationCountry): Intent {
            val intent = Intent(context, CountryDetailActivity::class.java)
            intent.putExtra(KEY_MODEL, model)
            return intent
        }
    }
}
