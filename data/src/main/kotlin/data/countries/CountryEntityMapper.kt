package data.countries

import domain.country.Country

/**
 * Entity mapper between layers.
 */
internal class CountryEntityMapper {
    /**
     * Guess what :D
     */
    fun transform(dataModel: DataCountry) = Country("")
}
