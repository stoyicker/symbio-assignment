package data.countries

import domain.entity.Country

/**
 * Entity mapper between layers.
 */
internal class CountryEntityMapper {
    /**
     * Guess what :D
     */
    fun transform(dataModel: DataCountry) = Country("")
}
