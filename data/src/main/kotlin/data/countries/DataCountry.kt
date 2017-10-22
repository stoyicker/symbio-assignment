package data.countries

import com.squareup.moshi.Json

/**
 * A country as the data layer sees it.
 */
internal class DataCountry(
        @Json(name = "languages") val languages: List<DataLanguage>,
        @Json(name = "translations") val translations: Map<String, String>,
        @Json(name = "flag") val flagUrl: String?,
        @Json(name = "name") val name: String,
        @Json(name = "capital") val capital: String,
        @Json(name = "region") val region: String,
        @Json(name = "area") val area: String?,
        @Json(name = "nativeName") val nativeName: String)

/**
 * The relevant part of a language.
 */
internal class DataLanguage(@Json(name = "name") val name: String)
