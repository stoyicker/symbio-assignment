package data

import data.countries.CountryListFacadeComponent
import data.countries.CountryListRequestSourceComponent

/**
 * A holder for dependency injectors.
 */
internal object ComponentHolder {
    lateinit var countryListFacadeComponent: CountryListFacadeComponent
    lateinit var countryListRequestSourceComponent: CountryListRequestSourceComponent
}
