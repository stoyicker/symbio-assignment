package data.countries

import domain.country.Country
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.util.Locale

/**
 * Unit tests for the country entity mapper.
 * @see CountryEntityMapper
 */
@RunWith(JUnitPlatform::class)
internal class CountryEntityMapperSpek : SubjectSpek<CountryEntityMapper>({
    subject { CountryEntityMapper() } // <- Specify the test subject

    it ("should transform a happy case") {
        val source = DataCountry(
                languages = listOf(DataLanguage("languageOne")),
                translations = mapOf("de" to "languageOneInGerman"),
                alpha2Code = "alpha2Code",
                name = "aName",
                capital = "aCapital",
                region = "aRegion",
                area = "anArea",
                nativeName = "aNativeName")
        assertEquivalent(source, subject.transform(source))
    }

    it ("should transform an all empty case") {
        val source = DataCountry(
                languages = emptyList(),
                translations = emptyMap(),
                alpha2Code = "",
                name = "",
                capital = "",
                region = "",
                area = "",
                nativeName = "")
        assertEquivalent(source, subject.transform(source))
    }

    it ("should transform a mixed case") {
        val source = DataCountry(
                languages = listOf(DataLanguage("languageOne")),
                translations = emptyMap(),
                alpha2Code = "alpha2Code",
                name = "aName",
                capital = "",
                region = "",
                area = "anArea",
                nativeName = "")
        assertEquivalent(source, subject.transform(source))
    }
}) {
    private companion object {
        private fun assertEquivalent(srcModel: DataCountry, targetModel: Country) {
            assertEquals(srcModel.name, targetModel.name)
            assertEquals(srcModel.nativeName, targetModel.nativeName)
            assertEquals(srcModel.region, targetModel.region)
            assertEquals(srcModel.capital, targetModel.capital)
            assertEquals(srcModel.area, targetModel.area)
            assertArrayEquals(srcModel.languages.map { it.name }.toTypedArray(), targetModel.languages)
            assertEquals(srcModel.translations["de"], targetModel.germanTranslation)
            assertTrue(targetModel.flag?.contains(srcModel.alpha2Code.toLowerCase(Locale.ENGLISH)) ?: false) // <- Not the best test, should extract the url and make an exact comparison instead
        }
    }
}
