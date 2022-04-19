package eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression

import eu.kowalcze.michal.kotlin.cron.utils.randomCalendarField
import eu.kowalcze.michal.kotlin.cron.utils.randomValueForAnyField
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class PossibleValuesFieldPatternTest : FunSpec({

    test("should not create an empty instance") {
        // expect
        val exception = shouldThrow<NoPatternsProvidedException> {
            possibleValues { }
        }

        // then
        exception.message shouldBe "PossibleValuesFieldPattern requires at least one pattern"
    }

    test("should match if any match") {
        // given
        val possibleValuesFieldPattern = possibleValues {
            single(randomValueForAnyField())
            any()
        }
        val calendarField = randomCalendarField { }

        // when
        val matched = possibleValuesFieldPattern.isMatched(calendarField)

        // then
        matched shouldBe true
    }

    test("should not match if none matched") {
        // given
        val givenValue1 = randomValueForAnyField()
        val givenValue2 = randomValueForAnyField()
        val possibleValuesFieldPattern = possibleValues {
            single(givenValue1)
            single(givenValue2)
        }
        val calendarField = randomCalendarField {
            val valueDifferentThanGiven = givenValue1 + givenValue2
            value = valueDifferentThanGiven
        }

        // when
        val matched = possibleValuesFieldPattern.isMatched(calendarField)

        // then
        matched shouldBe false
    }

    test("should not wrap a single field") {
        // given
        val singlePattern = SingleValueFieldPattern<CalendarField>(randomValueForAnyField())

        // when
        val notWrappedPattern = PossibleValuesFieldPattern.optionalWrapWithPossibleValues(listOf(singlePattern))

        // then
        notWrappedPattern shouldBeSameInstanceAs singlePattern
    }
})

private fun possibleValues(
    customization: PossibleValuesConfiguration.() -> Unit
): PossibleValuesFieldPattern<CalendarField> {
    val configuration = PossibleValuesConfiguration().apply(customization)
    return PossibleValuesFieldPattern(
        patterns = configuration.patterns,
    )
}

private class PossibleValuesConfiguration {
    val patterns = mutableListOf<CalendarFieldPattern<CalendarField>>()

    fun any() {
        patterns.add(AnyValueFieldPattern())
    }

    fun single(value: Int = randomValueForAnyField()) {
        patterns.add(SingleValueFieldPattern(value))
    }
}
