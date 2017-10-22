package data.countries

import domain.country.Country
import java.util.Locale

/**
 * Entity mapper between layers.
 */
internal class CountryEntityMapper {
    /**
     * Guess what :D
     */
    fun transform(model: DataCountry) = model.let {
        Country(name = it.name,
                nativeName = it.nativeName,
                region = it.region,
                capital = it.capital,
                area = it.area,
                languages = it.languages.map { it.name }.toTypedArray(),
                germanTranslation = it.translations["de"],
                // Our API only points to images in SVG, which is not natively supported in Android.
                // Therefore, there are two choices:
                // 1. Put together some adapter of sorts, using Glide or raw download and bitmap creation,
                // to get a format Android can understand or
                // 2. put together an arguably dirtier but much quicker solution - pull the ready-to-use
                // files from somewhere else.
                flag = "https://raw.githubusercontent.com/hjnilsson/country-flags/26b79b8730b39eeaa47194a2322ece4947329df7/png250px/${it.alpha2Code.toLowerCase(Locale.ENGLISH)}.png")
    }
}
