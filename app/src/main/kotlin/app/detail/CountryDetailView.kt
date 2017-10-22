package app.detail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import android.widget.TextView
import app.list.PresentationCountry
import com.squareup.picasso.Picasso

/**
 * Wraps UI behavior for the list scenario. Class is only open for testing purposes.
 */
internal open class CountryDetailView(
        private val nameView: TextView,
        private val imageView: ImageView,
        private val detailView: TextView) : DetailView<PresentationCountry> {
    override fun updateContent(item: PresentationCountry) {
        item.apply {
            name.let {
                nameView.text = "$it ($nativeName)"
                imageView.contentDescription = it
            }
            Picasso.with(imageView.context)
                    .load(flagUrl)
                    // Trick for correct image placement
                    .placeholder(ColorDrawable(Color.TRANSPARENT))
                    .fit()
                    .centerInside()
                    .into(imageView)
            detailView.text = """
                Region: $region
                Capital: $capital
                Area: ${area ?: "N/A"}
                Languages: ${languages.joinToString(separator = ", ")}
                German name: $germanTranslation"""
        }
    }
}
