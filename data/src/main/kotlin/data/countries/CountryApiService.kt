package data.countries

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET

/**
 * Describes interactions with the API.
 */
internal interface CountryApiService {
    /**
     * Gets the list of countries.
     */
    @GET("${VERSION_TWO}/${METHOD_ALL}/${QUERY_PARAMETERS_SEPARATOR}${QUERY_PARAMETER_FIELDS}")
    fun allCountries(): Single<ResponseBody>

    private companion object {
        const val VERSION_TWO = "v2"
        const val METHOD_ALL = "all"
        const val QUERY_PARAMETERS_SEPARATOR = "?"
        const val QUERY_PARAMETER_FIELDS = "fields=name;nativeName;region;capital;area;languages;translations;flag;alpha2Code"
    }
}
