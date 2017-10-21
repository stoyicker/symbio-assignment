package app.list

import domain.entity.Country

/**
 * Entity mapper between layers.
 */
internal class PresentationCountryEntityMapper {
    /**
     * Guess what :D
     */
    fun transform(model: Country) = PresentationCountry("")
}
