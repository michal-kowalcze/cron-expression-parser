package eu.kowalcze.michal.kotlin.cron.domain.usecase

import eu.kowalcze.michal.kotlin.cron.domain.model.parser.CronExpressionLine
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

internal class ParseCronExpressionUseCaseTest : StringSpec({
    val tested = ParseCronExpressionUseCase(CronExpressionParserService())

    "should not convert line due to parsing problem (regex not matched)"{
        listOf(
            "1",
            "1 2",
            "1 2 2",
            "1 2 4 5",
            "1 2 3 4 5",
        ).forAll { line ->
            // given
            val cronExpressionLine = CronExpressionLine.from(line)

            // when
            val exception = shouldThrow<CronExpressionNotMatched> { tested.parse(cronExpressionLine) }

            // then
            exception.message shouldBe "Provided input: '${line}' does not match a cron expression defined by the regex: (\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(.+)"
        }
    }

    "should create a valid cron expression"{
        listOf(
            "* * * * * a-simple-command",
            "* * * * * a-simple-command with blank arguments",
        ).forAll { line ->
            // given
            val cronExpressionLine = CronExpressionLine.from(line)

            // when
            val expression = tested.parse(cronExpressionLine)

            // then
            expression shouldNotBe null
        }
    }
})