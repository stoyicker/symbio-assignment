package app.list

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log.i
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.list_item.view.flag
import kotlinx.android.synthetic.main.list_item.view.name
import org.jorge.assignment.app.BuildConfig
import org.jorge.assignment.app.R

/**
 * Configuration for the recycler view holding the list.
 */
internal class CountryListViewConfig(
        view: CountryListLoadableContentView,
        private val callback: CountryListViewConfig.InteractionCallback) {
    private val adapter: Adapter = adapter(callback)

    /**
     * Dumps itself onto the injected view.
     */
    init {
        view.contentView.let { recyclerView ->
            recyclerView.adapter = adapter
            recyclerView.addOnScrollListener(endlessLoadListener(recyclerView.layoutManager))
            recyclerView.setHasFixedSize(false)
        }
        view.errorView.setOnClickListener { callback.onPageLoadRequested() }
    }

    /**
     * Requests a filtering command to be performed.
     * @param constraint The constraint for the filtering action.
     */
    fun filterView(constraint: CharSequence?) {
        adapter.filter.filter(constraint, null)
    }

    /**
     * Returns an adapter with stable ids that reports user interactions to the provided callback.
     * @return An adapter with stable ids that reports user interactions to the provided callback.
     */
    private fun adapter(callback: CountryListViewConfig.InteractionCallback) =
            Adapter(callback).also { it.setHasStableIds(true) }

    /**
     * Provides support for the user interaction that requests loading additional items.
     *
     */
    private fun endlessLoadListener(layoutManager: RecyclerView.LayoutManager) =
            object : EndlessLoadListener(layoutManager) {
                override fun onLoadMore() {
                    callback.onPageLoadRequested()
                }
    }

    /**
     * An interface for the view to communicate back with the activity.
     */
    interface InteractionCallback {
        /**
         * To be called when an item click happens.
         * @param item The item clicked.
         */
        fun onItemClicked(item: PresentationCountry)

        /**
         * To be called when a page load is requested.
         */
        fun onPageLoadRequested()
    }
}

/**
 * A very simple adapter backed by a mutable list that exposes a method to add items.
 * An alternative would have been to use the databinding library, but the fact that it does not
 * support merge layouts would make diverse screen support more complicated.
 */
internal class Adapter(private val callback: CountryListViewConfig.InteractionCallback)
    : RecyclerView.Adapter<Adapter.ViewHolder>(), Filterable {
    private var items = listOf<PresentationCountry>()
    private var shownItems = emptyList<PresentationCountry>()
    private lateinit var recyclerView: RecyclerView
    private val filter = RepeatableFilter()

    override fun onAttachedToRecyclerView(target: RecyclerView) {
        recyclerView = target
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.list_item, parent, false), { callback.onItemClicked(it) })

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(shownItems[position])
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }
        // This is used to take the latest valid value in the given payload list
        val fold: (Bundle, String) -> Unit = { bundle, key ->
            @Suppress("UNCHECKED_CAST")
            bundle.putString(key, (payloads as List<Bundle>).fold(Bundle(), { old, new ->
                val oldTitle = old.getString(key)
                bundle.putString(key, new.getString(key) ?: oldTitle)
                new
            }).getString(key))
        }
        val combinedBundle = Bundle().also { bundle ->
            arrayOf(KEY_NAME, KEY_FLAG_URL).forEach {
                fold(bundle, it)
            }
        }
        // Now combinedBundle contains the latest version of each of the fields that can be updated
        // individually
        holder.renderPartial(combinedBundle, shownItems[position])
    }

    override fun getItemCount(): Int = shownItems.size

    /**
     * This implementation is a bit 'meh' because of String (the type of the item id, which is
     * what we use to calculate the item hash code) being a bigger type than Long, the required one.
     */
    override fun getItemId(position: Int): Long = shownItems[position].hashCode().toLong()

    /**
     * Requests a list of items to be added to this adapter. This call re-applies the current
     * filter, which means some of the items passed in to be added will not be shown they don't meet
     * the current filter.
     * @param toAdd The items to add.
     */
    fun addItems(toAdd: List<PresentationCountry>) {
        // If the list is empty we have tried to load a non-existent page, which means we already
        // have all pages, so there is nothing to add.
        if (toAdd.isNotEmpty()) {
            items = items.plus(toAdd).distinct()
            filter.refresh()
        }
    }

    override fun getFilter() = filter

    /**
     * A filter that keeps track of its last query for repetition.
     */
    inner class RepeatableFilter : Filter() {
        private var currentQuery: CharSequence = ""
        private lateinit var diff: DiffUtil.DiffResult

        override fun performFiltering(constraint: CharSequence?): FilterResults? {
            currentQuery = constraint?.trim() ?: ""
            val filteredItems = if (currentQuery.isBlank()) {
                items
            } else {
                items.filter { it.name.contains(currentQuery, true) }
            }
            diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize() = shownItems.size

                override fun getNewListSize() = filteredItems.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                        shownItems[oldItemPosition].let { (oldId) ->
                            filteredItems[newItemPosition].let { (newId) ->
                                oldId.contentEquals(newId)
                            }
                        }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                        shownItems[oldItemPosition].let { oldItem ->
                            filteredItems[newItemPosition].let { newItem -> oldItem == newItem }
                        }

                override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int) =
                        shownItems[oldItemPosition].let {
                            (oldName, _, _, _, _, _, oldflag) ->
                            filteredItems[newItemPosition].let {
                                (newName, _, _, _, _, _, newflag) ->
                                Bundle().apply {
                                    putString(KEY_NAME, newName.takeIf {
                                        !it.contentEquals(oldName)
                                    })
                                    putString(KEY_FLAG_URL, newflag.takeIf {
                                        it != oldflag
                                    })
                                }
                            }
                        }
            })
            return FilterResults().also {
                it.values = filteredItems
                it.count = filteredItems.size
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            @Suppress("UNCHECKED_CAST")
            shownItems = results?.values as List<PresentationCountry>? ?: items
            diff.dispatchUpdatesTo(this@Adapter)
            if (!recyclerView.canScrollVertically(1)) {
                callback.onPageLoadRequested()
            }
        }

        /**
         * Queues a new filtering action with the last query.
         */
        fun refresh() = filter(currentQuery)
    }

    /**
     * Very simple viewholder that sets text and click event handling.
     * @param itemView The view to dump the held data onto.
     * @param onItemClicked What to run when a click happens.
     */
    internal class ViewHolder(
            itemView: View,
            private val onItemClicked: (PresentationCountry) -> Unit)
        : RecyclerView.ViewHolder(itemView), Target {

        /**
         * Draw an item.
         * @title The item to draw.
         */
        fun render(item: PresentationCountry) {
            setName(item.name)
            setFlag(item.flag)
            itemView.setOnClickListener { onItemClicked(item) }
        }

        /**
         * Performs partial re-drawing of an item.
         * @param bundle The updates that need to be drawn.
         * @param item The item these updates correspond to.
         */
        fun renderPartial(bundle: Bundle, item: PresentationCountry) {
            bundle.getString(KEY_NAME)?.let { setName(it) }
            setFlag(bundle.getString(KEY_FLAG_URL))
            itemView.setOnClickListener { onItemClicked(item) }
        }

        /**
         * Updates the layout according to the changes required by a new title.
         * @param title The new title.
         */
        private fun setName(title: String) {
            itemView.name.text = title
            itemView.flag.contentDescription = title
        }

        /**
         * Updates the layout according to the changes required by a new flag link.
         * @param flagLink The new flag link, or <code>null</code> if none is applicable.
         */
        private fun setFlag(flagLink: String?) {
            itemView.flag.let {
                Picasso.with(it.context).load(flagLink).into(this)
            }
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            itemView.flag.visibility = View.GONE
            itemView.flag.setImageDrawable(null)
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) { }

        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            itemView.flag.setImageBitmap(bitmap)
            itemView.flag.visibility = View.VISIBLE
        }
    }
}

private const val KEY_NAME = "org.jorge.assignment.app.KEY_NAME"
private const val KEY_FLAG_URL = "org.jorge.assignment.app.KEY_FLAG_URL"

/**
 * @see <a href="https://gist.githubusercontent.com/nesquena/d09dc68ff07e845cc622/raw/e2429b173f75afb408b420ad4088fed68240334c/EndlessRecyclerViewScrollListener.java">Adapted from CodePath</a>
 */
internal abstract class EndlessLoadListener(
        private val layoutManager: RecyclerView.LayoutManager) : RecyclerView.OnScrollListener() {
    private var loading = true
    private var previousTotalItemCount = 0

    override fun onScrolled(view: RecyclerView?, dx: Int, dy: Int) {
        val lastVisibleItemPosition = findLastVisibleItemPosition()
        val totalItemCount = layoutManager.itemCount
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false
            previousTotalItemCount = totalItemCount
        }
        if (!loading && lastVisibleItemPosition == totalItemCount - 1) {
            loading = true
            onLoadMore()
        }
    }

    /**
     * Independent of the layout manager in use.
     */
    private fun findLastVisibleItemPosition() =
        when (layoutManager) {
            is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
            is StaggeredGridLayoutManager -> getLastVisibleItem(
                    layoutManager.findLastVisibleItemPositions(null))
            else -> throw IllegalStateException(
                    """Only ${LinearLayoutManager::class.java.name} or
                    ${StaggeredGridLayoutManager::class.java.name}""")
        }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    /**
     * Implement your refresh logic here.
     */
    protected abstract fun onLoadMore()
}
