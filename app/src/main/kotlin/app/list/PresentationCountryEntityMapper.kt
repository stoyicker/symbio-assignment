package app.list

import domain.country.Country

/**
 * Entity mapper between layers.
 */
internal class PresentationCountryEntityMapper {
    /**
     * Guess what :D
     */
    fun transform(model: Country) = model.let {
        PresentationCountry(name = it.name,
                nativeName = it.nativeName,
                region = it.region,
                capital = it.capital,
                area = it.area,
                languages = it.languages,
                germanTranslation = it.germanTranslation,
                flag = it.flag)
    }
}
