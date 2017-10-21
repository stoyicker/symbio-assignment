package app.detail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import android.widget.TextView
import app.common.PresentationCountry
import com.squareup.picasso.Picasso

/**
 * Wraps UI behavior for top all time gaming posts scenario. Class is only open for testing
 * purposes.
 */
internal open class CountryDetailView(
        private val textView: TextView,
        private val imageView: ImageView) : TextAndImageView<PresentationCountry> {
    override fun updateContent(item: PresentationCountry) {
        item.name.let {
            textView.text = it
            imageView.contentDescription = it
        }
        Picasso.with(imageView.context)
                .load(item.name)
                // Trick for correct image placement
                .placeholder(ColorDrawable(Color.TRANSPARENT))
                .fit()
                .centerInside()
                .into(imageView)
    }
}
