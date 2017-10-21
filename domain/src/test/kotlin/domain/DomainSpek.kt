package domain

import com.nhaarman.mockito_kotlin.mock
import domain.repository.DomainTopPostsFacade
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import kotlin.test.assertEquals

/**
 * Unit tests for the Domain holder
 */
@RunWith(JUnitPlatform::class)
internal class DomainSpek : SubjectSpek<Domain>({
    subject { Domain } // <- Test subject is the singleton instance

    it ("should hold the provided top posts countryListFacade") {
        val expectedFacade = mock<DomainTopPostsFacade>()
        Domain.countryListFacade(expectedFacade)
        assertEquals(expectedFacade, Domain.countryListFacade, "Top posts countryListFacade not held.")
    }
})
