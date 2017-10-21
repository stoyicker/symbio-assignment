package data

import data.countries.CountryListFacadeComponent
import data.countries.CountryListRequestSourceComponent

/**
 * A holder for dependency injectors.
 */
internal object ComponentHolder {
    lateinit var topPostsFacadeComponent: CountryListFacadeComponent
    lateinit var countryListRequestSourceComponent: CountryListRequestSourceComponent
}
