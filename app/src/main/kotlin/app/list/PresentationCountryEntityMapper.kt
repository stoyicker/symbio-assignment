package app.list

import domain.country.Country

/**
 * Entity mapper between layers.
 */
internal class PresentationCountryEntityMapper {
    /**
     * Guess what :D
     */
    fun transform(model: Country) = model.apply {
        PresentationCountry(name = name,
                nativeName = nativeName,
                region = region,
                capital = capital,
                area = area,
                languages = languages,
                germanTranslation = germanTranslation,
                flagUrl = flagUrl)
    }
}
