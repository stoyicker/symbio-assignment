package app.list

import android.os.Parcel
import android.os.Parcelable

/**
 * Altered model for presentation purposes.
 */
@paperparcel.PaperParcel
internal data class PresentationCountry(
        val name: String,
        val nativeName: String,
        val region: String,
        val capital: String,
        val area: String,
        val languages: Array<String>,
        val germanTranslation: String?,
        val flagUrl: String) : Parcelable {
    /**
     * Note that Kotlin data classes compare all attributes on a country. That is not what we want,
     * so we are tuning that a bit both here and in equals(...).
     */
    override fun hashCode() = name.hashCode()

    /**
     * Note that Kotlin data classes compare all attributes on a country. That is not what we want,
     * so we are tuning that a bit both here and in hashCode().
     */
    override fun equals(other: Any?) =
            if (other == null) false else other is PresentationCountry && name == other.name

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelPresentationCountry.writeToParcel(this, dest, flags)
    }

    companion object {
        @Suppress("unused") // Parcelable
        @JvmField val CREATOR = PaperParcelPresentationCountry.CREATOR
    }
}

