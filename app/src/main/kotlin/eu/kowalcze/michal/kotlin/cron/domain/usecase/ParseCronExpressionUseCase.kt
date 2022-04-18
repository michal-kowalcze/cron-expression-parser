package eu.kowalcze.michal.kotlin.cron.domain.usecase

import eu.kowalcze.michal.kotlin.cron.domain.model.cronexpression.CronExpression
import eu.kowalcze.michal.kotlin.cron.domain.model.parser.CronExpressionLine
import eu.kowalcze.michal.kotlin.cron.domain.model.parser.CronExpressionParserService
import eu.kowalcze.michal.kotlin.cron.domain.model.parser.ParserInput
import eu.kowalcze.michal.kotlin.cron.logger

class ParseCronExpressionUseCase(
    private val cronExpressionParserService: CronExpressionParserService,
) {
    fun parse(input: CronExpressionLine): CronExpression {
        logger.debug("Processing: {}", input)

        val parserInput = prepareInput(input)
        return cronExpressionParserService.parse(parserInput)
    }

    private fun prepareInput(input: CronExpressionLine): ParserInput {
        val match = CRON_EXPRESSION_FIELDS.matchEntire(input.value)
            ?: throw CronExpressionNotMatched(input)

        return ParserInput(
            minute = match.groupValues[1],
            hour = match.groupValues[2],
            dayOfMonth = match.groupValues[3],
            month = match.groupValues[4],
            dayOfWeek = match.groupValues[5],
            command = match.groupValues[6],
        )
    }

    companion object {
        private val logger by logger()
    }
}

private val CRON_EXPRESSION_FIELDS = Regex("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(.+)")

class CronExpressionNotMatched(line: CronExpressionLine) :
    IllegalArgumentException("Provided input: '${line.value}' does not match a cron expression defined by the regex: $CRON_EXPRESSION_FIELDS")