package data.common

import data.countries.CountryEntityMapper
import data.countries.DataCountry
import data.countries.DataLanguage
import domain.entity.Country
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import kotlin.test.assertEquals

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
                flagUrl = "aUrl",
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
                flagUrl = "",
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
                flagUrl = "aUrl",
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
        }
    }
}
