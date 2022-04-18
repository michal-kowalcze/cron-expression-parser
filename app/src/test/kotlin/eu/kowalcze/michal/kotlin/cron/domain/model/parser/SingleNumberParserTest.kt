package eu.kowalcze.michal.kotlin.cron.domain.model.parser

import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CalendarField
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.random.Random

class SingleNumberParserTest : FunSpec({

    test("should check if value is inside limit") {
        // given
        val upperLimit = Random.nextInt(10, 100)
        val range = IntRange(0, upperLimit)
        val outsideOfRange = upperLimit + 1

        // when
        val exception = shouldThrow<SingleValueOutsideOfLimitException> {
            SingleValueParser.tryParse<CalendarField>(outsideOfRange.toString(), 0, range)
        }

        // then
        exception.message shouldBe "Provided value: '${outsideOfRange}' at index:0 is not within limit: $range"
    }
})
