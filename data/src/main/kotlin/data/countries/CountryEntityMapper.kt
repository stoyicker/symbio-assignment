package data.countries

import domain.country.Country

/**
 * Entity mapper between layers.
 */
internal class CountryEntityMapper {
    /**
     * Guess what :D
     */
    fun transform(dataModel: DataCountry) = dataModel.apply {
        Country(name = name,
                nativeName = nativeName,
                region = region,
                capital = capital,
                area = area,
                languages = languages.map { it.name }.toTypedArray(),
                germanTranslation = translations["de"],
                flagUrl = flagUrl)
    }
}
