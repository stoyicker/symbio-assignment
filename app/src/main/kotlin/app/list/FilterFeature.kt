package app.list

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import org.jorge.assignment.app.R
import util.android.HtmlCompat

/**
 * Contains boilerplate for list filtering.
 */
internal class FilterFeature(
        activity: Activity,
        private val searchView: SearchView,
        private val target: CountryListViewConfig) {
    internal var query: CharSequence = ""
        private set

    init {
        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false

                override fun onQueryTextChange(newText: String?): Boolean {
                    this@FilterFeature.query = newText ?: ""
                    target.filterView(query)
                    return true
                }
            })
            setSearchableInfo((context.getSystemService(Context.SEARCH_SERVICE) as SearchManager)
                    .getSearchableInfo(activity.componentName))
            setIconifiedByDefault(false)
            queryHint = HtmlCompat.fromHtml(context.getString(R.string.search_view_hint))
        }
        // Neat trick so your keystrokes get sent directly to the search field even if not focused
        activity.setDefaultKeyMode(AppCompatActivity.DEFAULT_KEYS_SEARCH_LOCAL)
    }

    /**
     * Delegates a query to the query handler in order to filter the list.
     * @param newQuery The query.
     */
    internal fun applyQuery(newQuery: CharSequence?) {
        searchView.setQuery(newQuery, false)
    }
}
