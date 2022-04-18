package eu.kowalcze.michal.kotlin.cron.domain.model.parser

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class CronExpressionParserServiceTest : StringSpec({
    val tested = CronExpressionParserService()

    "should not convert line due to parsing problem (regex not matched)"{
        listOf(
            "1",
            "1 2",
            "1 2 2",
            "1 2 4 5",
            "1 2 3 4 5",
        ).forAll { line ->
            // given
            val cronExpressionLine = CronExpressionLine(line)

            // when
            val exception = shouldThrow<CronExpressionNotMatched> { tested.parse(cronExpressionLine) }

            // then
            exception.message shouldBe "Provided input: '${line}' does not match a cron expression defined by the regex: (\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(.+)"
        }
    }

    "should not convert line due to parsing problem (pattern not supported)"{
        listOf(
            "1 2 3 4 *-/1 command" to "*-/1",
        ).forAll { (line, failingValue) ->
            // given
            val cronExpressionLine = CronExpressionLine(line)

            // when
            val exception = shouldThrow<FieldPatternNotMatched> { tested.parse(cronExpressionLine) }

            // then
            exception.message shouldBe "Provided value: '${failingValue}' at index:5 is not recognizable by any known parser"
        }
    }

    "should not convert line due to parsing problem (value outside of range)"{
        listOf(
            "66 * * * * command" to "Provided value: '66' at index:1 is not within limit: 0..59",
            "* 24 * * * command" to "Provided value: '24' at index:2 is not within limit: 0..23",
            "* * 32 * * command" to "Provided value: '32' at index:3 is not within limit: 1..31",
            "* * * 0 * command" to "Provided value: '0' at index:4 is not within limit: 1..12",
            "* * * * 9 command" to "Provided value: '9' at index:5 is not within limit: 1..7",
        ).forAll { (line, expectedMessage) ->
            // given
            val cronExpressionLine = CronExpressionLine(line)

            // when
            val exception = shouldThrow<SingleValueOutsideOfLimitException> { tested.parse(cronExpressionLine) }

            // then
            exception.message shouldBe expectedMessage
        }
    }

    "should not convert line due to parsing problem (range not within range)"{
        listOf(
            "30-70 * * * * command" to "Provided value: '30-70' at index:1 is not within limit: 0..59",
            "* 1-25 * * * command" to "Provided value: '1-25' at index:2 is not within limit: 0..23",
            "* * 0-40 * * command" to "Provided value: '0-40' at index:3 is not within limit: 1..31",
            "* * * 0-1 * command" to "Provided value: '0-1' at index:4 is not within limit: 1..12",
            "* * * * 7-9 command" to "Provided value: '7-9' at index:5 is not within limit: 1..7",
        ).forAll { (line, expectedMessage) ->
            // given
            val cronExpressionLine = CronExpressionLine(line)

            // when
            val exception = shouldThrow<RangeOutOfLimitException> { tested.parse(cronExpressionLine) }

            // then
            exception.message shouldBe expectedMessage
        }
    }

    "should create a valid cron expression"{
        listOf(
            "* * * * * a-simple-command",
            "* * * * * a-simple-command with blank arguments",
            "* 1 2 * * single-values-command",
            "* 1 2 3-4 * range-command",
        ).forAll { line ->
            // given
            val cronExpressionLine = CronExpressionLine(line)

            // when
            val expression = tested.parse(cronExpressionLine)

            // then
            expression shouldNotBe null
        }
    }
})