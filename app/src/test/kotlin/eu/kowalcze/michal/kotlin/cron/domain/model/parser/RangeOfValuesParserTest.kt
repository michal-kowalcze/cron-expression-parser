package eu.kowalcze.michal.kotlin.cron.domain.model.parser

import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CalendarField
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class RangeOfValuesParserTest : StringSpec({

    "should not parse invalid values"{
        listOf(
            "1",
            "-2",
            "1-",
            " 1-2 ",
        ).forAll { value ->
            // when
            val result = RangeOfValuesParser.tryParse<CalendarField>(value, 1, 1..10)

            // then
            result shouldBe null
        }
    }

    "should fail when range is empty"{
        // given
        val value = "11-1"

        // when
        val exception = shouldThrow<EmptyRangeException> {
            RangeOfValuesParser.tryParse<CalendarField>(value, 1, 1..10)
        }

        // then
        exception.message shouldBe "Provided value: '11-1' at index:1 defines an empty range"
    }
    "should fail when range is outside of limit"{
        // given
        val value = "1-2"

        // when
        val exception = shouldThrow<RangeOutOfLimitException> {
            RangeOfValuesParser.tryParse<CalendarField>(value, 1, 2..10)
        }

        // then
        exception.message shouldBe "Provided value: '1-2' at index:1 is not within limit: 2..10"
    }
})
