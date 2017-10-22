package domain.country

/**
 * Models the relevant information about a country, but in a way that modules other than data can
 * see it without knowing about how it is retrieved (deserialized).
 */
class Country(
        val name: String,
        val nativeName: String,
        val region: String,
        val capital: String,
        val area: String,
        val languages: Array<String>,
        val germanTranslation: String?,
        val flagUrl: String)
