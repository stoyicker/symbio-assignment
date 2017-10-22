package app.list

import domain.country.Country

/**
 * Entity mapper between layers.
 */
internal class PresentationCountryEntityMapper {
    /**
     * Guess what :D
     */
    fun transform(model: Country) = PresentationCountry("")
}
