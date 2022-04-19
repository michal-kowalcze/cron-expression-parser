package eu.kowalcze.michal.kotlin.cron.domain.model.parser

import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CalendarField
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class AnyWithStepParserTest : StringSpec({

    "should parse a proper value"  {
        // given
        val value = "*/3"
        val limit = 1..10

        // when
        val pattern = AnyWithStepParser.tryParse<CalendarField>(value, 1, limit)

        // then
        pattern shouldNotBe null
    }

    "should not parse invalid values"{
        listOf(
            "*-1",
            "*/-1",
            "1/*",
            " */1 ",
        ).forAll { value ->
            // given
            val limit = 1..10

            // when
            val pattern = AnyWithStepParser.tryParse<CalendarField>(value, 1, limit)

            // then
            pattern shouldBe null
        }
    }
})
