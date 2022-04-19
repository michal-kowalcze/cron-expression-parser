package eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression

import eu.kowalcze.michal.kotlin.cron.utils.randomCalendarField
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AnyValueFieldPatternTest : FunSpec({

    val anyValueFieldPattern = AnyValueFieldPattern<CalendarField>()

    test("should always match a value") {
        // given
        val givenField = randomCalendarField {}

        // when
        val matched = anyValueFieldPattern.isMatched(givenField)

        // then
        matched shouldBe true
    }
})
