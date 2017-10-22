package data.countries

import domain.country.Country

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
                flag = it.flag)
    }
}
