package eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression

import eu.kowalcze.michal.kotlin.cron.utils.randomCalendarField
import eu.kowalcze.michal.kotlin.cron.utils.randomValueForAnyField
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SingleNumberFieldPatternTest : FunSpec({

    test("should match exact value") {
        // given
        val givenValue = randomValueForAnyField()
        val givenField = randomCalendarField {
            value = givenValue
        }
        val singleValueFieldPattern = SingleValueFieldPattern<CalendarField>(value = givenValue)

        // when
        val matched = singleValueFieldPattern.isMatched(givenField)

        // then
        matched shouldBe true
    }

    test("should not match a different value") {
        // given
        val givenField = randomCalendarField {
        }
        val valueDifferentThanGiven = givenField.value + randomValueForAnyField()
        val singleValueFieldPattern = SingleValueFieldPattern<CalendarField>(value = valueDifferentThanGiven)

        // when
        val matched = singleValueFieldPattern.isMatched(givenField)

        // then
        matched shouldBe false
    }
})
