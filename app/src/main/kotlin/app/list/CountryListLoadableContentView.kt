package app.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import org.jorge.assignment.app.R
import util.android.ext.getDimension

/**
 * Wraps UI behavior for the list scenario.
 */
internal class CountryListLoadableContentView(
       val contentView: RecyclerView,
       val errorView: View,
       private val progressView: View,
       private val guideView: View) : LoadableContentView<PresentationCountry> {
    override fun showLoadingLayout() {
        pushInfoArea()
        progressView.visibility = View.VISIBLE
        guideView.visibility = View.INVISIBLE
    }

    override fun hideLoadingLayout() {
        progressView.visibility = View.GONE
    }

    override fun updateContent(actionResult: List<PresentationCountry>) {
        (contentView.adapter as Adapter).addItems(actionResult)
        guideView.visibility = View.VISIBLE
    }

    override fun showErrorLayout() {
        pushInfoArea()
        errorView.visibility = View.VISIBLE
        guideView.visibility = View.INVISIBLE
    }

    override fun hideErrorLayout() {
        errorView.visibility = View.GONE
    }

    private fun pushInfoArea() {
        (contentView.layoutParams as FrameLayout.LayoutParams).bottomMargin =
                contentView.context.getDimension(R.dimen.footer_padding).toInt()
    }
}
